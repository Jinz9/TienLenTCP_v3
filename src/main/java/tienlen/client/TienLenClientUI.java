package tienlen.client;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tienlen.model.Card;
import tienlen.model.Message;
import tienlen.model.Move;
import tienlen.utils.Protocol;
import tienlen.logic.GameLogic;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class TienLenClientUI{

    private HBox playerHand;              // Lá bài của người chơi
    private HBox playedCardsPane;         // Các lá bài đã đánh
    private VBox chatBox;                 // Khung chat
    private VBox playerListBox;           // Danh sách người chơi
    private TextArea chatArea;
    private TextField chatInput;
    private Button sendButton;
    private Button voiceButton;
    private Button playButton;
    private Button passButton;
    private Move lastMove = null;
    // Các khung hiển thị người chơi
    private VBox leftPlayerBox;
    private VBox topPlayerBox;
    private VBox rightPlayerBox;
    private Button newGameButton;
    private long myBalance = 0;     // Lưu số tiền hiện tại
        private VBox myPlayerBox;       // Khung hiển thị tên + tiền của bản thân (góc dưới trái)
        private Label myNameLabel;      // Tên của bản thân
        private Label myBalanceLabel;   // Số tiền của bản thân
        public HBox getPlayerHand() { return playerHand; }
    public HBox getPlayedCardsPane() { return playedCardsPane; }
    public VBox getPlayerListBox() { return playerListBox; }
    public TextArea getChatArea() { return chatArea; }
    public List<VBox> getPlayerBoxes(){return playerBoxes;}
    private String currentTurn = null;
    private javafx.animation.Animation currentTurnAnimation = null;
    private javafx.animation.Timeline turnCountdownTimeline = null;
    private int turnSecondsLeft = 30;
    private Label playedByLabel;
    private final java.util.Map<String, Integer> playerCardCounts = new java.util.HashMap<>();

    // Map tên -> VBox playerBox
    private final List<VBox> playerBoxes = new ArrayList<>();

    private final List<ToggleButton> cardButtons = new ArrayList<>();
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String sessionDisplayName = "";
    private long sessionBet = 10000;
    private Runnable onExit;

    // Audio recording
    private volatile boolean recording = false;
    private TargetDataLine targetLine;

    public TienLenClientUI(PrintWriter out, BufferedReader in, String username) {
        this.out = out;
        this.in = in;
        this.username = username;
    }

    public TienLenClientUI(PrintWriter out, BufferedReader in, String username, String displayName, long bet) {
        this.out = out;
        this.in = in;
        this.username = username;
        this.sessionDisplayName = displayName == null ? "" : displayName;
        this.sessionBet = bet;
    }

    public void setOnExit(Runnable r) { this.onExit = r; }
    
    
    public Move getLastMove() {
		return lastMove;
	}
	public void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}
	
    // Tạo avatar + tên + số dư cho người chơi
    private VBox createPlayerBox(String name) {
        Circle avatar = new Circle(18, Color.web("#2196F3"));
        avatar.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);");
        
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("player-name");
        
        Label balanceLabel = new Label("0 VND");
        balanceLabel.getStyleClass().add("player-balance");
        
        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("player-status");

        VBox box = new VBox(2, avatar, nameLabel, balanceLabel, statusLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setMinWidth(110);
        box.getStyleClass().add("player-box");

        return box;
    }

    // Tạo giao diện mặt lá bài dùng chung cho cả bài trên tay và bài đánh giữa bàn
    private javafx.scene.Node createCardGraphic(String cardText, double width, double height) {
        String trimmed = cardText == null ? "" : cardText.trim();
        String rank = trimmed.length() > 1 ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
        String suit = trimmed.length() > 1 ? trimmed.substring(trimmed.length() - 1) : "";
        boolean isRed = "♥".equals(suit) || "♦".equals(suit);
        String colorHex = isRed ? "#D32F2F" : "#1E293B";

        double cornerRankSize = Math.max(11, width * 0.19);
        double cornerSuitSize = Math.max(10, width * 0.16);
        double centerSuitSize = Math.max(20, width * 0.38);

        Label topRank = new Label(rank);
        topRank.setStyle("-fx-font-size: " + cornerRankSize + "px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label topSuit = new Label(suit);
        topSuit.setStyle("-fx-font-size: " + cornerSuitSize + "px; -fx-text-fill: " + colorHex + ";");
        VBox topLeft = new VBox(-3, topRank, topSuit);
        topLeft.setAlignment(Pos.CENTER);
        topLeft.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Label centerSuit = new Label(suit.isEmpty() ? rank : suit);
        centerSuit.setStyle("-fx-font-size: " + centerSuitSize + "px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label bottomRank = new Label(rank);
        bottomRank.setStyle("-fx-font-size: " + cornerRankSize + "px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label bottomSuit = new Label(suit);
        bottomSuit.setStyle("-fx-font-size: " + cornerSuitSize + "px; -fx-text-fill: " + colorHex + ";");
        VBox bottomRight = new VBox(-3, bottomRank, bottomSuit);
        bottomRight.setAlignment(Pos.CENTER);
        bottomRight.setRotate(180);
        bottomRight.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane cardFace = new StackPane();
        cardFace.setPrefSize(width, height);
        cardFace.setMinSize(width, height);
        cardFace.setMaxSize(width, height);
        cardFace.setPadding(new Insets(3, 5, 3, 5));
        cardFace.getChildren().addAll(centerSuit, topLeft, bottomRight);
        StackPane.setAlignment(topLeft, Pos.TOP_LEFT);
        StackPane.setAlignment(centerSuit, Pos.CENTER);
        StackPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);

        return cardFace;
    }

    // Thêm bài đã đánh vào giữa bàn
    private void addPlayedCards(List<String> cards) {
        playedCardsPane.getChildren().clear();
        for (String c : cards) {
            Label card = new Label();
            card.setGraphic(createCardGraphic(c, 62, 92));
            card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            card.getStyleClass().add("played-card");
            card.setAlignment(Pos.CENTER);
            card.setPrefWidth(65);
            card.setPrefHeight(95);
            card.setMinWidth(65);
            card.setMinHeight(95);
            card.setMaxWidth(65);
            card.setMaxHeight(95);

            FadeTransition ft = new FadeTransition(Duration.millis(500), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            playedCardsPane.getChildren().add(card);
        }
    }
    public BorderPane createRootPane() {
        
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");


        // === Trung tâm bàn chơi ===
        StackPane tablePane = new StackPane();
        tablePane.setPadding(new Insets(6));
        tablePane.setMinWidth(240);
        tablePane.setMinHeight(120);

        playedByLabel = new Label("");
        playedByLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 13px;");

        playedCardsPane = new HBox(6);
        playedCardsPane.setAlignment(Pos.CENTER);

        VBox centerTableBox = new VBox(8, playedByLabel, playedCardsPane);
        centerTableBox.setAlignment(Pos.CENTER);
        centerTableBox.getStyleClass().add("played-cards");
        VBox.setVgrow(playedCardsPane, Priority.ALWAYS);

        tablePane.getChildren().add(centerTableBox);
        root.setCenter(tablePane);

        // === Người chơi trái / trên / phải ===
        leftPlayerBox = createPlayerBox("Player Left");
        topPlayerBox = createPlayerBox("Player Top");
        rightPlayerBox = createPlayerBox("Player Right");
       

        // Lưu tất cả để tiện update
        playerBoxes.add(leftPlayerBox);
        playerBoxes.add(topPlayerBox);
        playerBoxes.add(rightPlayerBox);
        


        BorderPane.setAlignment(leftPlayerBox, Pos.CENTER_LEFT);
        BorderPane.setAlignment(topPlayerBox, Pos.TOP_CENTER);
        BorderPane.setAlignment(rightPlayerBox, Pos.CENTER_RIGHT);

        BorderPane.setMargin(leftPlayerBox, new Insets(0, 0, 0, 8));
        root.setLeft(leftPlayerBox);
        
        // Create a top bar with info box on the left and the top player box centered
        HBox infoBox = new HBox(12);
        infoBox.setPadding(new Insets(8, 12, 8, 12));
        infoBox.getStyleClass().add("info-box");
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(sessionDisplayName == null || sessionDisplayName.isEmpty() ? "🎴 Bàn" : "🎴 " + sessionDisplayName);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label betLabel = new Label("💰 Cược: " + sessionBet + " VND");
        betLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Button exitBtn = new Button("Thoát");
        exitBtn.getStyleClass().add("info-button");
        exitBtn.setOnAction(e -> {
            try { out.println(Protocol.encode(new Message("LEAVE_SESSION", ""))); } catch (Exception ex) {}
            if (onExit != null) onExit.run();
        });
        
        infoBox.getChildren().addAll(nameLabel, betLabel, exitBtn);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(12, infoBox, leftSpacer, topPlayerBox, rightSpacer);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(6, 10, 4, 10));

        root.setTop(topBar);

        // === Khu vực bài của người chơi ===
        playerHand = new HBox(3);
        playerHand.setAlignment(Pos.CENTER_LEFT);
        playerHand.setPadding(new Insets(6, 10, 6, 10));
        playerHand.getStyleClass().add("player-hand");
        playerHand.setPrefHeight(118);
        playerHand.setMinHeight(115);
        
        // ScrollPane for hand
        ScrollPane handScroll = new ScrollPane(playerHand);
        handScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        handScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        handScroll.setFitToHeight(true);
        handScroll.setMinHeight(120);
        handScroll.setPrefHeight(122);
        handScroll.setMaxHeight(130);
        handScroll.setPannable(true);

        // Nút Play / Pass
        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(5, 10, 5, 10));
        controlBox.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-border-radius: 8; -fx-background-radius: 8;");
        
        playButton = new Button("Đánh Bài");
        passButton = new Button("Bỏ Qua");
        playButton.getStyleClass().add("control-btn");
        passButton.getStyleClass().add("control-btn");
        passButton.setOnAction(e -> passTurn());
        playButton.setOnAction(e -> playSelectedCards());
        
        newGameButton = new Button("Ván Mới");
        newGameButton.getStyleClass().add("control-btn");
        newGameButton.setVisible(true);    
        newGameButton.setManaged(true);   

        newGameButton.setOnAction(e -> {
            Message ms = new Message("NEWGAME", username);
            lastMove = null;
            out.println(Protocol.encode(ms));
            newGameButton.setVisible(false);
            newGameButton.setManaged(false);
        });
        
        controlBox.getChildren().addAll(playButton, passButton, newGameButton);

        // === Tạo player box cho chính mình (góc dưới trái) ===
        Circle myAvatar = new Circle(18, Color.web("#00BCD4"));
        myAvatar.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);");
        
        myNameLabel = new Label(username);
        myNameLabel.getStyleClass().add("player-name");
        
        myBalanceLabel = new Label("0 VND");
        myBalanceLabel.getStyleClass().add("player-balance");
        
        Label myStatusLabel = new Label("Sẵn sàng");
        myStatusLabel.getStyleClass().add("player-status");
        
        myPlayerBox = new VBox(2, myAvatar, myNameLabel, myBalanceLabel, myStatusLabel);
        myPlayerBox.setAlignment(Pos.CENTER);
        myPlayerBox.setPadding(new Insets(6, 10, 6, 10));
        myPlayerBox.setMaxHeight(Region.USE_PREF_SIZE);
        myPlayerBox.setMinWidth(110);
        myPlayerBox.getStyleClass().add("player-box");
        playerBoxes.add(myPlayerBox); // Thêm vào danh sách để nhận hiệu ứng turn

        // Layout dưới cùng: player box bên trái + kiểm soát ở giữa + bài ở dưới
        VBox centerControlBox = new VBox(6, controlBox, handScroll);
        centerControlBox.setAlignment(Pos.CENTER);
        centerControlBox.setFillWidth(true);

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.setPadding(new Insets(4, 10, 8, 10));
        bottomBox.getChildren().addAll(myPlayerBox, centerControlBox);
        HBox.setHgrow(centerControlBox, Priority.ALWAYS);

        root.setBottom(bottomBox);

        // === Khung Chat + DS người chơi (chia đôi dọc) ===
        VBox rightPane = new VBox(6);
        rightPane.setPrefWidth(270);
        rightPane.setMinWidth(230);
        rightPane.setMinHeight(0);

        Label chatLabel = new Label("💬 TRÒ CHUYỆN");
        chatLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        chatArea = new TextArea("🎮 Trò chơi sẽ bắt đầu khi có đủ 2 người chơi\n");
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefRowCount(4);
        chatArea.setMinHeight(60);
        chatArea.getStyleClass().add("chat-area");

        chatInput = new TextField();
        chatInput.getStyleClass().add("chat-input");
        chatInput.setPromptText("Nhập tin nhắn...");
        chatInput.setMinWidth(80);
        
        sendButton = new Button("Gửi");
        sendButton.getStyleClass().add("chat-button");
        sendButton.setMinWidth(Region.USE_PREF_SIZE);
        sendButton.setOnAction(e -> sendChat());
        chatInput.setOnAction(e -> sendChat());

        voiceButton = new Button("🎤");
        voiceButton.getStyleClass().add("voice-button");
        voiceButton.setMinWidth(Region.USE_PREF_SIZE);
        voiceButton.setPrefWidth(48);
        voiceButton.setOnAction(e -> {
            if (!recording) startRecording();
            else stopRecordingAndSend();
        });

        HBox chatInputBox = new HBox(6, chatInput, sendButton, voiceButton);
        chatInputBox.setAlignment(Pos.CENTER);
        chatInputBox.setPadding(new Insets(4, 0, 0, 0));
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        chatBox = new VBox(6, chatLabel, chatArea, chatInputBox);
        chatBox.setAlignment(Pos.TOP_LEFT);
        chatBox.setPadding(new Insets(8));
        chatBox.setMinHeight(120);
        chatBox.getStyleClass().add("chat-box");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        Label playerListLabel = new Label("👥 NGƯỜI CHƠI");
        playerListLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        playerListBox = new VBox(6);
        playerListBox.setAlignment(Pos.TOP_LEFT);
        playerListBox.setPadding(new Insets(8));
        playerListBox.getStyleClass().add("player-list");
        
        ScrollPane playerListScroll = new ScrollPane(playerListBox);
        playerListScroll.setFitToWidth(true);
        playerListScroll.setMinHeight(60);
        playerListScroll.setPrefHeight(90);
        
        VBox playerListContainer = new VBox(4, playerListLabel, playerListScroll);
        VBox.setVgrow(playerListScroll, Priority.ALWAYS);

        VBox.setVgrow(chatBox, Priority.ALWAYS);
        rightPane.getChildren().addAll(chatBox, playerListContainer);

        /// Gom player bên phải + chat + danh sách vào chung VBox
        VBox outerRight = new VBox(8, rightPlayerBox, rightPane);
        outerRight.setAlignment(Pos.TOP_CENTER);
        outerRight.setPadding(new Insets(4, 8, 4, 4));
        outerRight.setMinHeight(0);
        VBox.setVgrow(rightPane, Priority.ALWAYS);

        root.setRight(outerRight);

        disableButton(); 
        
        return root;
    }
    private void playSelectedCards() {
        if (currentTurn == null || !currentTurn.equals(username)) {
            chatArea.appendText("⚠ Chưa đến lượt của bạn!\n");
            return;
        }
        List<Card> selectedCards = new ArrayList<>();

        for (ToggleButton btn : cardButtons) {
            if (btn.isSelected()) {
                String cardText = btn.getUserData() != null ? btn.getUserData().toString() : btn.getText();
                Card card = new Card(cardText);
                selectedCards.add(card);
            }
        }

        if (selectedCards.isEmpty()) {
            chatArea.appendText("⚠ Bạn chưa chọn lá nào!\n");
            return;
        }
        // Xác định loại combo
        Move.ComboType type = GameLogic.identifyType(selectedCards);
        if (type == Move.ComboType.INVALID) {
            chatArea.appendText("❌ Bộ bài không hợp lệ!\n");
            return;
        }

        // Kiểm tra có chặt được hay không (nếu đã có bàn trước đó)
        if (lastMove != null) {
            Move prev = lastMove;
            Move next = new Move(selectedCards);
            if (!GameLogic.canBeat(prev, next)) {
                chatArea.appendText("❌ Bộ này không chặt được bài trên bàn!\n");
                return;
            }
        }

        // Chống spam click: khóa nút ngay khi gửi, chỉ xóa bài khi Server xác nhận PLAY
        playButton.setDisable(true);
        passButton.setDisable(true);

        StringBuilder data = new StringBuilder(selectedCards.get(0).toString());
        for (int i = 1; i < selectedCards.size(); i++) {
            data.append(",").append(selectedCards.get(i).toString());
        }
        Message ms = new Message("PLAY", data.toString());
        out.println(Protocol.encode(ms));
    }

    public void stopTurnCountdown() {
        if (turnCountdownTimeline != null) {
            turnCountdownTimeline.stop();
            turnCountdownTimeline = null;
        }
        if (currentTurnAnimation != null) {
            currentTurnAnimation.stop();
            currentTurnAnimation = null;
        }
        for (VBox box : playerBoxes) {
            box.setScaleX(1.0);
            box.setScaleY(1.0);
            box.setStyle("");
        }
    }

    private void updateSeatStatusLabel(VBox box, String playerName) {
        if (box.getChildren().size() < 4) return;
        Label statusLbl = (Label) box.getChildren().get(3);
        Integer count = playerCardCounts.get(playerName);
        String countText = (count != null && count > 0) ? (count + " lá") : (playerName.equals(username) ? "Sẵn sàng" : "");
        if (playerName.equals(currentTurn)) {
            statusLbl.setText((count != null && count > 0 ? count + " lá • " : "") + "⏱ " + turnSecondsLeft + "s");
        } else {
            statusLbl.setText(countText);
        }
    }

    public void highlightTurn(String currentPlayer) {
        if (currentPlayer == null) return;
        this.currentTurn = currentPlayer;

        // Hủy animation & timer cũ trước khi bắt đầu lượt mới
        stopTurnCountdown();
        turnSecondsLeft = 30;

        VBox activeBox = null;
        for (VBox box : playerBoxes) {
            Label nameLabel = (Label) box.getChildren().get(1);
            String pName = nameLabel.getText();
            updateSeatStatusLabel(box, pName);

            if (pName.equals(currentPlayer)) {
                activeBox = box;
                // Áp dụng style với glow effect
                box.setStyle("-fx-border-color: #ffeb3b; -fx-border-width: 3; " +
                    "-fx-background-color: linear-gradient(to bottom, rgba(255,235,59,0.08), rgba(255,193,7,0.03)); " +
                    "-fx-effect: dropshadow(gaussian, #ffeb3b, 15, 0.8, 0, 0);");
                
                // Thêm animation scale với glow
                ScaleTransition st = new ScaleTransition(Duration.millis(1200), box);
                st.setFromX(1.0);
                st.setToX(1.05);
                st.setFromY(1.0);
                st.setToY(1.05);
                st.setCycleCount(javafx.animation.Animation.INDEFINITE);
                st.setAutoReverse(true);
                currentTurnAnimation = st;
                st.play();
                
                // Thông báo & bật/tắt nút theo lượt
                if (pName.equals(username)) {
                    chatArea.appendText("\n🎯 ĐẾN LƯỢT CỦA BẠN!\n");
                    chatArea.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #1a1a1a; -fx-border-color: #ffc107; -fx-border-width: 2;");
                    playButton.setDisable(false);
                    passButton.setDisable(lastMove == null);
                    playerHand.setDisable(false);
                } else {
                    chatArea.appendText("\n▶ Lượt của: " + currentPlayer + "\n");
                    chatArea.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #1a1a1a; -fx-border-color: #1976d2; -fx-border-width: 1;");
                    playButton.setDisable(true);
                    passButton.setDisable(true);
                }
            } else {
                box.setStyle(""); // reset lại style
            }
        }

        if (activeBox != null) {
            final VBox targetBox = activeBox;
            final String targetPlayer = currentPlayer;
            turnCountdownTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(1), e -> {
                    turnSecondsLeft = Math.max(0, turnSecondsLeft - 1);
                    updateSeatStatusLabel(targetBox, targetPlayer);
                })
            );
            turnCountdownTimeline.setCycleCount(30);
            turnCountdownTimeline.play();
        }
    }
    public void updatePlayerList(String[] playersWithBalances, String myName) {
        playerListBox.getChildren().clear();
        playerListBox.getChildren().add(new Label("Players"));

        // Update my balance in header and fill player list
        for (String entry : playersWithBalances) {
            String[] parts = entry.split(":");
            String name = parts[0];
            String bal = parts.length > 1 ? parts[1] : "0";
            if (name.equals(myName)) {
                myBalance = Long.parseLong(bal);
                    myBalanceLabel.setText(bal + " VND");
            }
            playerListBox.getChildren().add(new Label(name + " - " + bal));
        }

        int myIndex = -1;
        String[] names = new String[playersWithBalances.length];
        for (int i = 0; i < playersWithBalances.length; i++) {
            String[] parts = playersWithBalances[i].split(":");
            names[i] = parts[0];
            if (names[i].equals(myName)) myIndex = i;
        }

        // Reset 3 ghế đối thủ trước khi cập nhật lại danh sách hiện tại
        String[] defaultSeatNames = {"Trống", "Trống", "Trống"};
        for (int i = 0; i < 3 && i < playerBoxes.size(); i++) {
            VBox seat = playerBoxes.get(i);
            Label nameLabel = (Label) seat.getChildren().get(1);
            Label balLabel = (Label) seat.getChildren().get(2);
            nameLabel.setText(defaultSeatNames[i]);
            balLabel.setText("0 VND");
            seat.setOpacity(0.55);
        }

        if (myIndex != -1) {
            for (int offset = 1; offset < names.length; offset++) {
                int pos = (myIndex + offset) % names.length;
                String[] parts = playersWithBalances[pos].split(":");
                String playerName = parts[0];
                String balance = parts.length > 1 ? parts[1] : "0";

                int seatIdx = offset - 1;
                if (seatIdx >= 0 && seatIdx < 3 && seatIdx < playerBoxes.size()) {
                    VBox seat = playerBoxes.get(seatIdx);
                    Label nameLabel = (Label) seat.getChildren().get(1);
                    Label balLabel = (Label) seat.getChildren().get(2);
                    nameLabel.setText(playerName);
                    balLabel.setText(balance + " VND");
                    seat.setOpacity(1.0);
                }
            }
        }
        highlightTurn(currentTurn);
    }
    public void addCardToHand(String cardText) {
        ToggleButton card = new ToggleButton();
        card.setUserData(cardText);
        card.setGraphic(createCardGraphic(cardText, 68, 100));
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.getStyleClass().add("card");
        
        // Thêm animation khi chọn/bỏ chọn
        card.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Khi chọn: nâng lên
                TranslateTransition tt = new TranslateTransition(Duration.millis(150), card);
                tt.setByY(-15);
                tt.play();
            } else {
                // Khi bỏ chọn: hạ xuống
                TranslateTransition tt = new TranslateTransition(Duration.millis(150), card);
                tt.setByY(15);
                tt.play();
            }
        });
        
        cardButtons.add(card);
        playerHand.getChildren().add(card);
    }

    public void clearTable() {
        playedCardsPane.getChildren().clear();
        if (playedByLabel != null) {
            playedByLabel.setText("");
        }
        lastMove = null;
    }

    public void setHand(List<String> cards) {
        clearTable();
        playerHand.getChildren().clear();
        cardButtons.clear();

        // Tự động sắp xếp bài trên tay từ nhỏ đến lớn (3 -> 2, chất ♠ ♣ ♦ ♥)
        List<Card> sortedCards = new ArrayList<>();
        for (String c : cards) {
            if (c != null && !c.trim().isEmpty()) {
                sortedCards.add(new Card(c.trim()));
            }
        }
        java.util.Collections.sort(sortedCards);
        for (Card c : sortedCards) {
            addCardToHand(c.toString());
        }

        // Khởi tạo số lá bài (13 lá) cho tất cả người chơi đang ngồi trên bàn
        playerCardCounts.clear();
        for (VBox box : playerBoxes) {
            Label nameLbl = (Label) box.getChildren().get(1);
            String pName = nameLbl.getText();
            if (!"Trống".equals(pName)) {
                playerCardCounts.put(pName, sortedCards.size());
                updateSeatStatusLabel(box, pName);
            }
        }

        playButton.setDisable(false);
        passButton.setDisable(false);
        playerHand.setDisable(false);
        newGameButton.setVisible(false);
        newGameButton.setManaged(false);
    }

    public List<ToggleButton> getCardButtons() {
        return cardButtons;
    }

   
    public void showPlayedCards(String moveData) {
        String[] parts = moveData.split(",");
        String playerName = parts[0];
        List<String> cards = new ArrayList<>();
        List<Card> listCards = new ArrayList<>();
        for(int i = 1; i <parts.length;i++) {
            String cardStr = parts[i].trim();
        	cards.add(cardStr);
        	Card card = new Card(cardStr); 
            listCards.add(card);
        }
        lastMove = new Move(listCards);
        addPlayedCards(cards);
        if (playedByLabel != null) {
            playedByLabel.setText("🃏 " + playerName + " vừa đánh:");
        }

        // Giảm số lá bài còn lại của người vừa đánh và cập nhật UI
        int remaining = Math.max(0, playerCardCounts.getOrDefault(playerName, 13) - cards.size());
        playerCardCounts.put(playerName, remaining);
        for (VBox box : playerBoxes) {
            Label nameLbl = (Label) box.getChildren().get(1);
            if (nameLbl.getText().equals(playerName)) {
                updateSeatStatusLabel(box, playerName);
                break;
            }
        }

        // Chỉ xóa bài trên tay khi Server xác nhận nước đi PLAY của chính mình
        if (playerName.equals(username)) {
            List<String> toRemove = new ArrayList<>(cards);
            cardButtons.removeIf(btn -> {
                String cardText = btn.getUserData() != null ? btn.getUserData().toString() : btn.getText();
                if (toRemove.remove(cardText)) {
                    playerHand.getChildren().remove(btn);
                    return true;
                }
                return false;
            });
        }
    }

    public void onMoveRejected(String errorMsg) {
        chatArea.appendText("❌ " + errorMsg + "\n");
        if (currentTurn != null && currentTurn.equals(username)) {
            playButton.setDisable(false);
            passButton.setDisable(lastMove == null);
            playerHand.setDisable(false);
        }
    }

    private void passTurn() {
        if (currentTurn == null || !currentTurn.equals(username)) {
            chatArea.appendText("⚠ Chưa đến lượt của bạn!\n");
            return;
        }
        // Nếu không có lastMove (tức mình được đánh đầu) thì không thể Pass
        if (lastMove == null) {
            chatArea.appendText("⚠ Không thể bỏ lượt khi chưa có ai đánh!\n");
            return;
        }

        // Chống spam click: khóa nút ngay khi gửi PASS
        playButton.setDisable(true);
        passButton.setDisable(true);

        // Gửi thông điệp PASS lên server
        Message ms = new Message("PASS", ""); 
        out.println(Protocol.encode(ms));
    }

    public void disableButton() {
    	 playButton.setDisable(true);
         passButton.setDisable(true);
         playerHand.setDisable(true);
	}
    public void showNewGameButton() {
        newGameButton.setVisible(true);
        newGameButton.setManaged(true);
    }
    
    public void updateBalance(long newBalance) {
        this.myBalance = newBalance;
            myBalanceLabel.setText(newBalance + " VND");
    }
    
    public long getMyBalance() {
        return myBalance;
    }
    
    private void sendChat() {
        String text = chatInput.getText().trim();
        if (!text.isEmpty()) {
            Message ms = new Message("CHAT", username + ": " + text);
            out.println(Protocol.encode(ms));
            chatInput.clear();
        }
    }

    private void startRecording() {
        AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(format);
            targetLine.start();
            recording = true;
            voiceButton.setText("⏹");

            Thread t = new Thread(() -> {
                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    while (recording) {
                        int count = targetLine.read(buffer, 0, buffer.length);
                        if (count > 0) outStream.write(buffer, 0, count);
                    }
                    // build wav bytes
                    byte[] pcm = outStream.toByteArray();
                    byte[] wav = toWav(pcm, format);
                    String b64 = Base64.getEncoder().encodeToString(wav);
                    Message ms = new Message(Protocol.CHAT_VOICE, username + "|" + b64);
                    out.println(Protocol.encode(ms));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingAndSend() {
        recording = false;
        voiceButton.setText("🎤");
        if (targetLine != null) {
            targetLine.stop();
            targetLine.close();
        }
    }

    private byte[] toWav(byte[] pcm, AudioFormat format) throws IOException {
        int channels = format.getChannels();
        int sampleRate = (int) format.getSampleRate();
        int bitsPerSample = format.getSampleSizeInBits();
        int byteRate = sampleRate * channels * bitsPerSample / 8;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // RIFF header
        out.write(new byte[]{'R','I','F','F'});
        int dataLen = pcm.length + 36;
        out.write(intToLittleEndian(dataLen));
        out.write(new byte[]{'W','A','V','E'});
        // fmt chunk
        out.write(new byte[]{'f','m','t',' '});
        out.write(intToLittleEndian(16)); // subchunk1 size
        out.write(shortToLittleEndian((short)1)); // PCM
        out.write(shortToLittleEndian((short)channels));
        out.write(intToLittleEndian(sampleRate));
        out.write(intToLittleEndian(byteRate));
        out.write(shortToLittleEndian((short)(channels * bitsPerSample/8)));
        out.write(shortToLittleEndian((short)bitsPerSample));
        // data chunk
        out.write(new byte[]{'d','a','t','a'});
        out.write(intToLittleEndian(pcm.length));
        out.write(pcm);

        return out.toByteArray();
    }

    private byte[] intToLittleEndian(int val) {
        return new byte[]{(byte)(val & 0xff), (byte)((val>>8)&0xff), (byte)((val>>16)&0xff), (byte)((val>>24)&0xff)};
    }

    private byte[] shortToLittleEndian(short val) {
        return new byte[]{(byte)(val & 0xff), (byte)((val>>8)&0xff)};
    }


}

