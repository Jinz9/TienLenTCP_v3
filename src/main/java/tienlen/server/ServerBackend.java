package tienlen.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Backend logic cho server - quản lý connections, players, và sessions
 */
public class ServerBackend {
    private static final int PORT = 12345;
    private final List<GameSession> gameSessions = new CopyOnWriteArrayList<>();
    private final Map<String, PlayerInfo> onlinePlayers = new ConcurrentHashMap<>();
    private final Map<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public void start(ServerUI ui) throws IOException {
        serverSocket = new ServerSocket(PORT);
        isRunning = true;
        ui.appendLog("✅ Server started on port " + PORT);

        // Tải danh sách bàn chơi từ database
        UserManager userManager = UserManager.getInstance();
        java.util.List<java.util.Map<String, Object>> sessionsFromDb = userManager.loadActiveSessions();
        
        for (java.util.Map<String, Object> sessionData : sessionsFromDb) {
            String sessionId = (String) sessionData.get("session_id");
            String displayName = (String) sessionData.get("display_name");
            long betAmount = (long) sessionData.get("bet_amount");
            
            GameSession session = new GameSession(sessionId, displayName, betAmount);
            session.setBackend(this);
            gameSessions.add(session);
            ui.appendLog("📋 Loaded session from DB: " + displayName + " (ID: " + sessionId + ")");
        }
        
        if (sessionsFromDb.isEmpty()) {
            ui.appendLog("ℹ️ No active sessions found in database");
        }

        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                String clientIp = socket.getInetAddress().getHostAddress();
                ui.appendLog("🔗 New client connected: " + clientIp);

                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            } catch (IOException e) {
                if (isRunning) {
                    ui.appendLog("❌ Error accepting client: " + e.getMessage());
                }
            }
        }
    }

    public boolean registerPlayer(String username, String ipAddress, ClientHandler handler) {
        String key = normalizeUsername(username);
        if (key.isEmpty() || handler == null) {
            return false;
        }
        ClientHandler existing = clientHandlers.putIfAbsent(key, handler);
        if (existing != null) {
            return false;
        }
        PlayerInfo player = new PlayerInfo(username.trim(), ipAddress);
        onlinePlayers.put(key, player);
        return true;
    }

    public boolean isPlayerOnline(String username) {
        String key = normalizeUsername(username);
        return !key.isEmpty() && clientHandlers.containsKey(key);
    }

    public void unregisterPlayer(String username, ClientHandler handler) {
        String key = normalizeUsername(username);
        if (key.isEmpty()) {
            return;
        }
        if (handler == null) {
            clientHandlers.remove(key);
            onlinePlayers.remove(key);
            return;
        }
        if (clientHandlers.remove(key, handler)) {
            onlinePlayers.remove(key);
        }
    }

    public void unregisterPlayer(String username) {
        unregisterPlayer(username, null);
    }

    public void registerPlayerInSession(String username, String sessionId) {
        PlayerInfo player = onlinePlayers.get(normalizeUsername(username));
        if (player != null) {
            player.setStatus("IN_GAME");
            player.setCurrentSession(sessionId);
        }
    }

    public void unregisterPlayerFromSession(String username) {
        PlayerInfo player = onlinePlayers.get(normalizeUsername(username));
        if (player != null) {
            player.setStatus("IDLE");
            player.setCurrentSession(null);
        }
    }

    public List<PlayerInfo> getAllPlayers() {
        return new ArrayList<>(onlinePlayers.values());
    }

    public List<PlayerInfo> getAllPlayersWithOffline() {
        List<PlayerInfo> allPlayers = new ArrayList<>(onlinePlayers.values());
        UserManager userManager = UserManager.getInstance();
        java.util.List<String> allUsers = userManager.getAllUsers();
        
        // Add offline players (users not in onlinePlayers)
        for (String username : allUsers) {
            if (!onlinePlayers.containsKey(normalizeUsername(username))) {
                PlayerInfo offlinePlayer = new PlayerInfo(username, "N/A");
                offlinePlayer.setStatus("OFFLINE");
                allPlayers.add(offlinePlayer);
            }
        }
        return allPlayers;
    }

    public List<SessionInfo> getAllSessions() {
        List<SessionInfo> sessionInfos = new ArrayList<>();
        for (GameSession session : gameSessions) {
            String status = session.isGameRunning() ? "RUNNING" : "WAITING";
            sessionInfos.add(new SessionInfo(session.getSessionId(), session.getPlayers().size(), status, session.getDisplayName(), session.getBetAmount()));
        }
        return sessionInfos;
    }

    public void broadcastSessionListToLobby() {
        StringBuilder sb = new StringBuilder();
        List<SessionInfo> sessions = getAllSessions();
        for (int i = 0; i < sessions.size(); i++) {
            SessionInfo s = sessions.get(i);
            if (i > 0) sb.append(";");
            sb.append(s.getSessionId()).append(",")
              .append(s.getPlayerCount()).append(",")
              .append(s.getStatus()).append(",")
              .append(s.getDisplayName()).append(",")
              .append(s.getBetAmount());
        }
        tienlen.model.Message msg = new tienlen.model.Message(tienlen.utils.Protocol.SESSION_LIST, sb.toString());
        String encoded = tienlen.utils.Protocol.encode(msg);
        for (ClientHandler handler : clientHandlers.values()) {
            if (handler != null && handler.isInLobby()) {
                handler.sendMessage(encoded);
            }
        }
    }

    public GameSession createNewSession() {
        GameSession session = new GameSession();
        session.setBackend(this);
        gameSessions.add(session);
        return session;
    }

    public GameSession createNewSession(String displayName, long betAmount) {
        GameSession session = new GameSession(displayName);
        session.setBetAmount(betAmount);
        session.setBackend(this);
        gameSessions.add(session);
        return session;
    }

    public GameSession findSessionById(String sessionId) {
        return gameSessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public List<GameSession> getAvailableSessions() {
        List<GameSession> available = new ArrayList<>();
        for (GameSession session : gameSessions) {
            if (session.getPlayers().size() < 4 && !session.isGameRunning()) {
                available.add(session);
            }
        }
        return available;
    }

    public void kickPlayer(String username) {
        String key = normalizeUsername(username);
        PlayerInfo player = onlinePlayers.get(key);
        if (player != null) {
            // Gửi message kick đến player
            ClientHandler handler = clientHandlers.get(key);
            if (handler != null) {
                // Gửi thông báo KICKED trước khi đóng connection
                tienlen.model.Message msg = new tienlen.model.Message("KICKED", "Bạn đã bị loại khỏi server bởi admin");
                handler.sendMessage(tienlen.utils.Protocol.encode(msg));
                // Đóng connection
                handler.closeConnection();
            }
            unregisterPlayer(username, handler);
        }
    }

    public String getSessionDetails(String sessionId) {
        GameSession session = findSessionById(sessionId);
        if (session == null) {
            return "Session not found!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Session ID: ").append(sessionId).append("\n");
        sb.append("Players (").append(session.getPlayers().size()).append("/4):\n");

        for (tienlen.model.Player p : session.getPlayers()) {
            sb.append("  - ").append(p.getName()).append("\n");
        }

        sb.append("\nStatus: ").append(session.isGameRunning() ? "RUNNING" : "WAITING");

        return sb.toString();
    }

    public void closeSession(String sessionId) {
        GameSession session = findSessionById(sessionId);
        if (session != null) {
            // Notify all players in this session and set them to IDLE
            for (tienlen.model.Player player : new ArrayList<>(session.getPlayers())) {
                String playerName = player.getName();
                // send SESSION_CLOSED message to client if connected
                ClientHandler handler = clientHandlers.get(normalizeUsername(playerName));
                if (handler != null) {
                    tienlen.model.Message msg = new tienlen.model.Message(tienlen.utils.Protocol.SESSION_CLOSED, sessionId + "|Closed by server");
                    handler.sendMessage(tienlen.utils.Protocol.encode(msg));
                }
                unregisterPlayerFromSession(playerName);
            }
            // remove session from list
            gameSessions.remove(session);
            broadcastSessionListToLobby();
        }
    }

    public int getTotalPlayers() {
        return onlinePlayers.size();
    }

    public int getTotalSessions() {
        return gameSessions.size();
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
