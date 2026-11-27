package tienlen.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tienlen.model.Message;
import tienlen.utils.Protocol;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class TableSelectionUI {
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private VBox tableListBox;
    private Label statusLabel;
    private Label balanceLabel;
    private Label quickTotalGamesVal;
    private Label quickWinsVal;
    private Label quickWinRateVal;
    private Label quickNetProfitVal;
    private Map<String, TableInfo> tables = new HashMap<>();
    private Map<String, String[]> playerStatsCache = new HashMap<>(); // Cache thống kê
    private String selectedSessionId = null;
    private Runnable onTableSelected;
    private Runnable onQuit;

    public static class TableInfo {
        public String sessionId;
        public int playerCount;
        public String status;
        public String displayName;
        public long betAmount;
        public String[] playerNames;
        public long[] playerBalances;
        
        public TableInfo(String sessionId, int playerCount, String status, String displayName, long betAmount) {
            this.sessionId = sessionId;
            this.playerCount = playerCount;
            this.status = status;
            this.displayName = displayName;
            this.betAmount = betAmount;
            this.playerNames = new String[playerCount];
            this.playerBalances = new long[playerCount];
        }
    }

    public TableSelectionUI(PrintWriter out, BufferedReader in, String username) {
        this.out = out;
        this.in = in;
        this.username = username;
    }

    public javafx.scene.layout.BorderPane createRootPane() {
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e27, #1a1a3e);");

        // ========== TOP: COMPACT HERO BANNER ==========
        VBox heroBanner = createHeroBanner();
        root.setTop(heroBanner);

        // ========== CENTER: MAIN CONTENT ==========
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(16, 24, 12, 24));
        mainContent.setStyle("-fx-background-color: transparent;");

        // Left sidebar wrapped in ScrollPane so it never clips on smaller screens
        VBox leftSidebar = createLeftSidebar();
        ScrollPane leftScroll = new ScrollPane(leftSidebar);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroll.setPrefWidth(285);
        leftScroll.setMinWidth(240);
        leftScroll.setMaxWidth(310);
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        // Right content - Game Tables
        VBox rightContent = createRightContent();
        HBox.setHgrow(rightContent, Priority.ALWAYS);

        mainContent.getChildren().addAll(leftScroll, rightContent);
        root.setCenter(mainContent);

        // ========== BOTTOM: PINNED CONTROL BAR ==========
        HBox controlBox = createControlBox();
        controlBox.setPadding(new Insets(10, 24, 14, 24));
        controlBox.setStyle("-fx-background-color: rgba(10, 14, 39, 0.85); -fx-border-color: rgba(78, 204, 163, 0.35); -fx-border-width: 1 0 0 0;");
        root.setBottom(controlBox);

        return root;
    }

    private VBox createHeroBanner() {
        VBox banner = new VBox(4);
        banner.setPadding(new Insets(14, 24, 12, 24));
        banner.setStyle("-fx-background: linear-gradient(135deg, rgba(78,204,163,0.3), rgba(255,215,0,0.2)); " +
                "-fx-border-color: #4ecca3; -fx-border-width: 0 0 2 0;");
        banner.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎮 TIẾN LÊN ONLINE");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#FFD700"));

        Label subtitle = new Label("Tham gia bàn chơi hoặc tạo bàn mới của riêng bạn");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setStyle("-fx-text-fill: #4ecca3;");

        banner.getChildren().addAll(title, subtitle);
        return banner;
    }

    private VBox createLeftSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setStyle("-fx-background-color: transparent;");

        // ========== PROFILE CARD ==========
        VBox profileCard = createProfileCard();
        
        // ========== QUICK STATS ==========
        VBox quickStatsCard = createQuickStatsCard();

        sidebar.getChildren().addAll(profileCard, quickStatsCard);
        return sidebar;
    }

    private VBox createProfileCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: linear-gradient(135deg, rgba(78,204,163,0.15), rgba(255,215,0,0.08)); " +
                "-fx-border-color: #4ecca3; -fx-border-width: 2; -fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 4);");
        card.setStyle(card.getStyle() + " -fx-cursor: hand;");

        // Avatar Circle
        VBox avatarBox = new VBox();
        avatarBox.setPrefHeight(72);
        avatarBox.setAlignment(Pos.CENTER);
        Circle avatar = new Circle(34);
        avatar.setStyle("-fx-fill: linear-gradient(135deg, #4ecca3, #FFD700); " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 2);");
        Label avatarIcon = new Label("👤");
        avatarIcon.setFont(Font.font("Arial", 28));
        avatarIcon.setAlignment(Pos.CENTER);
        avatarBox.getChildren().add(avatar);
        
        StackPane avatarStack = new StackPane(avatar, avatarIcon);
        avatarStack.setAlignment(Pos.CENTER);

        // Player Info
        VBox playerInfo = new VBox(4);
        playerInfo.setAlignment(Pos.CENTER);

        Label playerNameLabel = new Label(username);
        playerNameLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        playerNameLabel.setStyle("-fx-text-fill: #ffffff;");

        Label levelLabel = new Label("🏆 LEVEL 5");
        levelLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11; -fx-font-weight: bold;");

        playerInfo.getChildren().addAll(playerNameLabel, levelLabel);

        // Balance Section
        VBox balanceSection = new VBox(4);
        balanceSection.setPadding(new Insets(10, 12, 10, 12));
        balanceSection.setStyle("-fx-background-color: rgba(255,215,0,0.1); " +
                "-fx-border-color: #FFD700; -fx-border-width: 1; -fx-border-radius: 10; " +
                "-fx-background-radius: 10;");
        balanceSection.setAlignment(Pos.CENTER);

        Label balanceTag = new Label("SỐ DƯ HIỆN TẠI");
        balanceTag.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 10; -fx-font-weight: bold; -fx-letter-spacing: 2;");

        balanceLabel = new Label("💰 (Đang cập nhật...)");
        balanceLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 19));
        balanceLabel.setStyle("-fx-text-fill: #FFD700;");

        balanceSection.getChildren().addAll(balanceTag, balanceLabel);

        // Click Hint
        Label hintLabel = new Label("👆 Bấm để xem thống kê cá nhân");
        hintLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 11; -fx-font-style: italic;");
        hintLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(avatarStack, playerInfo, balanceSection, hintLabel);
        card.setOnMouseClicked(e -> showMyStatistics());
        
        String normalStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(normalStyle.replace("rgba(78,204,163,0.15)", "rgba(78,204,163,0.25)")));
        card.setOnMouseExited(e -> card.setStyle(normalStyle));

        return card;
    }

    private VBox createQuickStatsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: rgba(78,204,163,0.1); " +
                "-fx-border-color: #4ecca3; -fx-border-width: 1; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);");

        Label title = new Label("📊 THỐNG KÊ NHANH");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12; -fx-font-weight: bold; -fx-letter-spacing: 1;");

        HBox stat1 = createStatRow("🎮 Tổng Ván", "0 ván");
        quickTotalGamesVal = (Label) stat1.getChildren().get(1);
        HBox stat2 = createStatRow("✅ Thắng", "0 ván");
        quickWinsVal = (Label) stat2.getChildren().get(1);
        HBox stat3 = createStatRow("📈 Tỉ Lệ", "0%");
        quickWinRateVal = (Label) stat3.getChildren().get(1);
        HBox stat4 = createStatRow("💸 Tổng Lãi", "0 VND");
        quickNetProfitVal = (Label) stat4.getChildren().get(1);

        card.getChildren().addAll(title, stat1, stat2, stat3, stat4);
        return card;
    }

    private HBox createStatRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12;");
        labelNode.setPrefWidth(100);

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold; -fx-font-size: 12;");
        HBox.setHgrow(valueNode, Priority.ALWAYS);
        valueNode.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private VBox createRightContent() {
        VBox content = new VBox(12);
        content.setStyle("-fx-background-color: transparent;");

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label contentTitle = new Label("📋 Danh sách bàn chơi");
        contentTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        contentTitle.setStyle("-fx-text-fill: #ffffff;");

        statusLabel = new Label("Đang tải...");
        statusLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 13;");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        statusLabel.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(contentTitle, statusLabel);

        // Table List
        tableListBox = new VBox(14);
        tableListBox.setPadding(new Insets(4));
        tableListBox.setStyle("-fx-background-color: transparent;");

        ScrollPane scrollPane = new ScrollPane(tableListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; " +
                "-fx-border-color: rgba(78,204,163,0.25); -fx-border-radius: 10;");
        scrollPane.setMinHeight(160);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(header, scrollPane);
        return content;
    }

    private HBox createUserInfoBox() {
        // Method này không dùng nữa - thay thế bằng createProfileCard
        return new HBox();
    }
    
    private void showMyStatistics() {
        Dialog<ButtonType> statsDialog = new Dialog<>();
        statsDialog.setTitle("Thống Kê Cá Nhân - " + username);
        statsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("📊 Thống Kê Cá Nhân Của " + username);
        titleLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 22));

        Label loadingLabel = new Label("Đang tải thống kê...");
        loadingLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-font-size: 14;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(18);
        statsGrid.setPadding(new Insets(20));
        statsGrid.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");

        content.getChildren().addAll(titleLabel, loadingLabel, statsGrid);
        statsDialog.getDialogPane().setContent(content);
        statsDialog.getDialogPane().setMinWidth(420);
        statsDialog.getDialogPane().setMinHeight(340);
        statsDialog.setResizable(true);

        // Yêu cầu thống kê từ server
        new Thread(() -> {
            try {
                Message msg = new Message(Protocol.GET_PLAYER_STATS, username);
                out.println(Protocol.encode(msg));
                Thread.sleep(1000);
                
                Platform.runLater(() -> {
                    loadingLabel.setVisible(false);
                    loadingLabel.setManaged(false);
                    
                    String[] stats = playerStatsCache.getOrDefault(username, 
                            new String[]{"0", "0", "0", "0", "0"});
                    
                    statsGrid.getChildren().clear();
                    int totalGames = Integer.parseInt(stats[0]);
                    int wins = Integer.parseInt(stats[1]);
                    double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;
                    
                    addStatRow(statsGrid, 0, "Tổng Ván Chơi", stats[0]);
                    addStatRow(statsGrid, 1, "Ván Thắng", stats[1]);
                    addStatRow(statsGrid, 2, "Ván Thua", stats[2]);
                    addStatRow(statsGrid, 3, "Tỉ Lệ Thắng", String.format("%.1f%%", winRate));
                    addStatRow(statsGrid, 4, "Tổng Tiền Thắng/Thua", (stats[4].startsWith("-") ? "" : "+") + stats[4] + " VND");

                    if (statsDialog.getDialogPane().getScene() != null && statsDialog.getDialogPane().getScene().getWindow() != null) {
                        statsDialog.getDialogPane().getScene().getWindow().sizeToScene();
                    }
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> loadingLabel.setText("Lỗi tải thống kê"));
            }
        }).start();

        statsDialog.showAndWait();
    }

    private HBox createControlBox() {
        HBox box = new HBox(16);
        box.setAlignment(Pos.CENTER);

        Button createTableBtn = new Button("➕ TẠO BÀN MỚI");
        styleButton(createTableBtn, "#4CAF50", "#66BB6A");
        createTableBtn.setOnAction(e -> requestNewTable());
        createTableBtn.setPrefWidth(200);
        createTableBtn.setPrefHeight(40);
        createTableBtn.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 13));

        Button refreshBtn = new Button("🔄 LÀM MỚI");
        styleButton(refreshBtn, "#2196F3", "#42A5F5");
        refreshBtn.setOnAction(e -> refreshTablesList());
        refreshBtn.setPrefWidth(200);
        refreshBtn.setPrefHeight(40);
        refreshBtn.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 13));

        Button quitBtn = new Button("❌ QUAY LẠI");
        styleButton(quitBtn, "#F44336", "#EF5350");
        quitBtn.setOnAction(e -> {
            if (onQuit != null) onQuit.run();
        });
        quitBtn.setPrefWidth(200);
        quitBtn.setPrefHeight(40);
        quitBtn.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 13));

        box.getChildren().addAll(createTableBtn, refreshBtn, quitBtn);
        return box;
    }

    
    private void styleButton(Button btn, String color, String hoverColor) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20;"));
    }

    private void requestTablesList() {
        new Thread(() -> {
            try {
                Message msg = new Message(Protocol.GET_SESSIONS, "");
                out.println(Protocol.encode(msg));
            } catch (Exception e) {
                Platform.runLater(() -> updateStatus("❌ Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    // Public method để refresh danh sách từ ngoài
    public void refreshTablesList() {
        requestTablesList();
        if (out != null && username != null && !username.isEmpty()) {
            out.println(Protocol.encode(new Message(Protocol.GET_PLAYER_STATS, username)));
        }
    }

    private void requestNewTable() {
        // Hiển thị dialog để nhập tên bàn và mức cược
        Dialog<javafx.util.Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Tạo Bàn Mới");

        // Set the button types.
        ButtonType createButtonType = new ButtonType("Tạo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the name and bet fields.
        TextField nameField = new TextField();
        nameField.setPromptText("Tên bàn (ví dụ: bàn 1)");
        TextField betField = new TextField("10000");
        betField.setPromptText("Mức cược (VND)");

        VBox content = new VBox(10, new Label("Tên bàn:"), nameField, new Label("Mức cược (VND):"), betField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new javafx.util.Pair<>(nameField.getText().trim(), betField.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String name = result.getKey();
            String bet = result.getValue();
            if (name == null) name = "";
            if (bet == null || bet.isEmpty()) bet = "10000";
            Message msg = new Message("CREATE_SESSION", "name=" + name + "&bet=" + bet);
            out.println(Protocol.encode(msg));
            requestTablesList();
        });
    }

    // Được gọi từ ServerListener để cập nhật danh sách bàn
    public void updateSessionList(String sessionListData) {
        Platform.runLater(() -> {
            tableListBox.getChildren().clear();
            tables.clear();

            if (sessionListData == null || sessionListData.isEmpty()) {
                Label emptyLabel = new Label("📭 Không có bàn chơi nào. Tạo bàn mới?");
                emptyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
                tableListBox.getChildren().add(emptyLabel);
                updateStatus("✅ Đã cập nhật danh sách (trống)");
                return;
            }

            // Parse dữ liệu: sessionId,playerCount,status;...
            String[] sessionArray = sessionListData.split(";");
            for (String sessionData : sessionArray) {
            	System.out.println("hehe: "+sessionData);
                if (sessionData.isEmpty()) continue;
                
                String[] parts = sessionData.split(",");
                    if (parts.length >= 5) {
                    String sessionId = parts[0];
                    int playerCount = Integer.parseInt(parts[1]);
                    String status = parts[2];
                    String displayName = parts[3];
                    long bet = 10000;
                    try { bet = Long.parseLong(parts[4]); } catch (NumberFormatException ex) {}

                    TableInfo info = new TableInfo(sessionId, playerCount, status, displayName, bet);
                    tables.put(sessionId, info);

                    HBox tableRow = createTableRow(info);
                    tableListBox.getChildren().add(tableRow);
                }
            }

            updateStatus("✅ Đã cập nhật danh sách (" + tables.size() + " bàn)");
        });
    }
    
    public void updateSessionDetails(String detailsData) {
        // Format: playerName1,balance1;playerName2,balance2;...
        if (detailsData == null || detailsData.isEmpty()) {
            return;
        }
        
        // Tìm cái gì đang được hiển thị để cập nhật
        if (selectedSessionId != null && tables.containsKey(selectedSessionId)) {
            TableInfo info = tables.get(selectedSessionId);
            String[] playerDetails = detailsData.split(";");
            
            // Cập nhật thông tin người chơi
            for (int i = 0; i < Math.min(playerDetails.length, 4); i++) {
                String[] parts = playerDetails[i].split(",");
                if (parts.length >= 2) {
                    String playerName = parts[0];
                    long playerBalance = Long.parseLong(parts[1]);
                    
                    if (i < info.playerNames.length) {
                        info.playerNames[i] = playerName;
                        info.playerBalances[i] = playerBalance;
                    }
                }
            }
        }
    }

    private HBox createTableRow(TableInfo info) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #1e5a5a; " +
                "-fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.45), 10, 0, 0, 3); " +
                "-fx-cursor: hand;");

        // Status Icon
        VBox statusBox = new VBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(56);
        
        String statusEmoji = info.status.equals("RUNNING") ? "⏱️" : "🟢";
        Label statusIcon = new Label(statusEmoji);
        statusIcon.setFont(Font.font("Arial", 34));
        statusBox.getChildren().add(statusIcon);

        // Main Info
        VBox mainInfo = new VBox(10);
        
        Label tableNameLabel = new Label(info.displayName != null && !info.displayName.isEmpty() ? 
                info.displayName : ("Bàn #" + info.sessionId.substring(0, 6)));
        tableNameLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 20));
        tableNameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        HBox statsRow = new HBox(25);
        
        VBox playersStat = new VBox(3);
        playersStat.setAlignment(Pos.CENTER_LEFT);
        Label playersTag = new Label("NGƯỜI CHƠI");
        playersTag.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11; -fx-font-weight: bold; -fx-letter-spacing: 1;");
        Label playersVal = new Label("👥 " + info.playerCount + "/4");
        playersVal.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16; -fx-font-weight: bold;");
        playersStat.getChildren().addAll(playersTag, playersVal);

        VBox betStat = new VBox(3);
        betStat.setAlignment(Pos.CENTER_LEFT);
        Label betTag = new Label("MỨC CƯỢC");
        betTag.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11; -fx-font-weight: bold; -fx-letter-spacing: 1;");
        Label betVal = new Label("💰 " + String.format("%,d", info.betAmount));
        betVal.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16; -fx-font-weight: bold;");
        betStat.getChildren().addAll(betTag, betVal);

        VBox statusStat = new VBox(3);
        statusStat.setAlignment(Pos.CENTER_LEFT);
        Label statusTag = new Label("TRẠNG THÁI");
        statusTag.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11; -fx-font-weight: bold; -fx-letter-spacing: 1;");
        String statusText = info.status.equals("RUNNING") ? "⏱️ Đang chơi" : "⏳ Chờ người";
        String statusColor = info.status.equals("RUNNING") ? "#FF6B6B" : "#4ecca3";
        Label statusVal = new Label(statusText);
        statusVal.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 16; -fx-font-weight: bold;");
        statusStat.getChildren().addAll(statusTag, statusVal);

        statsRow.getChildren().addAll(playersStat, betStat, statusStat);
        
        mainInfo.getChildren().addAll(tableNameLabel, statsRow);
        HBox.setHgrow(mainInfo, Priority.ALWAYS);

        // Action Buttons
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPrefWidth(160);
        
        Button infoBtn = new Button("Chi Tiết");
        infoBtn.setPrefWidth(145);
        infoBtn.setPrefHeight(38);
        infoBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13;");
        infoBtn.setOnAction(e -> {
            selectedSessionId = info.sessionId;
            Message msg = new Message(Protocol.GET_SESSION_DETAILS, info.sessionId);
            out.println(Protocol.encode(msg));
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    Platform.runLater(() -> showTableDetails(info));
                } catch (InterruptedException ex) {
                    Platform.runLater(() -> showTableDetails(info));
                }
            }).start();
        });

        Button selectBtn = new Button("Chơi");
        selectBtn.setPrefWidth(145);
        selectBtn.setPrefHeight(38);
        boolean canSelect = info.playerCount < 4 && !info.status.equals("RUNNING");
        selectBtn.setDisable(!canSelect);

        if (canSelect) {
            selectBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13;");
        } else {
            selectBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: #CCCCCC; -fx-font-weight: bold; " +
                    "-fx-padding: 8 15; -fx-background-radius: 6; -fx-font-size: 13;");
        }

        selectBtn.setOnAction(e -> {
            selectedSessionId = info.sessionId;
            selectTable(info.sessionId);
        });

        btnBox.getChildren().addAll(infoBtn, selectBtn);

        card.getChildren().addAll(statusBox, mainInfo, btnBox);
        return card;
    }

       

    private void showTableDetails(TableInfo info) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chi Tiết Bàn Chơi");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f0f0f0;");

        // Header
        Label titleLabel = new Label("🏓 " + (info.displayName != null && !info.displayName.isEmpty() ? 
                info.displayName : ("Bàn #" + info.sessionId.substring(0, 6))));
        titleLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 22));

        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: #4ecca3; -fx-padding: 15; -fx-border-radius: 5;");
        Label statusLabel = new Label(info.status.equals("RUNNING") ? "⏱️ ĐANG CHƠI" : "⏳ ĐỢI NGƯỜI");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        Label betLabel = new Label("Mức cược: " + String.format("%,d", info.betAmount) + " VND");
        betLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        headerBox.getChildren().addAll(statusLabel, new Separator(), betLabel);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        // Thông tin người chơi
        VBox playersBox = new VBox(12);
        playersBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: white;");
        
        Label playersTitle = new Label("👥 Người Chơi (" + info.playerCount + "/4):");
        playersTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        playersBox.getChildren().add(playersTitle);

        // Nếu có thông tin người chơi
        if (info.playerNames != null && info.playerCount > 0 && info.playerNames[0] != null) {
            int displayIdx = 0;
            for (int i = 0; i < info.playerCount && i < info.playerNames.length; i++) {

                String playerName = info.playerNames[i];
                if (playerName == null || playerName.trim().isEmpty()) continue;
                displayIdx++;
                long playerBalance = info.playerBalances[i];

                HBox playerRow = new HBox(20);
                playerRow.setStyle("-fx-padding: 12; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
                playerRow.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(displayIdx + ". " + playerName);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                nameLabel.setPrefWidth(160);

                Label balanceLabel = new Label("💰 " + String.format("%,d", playerBalance) + " VND");
                balanceLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold; -fx-font-size: 14;");

                Button playerDetailsBtn = new Button("Xem Thống Kê");
                playerDetailsBtn.setPrefWidth(130);
                playerDetailsBtn.setPrefHeight(35);
                playerDetailsBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 12; -fx-font-weight: bold;");
                playerDetailsBtn.setOnAction(e -> showPlayerStatistics(playerName));

                playerRow.getChildren().addAll(nameLabel, balanceLabel, playerDetailsBtn);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);

                playersBox.getChildren().add(playerRow);
            }
        } else {
            Label emptyLabel = new Label("(Chưa có thông tin người chơi)");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 13;");
            playersBox.getChildren().add(emptyLabel);
        }

        content.getChildren().addAll(titleLabel, headerBox, playersBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(480);
        dialog.setResizable(true);

        dialog.showAndWait();
    }

    private void showPlayerStatistics(String playerName) {
        Dialog<ButtonType> statsDialog = new Dialog<>();
        statsDialog.setTitle("Thống Kê - " + playerName);
        statsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("📊 Thống Kê Của " + playerName);
        titleLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 20));

        Label loadingLabel = new Label("Đang tải thống kê...");
        loadingLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-font-size: 14;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(18);
        statsGrid.setPadding(new Insets(20));
        statsGrid.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");

        content.getChildren().addAll(titleLabel, loadingLabel, statsGrid);
        statsDialog.getDialogPane().setContent(content);
        statsDialog.getDialogPane().setMinWidth(440);
        statsDialog.getDialogPane().setMinHeight(380);
        statsDialog.setResizable(true);

        // Yêu cầu thống kê từ server trên thread riêng
        new Thread(() -> {
            try {
                Message msg = new Message(Protocol.GET_PLAYER_STATS, playerName);
                out.println(Protocol.encode(msg));
                
                // Giả sử server sẽ gửi lại thống kê
                // Tạm thời sử dụng giá trị mặc định
                Thread.sleep(1000);
                
                Platform.runLater(() -> {
                    loadingLabel.setVisible(false);
                    loadingLabel.setManaged(false);
                    
                    String[] stats = playerStatsCache.getOrDefault(playerName, 
                            new String[]{"0", "0", "0", "0%", "0", "0"});
                    
                    statsGrid.getChildren().clear();
                    int totalGames = Integer.parseInt(stats[0]);
                    int wins = Integer.parseInt(stats[1]);
                    int losses = Integer.parseInt(stats[2]);
                    double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;
                    
                    addStatRow(statsGrid, 0, "Tổng Ván", stats[0]);
                    addStatRow(statsGrid, 1, "Ván Thắng", stats[1]);
                    addStatRow(statsGrid, 2, "Ván Thua", stats[2]);
                    addStatRow(statsGrid, 3, "Tỉ Lệ Thắng", String.format("%.1f%%", winRate));
                    addStatRow(statsGrid, 4, "Tổng Tiền Thắng", stats[3].startsWith("-") ? stats[3] : "+" + stats[3] + " VND");
                    addStatRow(statsGrid, 5, "Tổng Tiền Thua", stats[4].startsWith("-") ? stats[4] : "-" + stats[4] + " VND");

                    if (statsDialog.getDialogPane().getScene() != null && statsDialog.getDialogPane().getScene().getWindow() != null) {
                        statsDialog.getDialogPane().getScene().getWindow().sizeToScene();
                    }
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> loadingLabel.setText("Lỗi tải thống kê"));
            }
        }).start();

        statsDialog.showAndWait();
    }

    private void addStatRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label + ":");
        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 16; -fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void selectTable(String sessionId) {
        Message msg = new Message(Protocol.SELECT_SESSION, sessionId);
        out.println(Protocol.encode(msg));

        if (onTableSelected != null) {
            // Gọi callback sau khi chọn bàn
            Platform.runLater(onTableSelected);
        }
    }

    private void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public void setOnTableSelected(Runnable callback) {
        this.onTableSelected = callback;
    }

    public void setOnQuit(Runnable callback) {
        this.onQuit = callback;
    }
    
    public void updateBalance(long balance) {
        if (balanceLabel != null) {
            Platform.runLater(() -> {
                balanceLabel.setText("💰 " + String.format("%,d", balance) + " VND");
            });
        }
    }

    public String getSelectedSessionId() {
        return selectedSessionId;
    }

    public TableInfo getSelectedTableInfo() {
        if (selectedSessionId == null) return null;
        return tables.get(selectedSessionId);
    }

    // Cập nhật cache thống kê từ server
    public void updatePlayerStats(String playerName, String statsData) {
        // statsData format: totalGames,wins,losses,totalWon,totalBet
        String[] parts = statsData.split(",");
        if (parts.length >= 5) {
            playerStatsCache.put(playerName, parts);
            if (playerName.equals(username) && quickTotalGamesVal != null) {
                try {
                    int totalGames = Integer.parseInt(parts[0]);
                    int wins = Integer.parseInt(parts[1]);
                    long totalWon = Long.parseLong(parts[3]);
                    long totalBet = Long.parseLong(parts[4]);
                    long netProfit = totalWon - totalBet;
                    double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;

                    quickTotalGamesVal.setText(totalGames + " ván");
                    quickWinsVal.setText(wins + " ván");
                    quickWinRateVal.setText(String.format("%.1f%%", winRate));
                    quickNetProfitVal.setText((netProfit >= 0 ? "+" : "") + String.format("%,d", netProfit) + " VND");
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
