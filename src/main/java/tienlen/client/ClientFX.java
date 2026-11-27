package tienlen.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;	
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tienlen.model.Message;
import tienlen.utils.PasswordUtil;
import tienlen.utils.Protocol;

import java.io.*;
import java.net.Socket;

public class ClientFX extends Application {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private TextField ipField;
    private Button connectBtn;
    private TextField nameField;
    private Button loginBtn; 
    private TextArea logArea;
    private String myname;
    private PasswordField passwordField;
    private Button registerBtn;
    private Button submitRegBtn;
    private TableSelectionUI tableSelectionUI;
    private Stage primaryStage;
    private ServerListener universalListener;  // Listener duy nhất cho tất cả

    private VBox buildLoginRoot() {
        Label title = new Label("🎮 TIẾN LÊN MIỀN NAM");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#FFD700"));

        Label subtitle = new Label("Đăng nhập để tham gia sảnh chơi trực tuyến");
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setStyle("-fx-text-fill: #4ecca3;");

        ipField = new TextField("localhost");
        ipField.setPrefWidth(160);
        ipField.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #4ecca3; -fx-border-radius: 6; -fx-background-radius: 6;");
        connectBtn = new Button("Kết nối");
        styleButton(connectBtn, "#4ecca3", "#6ee7b7");

        Label ipLabel = new Label("Server IP:");
        ipLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
        HBox ipBox = new HBox(10, ipLabel, ipField, connectBtn);
        ipBox.setAlignment(Pos.CENTER);

        nameField = new TextField();
        nameField.setPromptText("Tên đăng nhập");
        nameField.setPrefWidth(180);
        nameField.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");
        passwordField.setPrefWidth(180);
        passwordField.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;");

        loginBtn = new Button("ĐĂNG NHẬP");
        styleButton(loginBtn, "#2196F3", "#42A5F5");
        loginBtn.setDisable(true);

        registerBtn = new Button("ĐĂNG KÝ");
        styleButton(registerBtn, "#FF9800", "#FFB74D");
        registerBtn.setDisable(true);

        GridPane loginGrid = new GridPane();
        loginGrid.setHgap(12);
        loginGrid.setVgap(10);
        loginGrid.setAlignment(Pos.CENTER);

        Label userLbl = new Label("Tên đăng nhập:");
        userLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
        Label passLbl = new Label("Mật khẩu:");
        passLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
        loginGrid.add(userLbl, 0, 0);
        loginGrid.add(nameField, 1, 0);
        loginGrid.add(passLbl, 0, 1);
        loginGrid.add(passwordField, 1, 1);

        HBox btnBox = new HBox(14, loginBtn, registerBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox loginCard = new VBox(14, loginGrid, btnBox);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(16));
        loginCard.setStyle("-fx-background-color: rgba(78,204,163,0.08); -fx-border-color: #4ecca3; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(130);
        logArea.setMinHeight(90);
        logArea.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #e2e8f0; -fx-font-family: 'Consolas'; -fx-font-size: 11pt; -fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox root = new VBox(12, title, subtitle, ipBox, loginCard, logArea);
        root.setPadding(new Insets(18, 22, 18, 22));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e27, #1a1a3e);");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        connectBtn.setOnAction(e -> connectToServer());
        ipField.setOnAction(e -> connectToServer());
        loginBtn.setOnAction(e -> loginGame());
        nameField.setOnAction(e -> { if (!loginBtn.isDisabled()) loginGame(); });
        passwordField.setOnAction(e -> { if (!loginBtn.isDisabled()) loginGame(); });
        registerBtn.setOnAction(e -> showRegisterScreen());

        return root;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        VBox root = buildLoginRoot();

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double w = Math.min(540, visualBounds.getWidth() * 0.85);
        double h = Math.min(470, visualBounds.getHeight() * 0.85);
        Scene scene = new Scene(root, w, h);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tiến Lên Client");
        primaryStage.setMinWidth(460);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // Hàm style nút đẹp hơn với hover
    private void styleButton(Button btn, String color, String hoverColor) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
    }

  
    private void connectToServer() {
        String host = ipField.getText().trim();
        int port = 12345;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            log("✅ Kết nối thành công tới server " + host + ":" + port);

            // Khởi động ServerListener duy nhất để xử lý TẤT CẢ tin nhắn từ server
            // (bao gồm cả register/login/table selection/game messages)
            universalListener = new ServerListener(in, this);
            Thread listenerThread = new Thread(universalListener);
            listenerThread.setDaemon(true);
            listenerThread.start();

            loginBtn.setDisable(false);
            registerBtn.setDisable(false);

            connectBtn.setDisable(true);
            ipField.setDisable(true);

        } catch (IOException ex) {
            log("❌ Kết nối thất bại: " + ex.getMessage());
        }
    }

    private void loginGame() {
        String username = nameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            log("⚠ Vui lòng nhập đầy đủ tên và mật khẩu.");
            return;
        }

        String hashedPassword = PasswordUtil.hashSHA256(password);
 
        Message ms = new Message(
        	    Protocol.LOGIN,
        	    "user=" + username + "&pass=" + hashedPassword
        	);

        out.println(Protocol.encode(ms));

        log("▶ Đang đăng nhập...");

        loginBtn.setDisable(true);
        registerBtn.setDisable(true);
        nameField.setDisable(true);
        passwordField.setDisable(true);
    }

    public void showRegisterScreen() {
        Label title = new Label("📝 ĐĂNG KÝ TÀI KHOẢN");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#FFD700"));

        String inputStyle = "-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;";
        String labelStyle = "-fx-text-fill: #e2e8f0; -fx-font-weight: bold;";

        TextField regUsername = new TextField();
        regUsername.setPromptText("Tên đăng nhập");
        regUsername.setPrefWidth(200);
        regUsername.setStyle(inputStyle);

        PasswordField regPassword = new PasswordField();
        regPassword.setPromptText("Mật khẩu (tối thiểu 6 ký tự)");
        regPassword.setPrefWidth(200);
        regPassword.setStyle(inputStyle);

        PasswordField regPasswordConfirm = new PasswordField();
        regPasswordConfirm.setPromptText("Xác nhận mật khẩu");
        regPasswordConfirm.setPrefWidth(200);
        regPasswordConfirm.setStyle(inputStyle);

        TextArea regStatus = new TextArea();
        regStatus.setEditable(false);
        regStatus.setPrefHeight(90);
        regStatus.setStyle("-fx-font-size: 11pt; -fx-control-inner-background: #0f172a; -fx-text-fill: #e2e8f0; -fx-border-color: #334155; -fx-border-radius: 8;");

        GridPane regGrid = new GridPane();
        regGrid.setHgap(12);
        regGrid.setVgap(10);
        regGrid.setAlignment(Pos.CENTER);
        Label uLbl = new Label("Tên đăng nhập:"); uLbl.setStyle(labelStyle);
        Label pLbl = new Label("Mật khẩu:"); pLbl.setStyle(labelStyle);
        Label cLbl = new Label("Xác nhận mật khẩu:"); cLbl.setStyle(labelStyle);
        regGrid.add(uLbl, 0, 0);
        regGrid.add(regUsername, 1, 0);
        regGrid.add(pLbl, 0, 1);
        regGrid.add(regPassword, 1, 1);
        regGrid.add(cLbl, 0, 2);
        regGrid.add(regPasswordConfirm, 1, 2);

        submitRegBtn = new Button("ĐĂNG KÝ");
        styleButton(submitRegBtn, "#4ecca3", "#6ee7b7");

        Button backBtn = new Button("QUAY LẠI");
        styleButton(backBtn, "#F44336", "#EF5350");

        HBox btnBox = new HBox(15, submitRegBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox regCard = new VBox(14, regGrid, btnBox);
        regCard.setPadding(new Insets(16));
        regCard.setAlignment(Pos.CENTER);
        regCard.setStyle("-fx-background-color: rgba(78,204,163,0.08); -fx-border-color: #4ecca3; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        VBox root = new VBox(14, title, regCard, regStatus);
        root.setPadding(new Insets(18, 22, 18, 22));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e27, #1a1a3e);");
        VBox.setVgrow(regStatus, Priority.ALWAYS);

        Runnable doRegister = () -> {
            String username = regUsername.getText().trim();
            String password = regPassword.getText().trim();
            String passwordConfirm = regPasswordConfirm.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                regStatus.setText("❌ Vui lòng nhập đầy đủ thông tin");
                return;
            }

            if (!password.equals(passwordConfirm)) {
                regStatus.setText("❌ Mật khẩu xác nhận không khớp");
                return;
            }

            if (password.length() < 6) {
                regStatus.setText("❌ Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }

            regStatus.setText("⏳ Đang đăng ký...");
            submitRegBtn.setDisable(true);

            String hashedPassword = PasswordUtil.hashSHA256(password);
            Message msg = new Message(Protocol.REGISTER, "user=" + username + "&pass=" + hashedPassword);
            out.println(Protocol.encode(msg));
        };

        submitRegBtn.setOnAction(e -> doRegister.run());
        regUsername.setOnAction(e -> doRegister.run());
        regPassword.setOnAction(e -> doRegister.run());
        regPasswordConfirm.setOnAction(e -> doRegister.run());

        backBtn.setOnAction(e -> showLoginScreen());

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double w = Math.min(540, visualBounds.getWidth() * 0.85);
        double h = Math.min(470, visualBounds.getHeight() * 0.85);
        Scene scene = new Scene(root, w, h);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showLoginScreen() {
        VBox root = buildLoginRoot();
        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double w = Math.min(540, visualBounds.getWidth() * 0.85);
        double h = Math.min(470, visualBounds.getHeight() * 0.85);
        Scene scene = new Scene(root, w, h);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        log("▶ Quay lại màn hình đăng nhập");
    }

    // Callback từ AuthListener khi đăng nhập thành công
    public void onLoginSuccess(String username) {
        this.myname = username;
        log("✅ Đăng nhập thành công: " + username);
        showTableSelection();
    }

    // Re-enable login controls khi đăng nhập thất bại
    public void enableLoginControls() {
        loginBtn.setDisable(false);
        registerBtn.setDisable(false);
        nameField.setDisable(false);
        passwordField.setDisable(false);
    }

    // Re-enable register controls khi đăng ký thất bại
    public void enableRegisterControls() {
        if (submitRegBtn != null) {
            submitRegBtn.setDisable(false);
        }
    }

  
// Hàm hiển thị giao diện chọn bàn chơi
    private void showTableSelection() {
        tableSelectionUI = new TableSelectionUI(out, in, myname);
        TableSelectionUI tableSelectionUI = this.tableSelectionUI;
        javafx.scene.layout.BorderPane tableSelectionPane = tableSelectionUI.createRootPane();
        
        // Cập nhật listener để xử lý TABLE_SELECTION
        universalListener.setTableSelectionUI(tableSelectionUI, myname);
        
        tableSelectionUI.setOnTableSelected(() -> {
            // Sau khi chọn bàn, chuyển sang giao diện game
            Platform.runLater(this::showGameTable);
        });
        
        tableSelectionUI.setOnQuit(() -> {
            // Đóng socket cũ để Server dọn phiên online, rồi quay lại màn hình login
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {}
            showLoginScreen();
        });
        
        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double tableWidth = Math.min(1150, visualBounds.getWidth() * 0.88);
        double tableHeight = Math.min(740, visualBounds.getHeight() * 0.88);
        Scene tableScene = new Scene(tableSelectionPane, tableWidth, tableHeight);
        primaryStage.setScene(tableScene);
        primaryStage.centerOnScreen();
        log("▶ Hiển thị giao diện chọn bàn chơi");
        
        // Yêu cầu danh sách bàn sau khi UI đã sẵn sàng
        tableSelectionUI.refreshTablesList();
    }

    // Hàm tạo giao diện bàn chơi
    private void showGameTable() {
        // Try to get selected table info (displayName & bet) from previous selection UI
        String displayName = "";
        long bet = 10000;
        if (tableSelectionUI != null) {
            TableSelectionUI.TableInfo info = tableSelectionUI.getSelectedTableInfo();
            if (info != null) {
                displayName = info.displayName;
                bet = info.betAmount;
            }
        }

        TienLenClientUI gameTable = new TienLenClientUI(out, in, myname, displayName, bet);
        // Set callback when user exits the table
        gameTable.setOnExit(() -> {
            // Return to table selection screen
            Platform.runLater(() -> showTableSelection());
        });

        javafx.scene.layout.BorderPane root = gameTable.createRootPane();
        // Đăng ký gameUI ngay sau khi các thành phần UI đã được khởi tạo, trước khi setScene
        universalListener.setGameUI(gameTable, myname);

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double gameWidth = Math.min(1360, visualBounds.getWidth() * 0.92);
        double gameHeight = Math.min(780, visualBounds.getHeight() * 0.90);
        Scene gameScene = new Scene(root, gameWidth, gameHeight);
        gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(gameScene);
        primaryStage.centerOnScreen();

        // Chủ động yêu cầu danh sách người chơi hiện tại khi UI phòng chơi đã sẵn sàng
        if (out != null) {
            out.println(Protocol.encode(new Message("GET_PLAYER_LIST", "")));
        }
    }

    public void returnToTableSelection() {
        Platform.runLater(this::showTableSelection);
    }

 
    
//    private void listenServer() {
//        String line;
//        try {
//            while ((line = in.readLine()) != null) {
//                String message = line;
//                Platform.runLater(() -> log("Server: " + message));
//            }
//        } catch (IOException e) {
//            Platform.runLater(() -> log("❌ Lỗi kết nối server: " + e.getMessage()));
//        }
//    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }

    public static void main(String[] args) {
        launch();
    }
}
