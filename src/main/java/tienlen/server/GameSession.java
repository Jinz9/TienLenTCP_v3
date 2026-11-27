package tienlen.server;

import tienlen.logic.GameLogic;
import tienlen.model.Card;
import tienlen.model.Deck;
import tienlen.model.Message;
import tienlen.model.Move;
import tienlen.model.Player;
import tienlen.utils.Protocol;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameSession {
    private static final int TURN_TIMEOUT_SECONDS = 30;
    private final String sessionId;
    private String displayName = "";
    private final List<Player> players = new ArrayList<>();
    private int currentTurnIndex = 0;
    private Move lastMove = null;
    private final int MAX_PLAYERS = 4;
    private final Set<Player> passedPlayers = new HashSet<>(); // Người đã PASS vòng này
    private Map<Player, ClientHandler> connections = new HashMap<>();
    private int winnerIndex = -1;
    private boolean requireThreeSpades = false;
    private long betAmount = 10000; // Số tiền cược mỗi ván (10k VND)
    private long totalPot = 0; // Tổng tiền trong pot
    private long forfeitedPot = 0; // Tiền thu từ người thoát giữa ván
    private final ScheduledExecutorService turnTimerExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "TurnTimer-" + UUID.randomUUID().toString().substring(0, 6));
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> turnTimeoutTask;
    private ServerBackend backend;

    public void setBackend(ServerBackend backend) {
        this.backend = backend;
    }
    
    private boolean gameRunning = false; 

    public synchronized boolean isGameRunning() {
        return gameRunning;
    }

    private synchronized void setGameRunning(boolean running) {
        gameRunning = running;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(long amount) {
        this.betAmount = amount;
    }

    public long getTotalPot() {
        return totalPot;
    }

    public synchronized void addToPot(long amount) {
        this.totalPot += amount;
    }

    public synchronized void resetPot() {
        this.totalPot = 0;
    }

    public synchronized boolean addPlayer(Player player, ClientHandler handler) {
        if (player == null || player.getName() == null || player.getName().trim().isEmpty()) {
            return false;
        }
        String candidateName = player.getName().trim();
        for (Player existing : players) {
            if (existing.getName() != null && existing.getName().trim().equalsIgnoreCase(candidateName)) {
                if (handler != null) {
                    handler.sendMessage(Protocol.encode(new Message(
                            "ERROR",
                            "Tài khoản này đã có mặt trong bàn chơi!"
                    )));
                }
                return false;
            }
        }
        if (players.size() >= MAX_PLAYERS || gameRunning) {
            if (handler != null) {
                handler.sendMessage(Protocol.encode(new Message(
                        "ERROR",
                        "Bàn chơi đã đầy hoặc đang diễn ra!"
                )));
            }
            return false;
        }

        // Load balance từ database và kiểm tra đủ tiền cược tối thiểu trước khi cho vào bàn
        UserManager userManager = UserManager.getInstance();
        long balance = userManager.getBalance(player.getName());
        if (balance < betAmount) {
            if (handler != null) {
                handler.sendMessage(Protocol.encode(new Message(
                        Protocol.INSUFFICIENT_BALANCE,
                        "Số dư của bạn (" + String.format("%,d", balance) + " VND) không đủ để vào bàn cược "
                                + String.format("%,d", betAmount) + " VND!"
                )));
            }
            return false;
        }

        players.add(player);
        connections.put(player, handler);
        player.setBalance(balance);
        
        broadcastPlayerList();
        return true;
    }
    public void broadcastPlayerList() {
        List<String> playerInfos = new ArrayList<>();
        for (Player p : players) {
            playerInfos.add(p.getName() + ":" + p.getBalance());
        }
        Message msg = new Message("PLAYER_LIST", String.join(",", playerInfos));
        for (Player p : players) {
            connections.get(p).sendMessage(Protocol.encode(msg));
        }
    }

    public GameSession() {
        this.sessionId = UUID.randomUUID().toString();
        // Save session to database
        UserManager userManager = UserManager.getInstance();
        userManager.createGameSession(this.sessionId, this.displayName, this.betAmount);
    }

    // Constructor with custom display name and bet amount
    public GameSession(String displayName, long betAmount) {
        this.sessionId = UUID.randomUUID().toString();
        this.displayName = displayName;
        this.betAmount = betAmount;
        // Save session to database
        UserManager userManager = UserManager.getInstance();
        userManager.createGameSession(this.sessionId, this.displayName, this.betAmount);
    }


    public GameSession(String displayName) {
        this.sessionId = UUID.randomUUID().toString();
        this.displayName = displayName;
        // Save session to database
        UserManager userManager = UserManager.getInstance();
        userManager.createGameSession(this.sessionId, this.displayName, this.betAmount);
    }

    // Constructor để load session từ database (không save lại)
    public GameSession(String sessionId, String displayName, long betAmount) {
        this.sessionId = sessionId;
        this.displayName = displayName;
        this.betAmount = betAmount;
        // Session đã tồn tại trong database, không cần save lại
    }

    public String getDisplayName() {
        return displayName == null || displayName.isEmpty() ? ("Bàn " + sessionId.substring(0, 6)) : displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    
    public Move getLastMove() {
		return lastMove;
	}

	public void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}

	public int getCurrentTurnIndex() {
		return currentTurnIndex;
	}

	public Set<Player> getPassedPlayers() {
		return passedPlayers;
	}

    public List<Player> getPlayers() {
        return players;
    }

    public void startGame() {
    	if (gameRunning) return;
      	if (players.size() < 2) return;

        UserManager userManager = UserManager.getInstance();
        for (Player p : players) {
            long latestBalance = userManager.getBalance(p.getName());
            p.setBalance(latestBalance);
            if (latestBalance < betAmount) {
                Message msg = new Message(
                        "CHAT",
                        "⚠️ Không thể bắt đầu ván mới: " + p.getName() + " không đủ tiền cược ("
                                + String.format("%,d", betAmount) + " VND)!"
                );
                for (Player target : players) {
                    ClientHandler ch = connections.get(target);
                    if (ch != null) {
                        ch.sendMessage(Protocol.encode(msg));
                    }
                }
                return;
            }
        }

      	gameRunning = true;
      	
      	// Update session status to PLAYING in database
      	userManager.updateSessionStatus(sessionId, "PLAYING");
      	
    	lastMove = null;
        passedPlayers.clear();
    	for (Player p : players) {
            p.getHand().clear();
            p.setPlaying(true);
        }
        Deck deck = new Deck();
        deck.shuffle();

        List<List<Card>> hands = deck.deal(players.size());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).addCards(hands.get(i));
        }
        //gửi bài cho từng người chơi
        for (Player player : players) {
            ClientHandler ch = connections.get(player);
            if (ch != null) {
                ch.sendHand(player.getHand());
            }
        }

        requireThreeSpades = false;
        if (winnerIndex >= 0 && winnerIndex < players.size()) {
            currentTurnIndex = winnerIndex;
        } else {
            winnerIndex = -1;
            currentTurnIndex = 0;
            for (int i = 0; i < players.size(); i++) {
                for (Card c : players.get(i).getHand()) {
                    if (c.getRank() == 3 && c.getSuit() == Card.Suit.SPADES) {
                        currentTurnIndex = i;
                        requireThreeSpades = true;
                        break;
                    }
                }
                if (requireThreeSpades) break;
            }
        }
        broadcastTurn();
        if (backend != null) {
            backend.broadcastSessionListToLobby();
        }
    }
  

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

    public void nextTurn() {
    	  do {
              currentTurnIndex = (currentTurnIndex + 1) % players.size();
          } while (passedPlayers.contains(players.get(currentTurnIndex)) || !players.get(currentTurnIndex).isPlaying());
    }

    public synchronized void handlePass(Player player) {
        if (lastMove == null) {
            ClientHandler ch = connections.get(player);
            if (ch != null) {
                ch.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Không thể bỏ lượt khi bắt đầu vòng mới!")));
            }
            return;
        }
        passedPlayers.add(player);
        nextTurn();
        // Nếu tất cả (trừ 1) đều PASS → reset vòng
        if (passedPlayers.size() >= players.size() - 1) {
            passedPlayers.clear();
            lastMove = null;
            broadcastMove(player, null);
        } else {
            broadcastTurn();
        }
    }

    public void broadcastMove(Player player, Move move) {
        Message msplay;
        if (move == null) msplay = new Message("PASS", "");
        else msplay = new Message("PLAY", player.getName() + "," + move.toString());
        
        for (Player p : players) {
            ClientHandler ch = connections.get(p);
            if (ch != null) {
                ch.sendMessage(Protocol.encode(msplay));
            }
        }
        broadcastTurn();
        if (backend != null) {
            backend.broadcastSessionListToLobby();
        }
    }

    private void cancelTurnTimer() {
        if (turnTimeoutTask != null) {
            turnTimeoutTask.cancel(false);
            turnTimeoutTask = null;
        }
    }

    private void scheduleTurnTimer() {
        cancelTurnTimer();
        if (!gameRunning || players.size() < 2 || currentTurnIndex < 0 || currentTurnIndex >= players.size()) {
            return;
        }
        final Player expectedPlayer = players.get(currentTurnIndex);
        turnTimeoutTask = turnTimerExecutor.schedule(() -> onTurnTimeout(expectedPlayer), TURN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private synchronized void onTurnTimeout(Player expectedPlayer) {
        if (!gameRunning || players.size() < 2) return;
        if (currentTurnIndex < 0 || currentTurnIndex >= players.size()) return;
        if (!players.get(currentTurnIndex).equals(expectedPlayer)) return;

        ClientHandler handler = connections.get(expectedPlayer);
        if (handler == null) return;

        if (lastMove != null) {
            Message notice = new Message("NOTIFICATION", "⏰ " + expectedPlayer.getName() + " hết thời gian (30s), tự động bỏ lượt.");
            for (Player p : players) {
                ClientHandler ch = connections.get(p);
                if (ch != null) ch.sendMessage(Protocol.encode(notice));
            }
            handlePass(expectedPlayer);
        } else if (!expectedPlayer.getHand().isEmpty()) {
            Card smallest = Collections.min(expectedPlayer.getHand());
            Message notice = new Message("NOTIFICATION", "⏰ " + expectedPlayer.getName() + " hết thời gian (30s), tự động đánh " + smallest + ".");
            for (Player p : players) {
                ClientHandler ch = connections.get(p);
                if (ch != null) ch.sendMessage(Protocol.encode(notice));
            }
            processMove(handler, smallest.toString());
        }
    }

    public void broadcastTurn() {
        if (players.isEmpty() || currentTurnIndex < 0 || currentTurnIndex >= players.size()) return;
        Message msturn = new Message("TURN", players.get(currentTurnIndex).getName());
        for (Player p : players) {
            ClientHandler ch = connections.get(p);
            if (ch != null) {
                ch.sendMessage(Protocol.encode(msturn));
            }
        }
        scheduleTurnTimer();
    }

    public synchronized void processMove(ClientHandler sender, String moveData) {
        if (!gameRunning) {
            sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Ván chơi chưa bắt đầu!")));
            return;
        }
        Player player = sender.getPlayer();
        if (player == null || !players.contains(player)) {
            sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Bạn không ở trong bàn chơi!")));
            return;
        }

        if (!getCurrentPlayer().equals(player)) {
            Message ms = new Message("NOTIFICATION", "ERROR:Chưa đến lượt của bạn!");
            sender.sendMessage(Protocol.encode(ms));
            return;
        }

        if (passedPlayers.contains(player)) {
            sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Bạn đã bỏ lượt ở vòng này!")));
            return;
        }

        if ("PASS".equals(moveData)) {
            handlePass(player);
            System.out.println(player.getName() + " passed.");
            return;
        }

        // Chuyển từ string sang Move
        Move move;
        try {
            move = parseMove(moveData);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Dữ liệu bài không hợp lệ!")));
            return;
        }

        // Kiểm tra bài có thực sự nằm trên tay người chơi và không trùng lặp
        List<Card> handCopy = new ArrayList<>(player.getHand());
        for (Card c : move.getCards()) {
            if (!handCopy.remove(c)) {
                sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Bạn không sở hữu lá bài này trên tay!")));
                return;
            }
        }

        // Kiểm tra luật 3 Bích ở lượt đầu ván đầu
        if (requireThreeSpades) {
            Card threeSpades = new Card(3, Card.Suit.SPADES);
            if (!move.getCards().contains(threeSpades)) {
                sender.sendMessage(Protocol.encode(new Message("NOTIFICATION", "ERROR:Lượt đầu tiên của ván đầu bắt buộc phải đánh 3♠!")));
                return;
            }
        }

        // Check hợp lệ
        if (!move.isValid()) {
            Message ms = new Message("NOTIFICATION", "ERROR:Bộ bài không hợp lệ!");
            sender.sendMessage(Protocol.encode(ms));
            return;
        }

        if (!GameLogic.canBeat(getLastMove(), move)) {
            Message ms = new Message("NOTIFICATION", "ERROR:Bộ bài không chặt được bài trên bàn!");
            sender.sendMessage(Protocol.encode(ms));
            return;
        }
        
        // Nếu ok → remove bài trong tay người chơi
        requireThreeSpades = false;
        player.removeCards(move.getCards());
        System.out.println(player.getName()+"play: "+moveData);
        setLastMove(move); 
        System.out.println(player.getName() +": " + player.getHand());
        if (player.hasNoCards()) {
            winnerIndex = players.indexOf(player);
            endGame(player.getName());
            return;
        }

        nextTurn();
        broadcastMove(player, move);
    }
    private Move parseMove(String moveData) {
        String[] parts = moveData.split(",");
        List<Card> cards = new ArrayList<>();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            String rankStr = part.substring(0, part.length() - 1);
            String suitStr = part.substring(part.length() - 1);

            // Xử lý chất (suit)
            Card.Suit suit;
            switch (suitStr) {
                case "♠": suit = Card.Suit.SPADES; break;
                case "♥": suit = Card.Suit.HEARTS; break;
                case "♦": suit = Card.Suit.DIAMONDS; break;
                case "♣": suit = Card.Suit.CLUBS; break;
                default: throw new IllegalArgumentException("Invalid suit: " + suitStr);
            }

            // Xử lý giá trị (rank)
            int rank;
            switch (rankStr.toUpperCase()) {
                case "A": rank = 14; break;
                case "K": rank = 13; break;
                case "Q": rank = 12; break;
                case "J": rank = 11; break;
                default:
                    try {
                        rank = Integer.parseInt(rankStr); 
                        if(rank == 2) rank = 15;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid rank: " + rankStr);
                    }
            }

            cards.add(new Card(rank, suit));
        }

        return new Move(cards);
    }


//    public boolean isGameOver() {
//        for (Player p : players) {
//            if (p.hasNoCards()) {
//                return true;
//            }
//        }
//        return false;
//    }

    public Player getWinner() {
        for (Player p : players) {
            if (p.hasNoCards()) {
                return p;
            }
        }
        return null;
    }
    public void chat(String msg) {
    	for (Player p : players) {
	        connections.get(p).sendMessage(Protocol.encode(new Message("CHAT", msg)));
	    }
    }

    public void chatVoice(String data, ClientHandler sender) {
        // data expected: senderName|base64AudioWav
        // Send to all players except the sender
        for (Player p : players) {
            if (connections.get(p) != sender) {
                connections.get(p).sendMessage(Protocol.encode(new Message(Protocol.CHAT_VOICE, data)));
            }
        }
    }
    
    public synchronized void removePlayer(Player player) {
        if (player == null) return;
        int removedIndex = players.indexOf(player);
        boolean wasCurrentTurn = gameRunning && removedIndex != -1 && removedIndex == currentTurnIndex;

        players.remove(player);
        connections.remove(player);
        passedPlayers.remove(player);

        if (winnerIndex == removedIndex || winnerIndex >= players.size()) {
            winnerIndex = -1;
        } else if (removedIndex != -1 && removedIndex < winnerIndex) {
            winnerIndex--;
        }

        // Nếu đang trong ván mà người chơi thoát giữa chừng -> xử thua cược của ván đó
        if (gameRunning && removedIndex != -1) {
            UserManager userManager = UserManager.getInstance();
            player.subtractBalance(betAmount);
            userManager.subtractBalance(player.getName(), betAmount);
            userManager.saveGameResult(sessionId, player.getName(), "LOSE", betAmount, 0);
            forfeitedPot += betAmount;
            totalPot += betAmount;
        }

        String noticeText = gameRunning && removedIndex != -1
                ? player.getName() + " đã thoát giữa ván (thua " + betAmount + " VND)."
                : player.getName() + " đã thoát.";
        Message msg = new Message("NOTIFICATION", noticeText);
        for (Player p : players) {
            ClientHandler ch = connections.get(p);
            if (ch != null) {
                ch.sendMessage(Protocol.encode(msg));
            }
        }

        if (gameRunning) {
            if (players.size() == 1) {
                cancelTurnTimer();
                // Còn lại 1 người -> người ở lại thắng toàn bộ tiền cược đã thu
                Player survivor = players.get(0);
                long winAmount = forfeitedPot;
                UserManager userManager = UserManager.getInstance();
                survivor.addBalance(winAmount);
                userManager.addBalance(survivor.getName(), winAmount);
                userManager.saveGameResult(sessionId, survivor.getName(), "WIN", betAmount, winAmount);
                userManager.endGameSession(sessionId, survivor.getName(), totalPot);
                userManager.updateSessionStatus(sessionId, "WAITING");

                String balanceInfo = survivor.getName() + ":" + survivor.getBalance();
                survivor.setPlaying(false);
                ClientHandler ch = connections.get(survivor);
                if (ch != null) {
                    ch.sendMessage(Protocol.encode(new Message("WIN", survivor.getName() + "|" + balanceInfo)));
                }
                broadcastPlayerList();
                setGameRunning(false);
                lastMove = null;
                passedPlayers.clear();
                resetPot();
                forfeitedPot = 0;
                winnerIndex = -1;
                currentTurnIndex = 0;
                return;
            }
            if (players.isEmpty()) {
                endGame("END");
                return;
            }
            if (removedIndex != -1) {
                if (removedIndex < currentTurnIndex) {
                    currentTurnIndex--;
                }
                if (currentTurnIndex >= players.size()) {
                    currentTurnIndex = 0;
                }
                if (wasCurrentTurn) {
                    while (passedPlayers.contains(players.get(currentTurnIndex)) || !players.get(currentTurnIndex).isPlaying()) {
                        currentTurnIndex = (currentTurnIndex + 1) % players.size();
                    }
                    broadcastTurn();
                }
            }
        } else if (currentTurnIndex >= players.size()) {
            currentTurnIndex = 0;
        }
    }

    public synchronized void endGame(String winnerName) {
        cancelTurnTimer();
        // Xử lý tiền khi ván kết thúc
        if (!"END".equals(winnerName)) {
            // Tìm người thắng
            Player winner = null;
            for (Player p : players) {
                if (p.getName().equals(winnerName)) {
                    winner = p;
                    break;
                }
            }

            if (winner != null) {
                // Tính tiền: Người thắng nhận tiền từ người thua + tiền người đã thoát giữa ván (nếu có)
                long winningSAmount = betAmount * (players.size() - 1) + forfeitedPot;
                
                // Cộng tiền cho người thắng
                winner.addBalance(winningSAmount);
                
                // Trừ tiền từ những người thua
                UserManager userManager = UserManager.getInstance();
                for (Player p : players) {
                    if (!p.getName().equals(winnerName)) {
                        p.subtractBalance(betAmount);
                        userManager.subtractBalance(p.getName(), betAmount);
                        // Lưu kết quả người thua vào database
                        userManager.saveGameResult(sessionId, p.getName(), "LOSE", betAmount, 0);
                    }
                }
                // Cộng tiền cho người thắng trong database
                userManager.addBalance(winnerName, winningSAmount);
                // Lưu kết quả người thắng vào database
                userManager.saveGameResult(sessionId, winnerName, "WIN", betAmount, winningSAmount);
                
                // Update game session status in database
                userManager.endGameSession(sessionId, winnerName, totalPot);
                userManager.updateSessionStatus(sessionId, "WAITING");
                
                System.out.println("🏆 " + winnerName + " won! Earned: " + winningSAmount + " VND");
            }
        } else {
            // Ván bị hủy do không đủ người chơi -> đưa trạng thái bàn trong DB về WAITING
            UserManager.getInstance().updateSessionStatus(sessionId, "WAITING");
            winnerIndex = -1;
        }

        // Gửi thông báo kết thúc ván cho các người chơi còn lại trong phòng
        if ("END".equals(winnerName)) {
            Message endMsg = new Message("END", "");
            for (Player p : players) {
                p.setPlaying(false);
                ClientHandler ch = connections.get(p);
                if (ch != null) {
                    ch.sendMessage(Protocol.encode(endMsg));
                }
            }
        } else {
            StringBuilder balanceData = new StringBuilder();
            for (int i = 0; i < players.size(); i++) {
                balanceData.append(players.get(i).getName()).append(":").append(players.get(i).getBalance());
                if (i < players.size() - 1) balanceData.append(",");
            }
            Message ms = new Message("WIN", winnerName + "|" + balanceData.toString());
            for (Player p : players) {
                p.setPlaying(false);
                ClientHandler ch = connections.get(p);
                if (ch != null) {
                    ch.sendMessage(Protocol.encode(ms));
                }
            }
        }
        
        // Broadcast updated player list with new balances
        if (!players.isEmpty()) {
            broadcastPlayerList();
        }
        
        setGameRunning(false);
        lastMove = null;
        passedPlayers.clear();
        resetPot();
        forfeitedPot = 0;
        if (backend != null) {
            backend.broadcastSessionListToLobby();
        }
    }
}
