# Báo cáo Khảo sát nhanh (Lượt A) — TienLenMienNamTCP

## 1. Cấu trúc project

### Cây thư mục (đã bỏ qua `.git`, `target` và các thư mục sinh tự động)

```text
TienLenMienNamTCP/
├── .github/
│   └── copilot-instructions.md
├── .settings/
│   ├── org.eclipse.core.resources.prefs
│   ├── org.eclipse.jdt.apt.core.prefs
│   ├── org.eclipse.jdt.core.prefs
│   └── org.eclipse.m2e.core.prefs
├── src/
│   └── main/
│       ├── java/
│       │   ├── Test.java
│       │   ├── TestProtocol.java
│       │   └── tienlen/
│       │       ├── client/
│       │       │   ├── ClientFX.java
│       │       │   ├── ClientMain.java
│       │       │   ├── ServerListener.java
│       │       │   ├── TableSelectionUI.java
│       │       │   └── TienLenClientUI.java
│       │       ├── logic/
│       │       │   └── GameLogic.java
│       │       ├── model/
│       │       │   ├── Card.java
│       │       │   ├── Deck.java
│       │       │   ├── GameState.java
│       │       │   ├── Message.java
│       │       │   ├── Move.java
│       │       │   └── Player.java
│       │       ├── server/
│       │       │   ├── ClientHandler.java
│       │       │   ├── GameSession.java
│       │       │   ├── PlayerInfo.java
│       │       │   ├── ServerBackend.java
│       │       │   ├── ServerMain.java
│       │       │   ├── ServerUI.java
│       │       │   ├── SessionInfo.java
│       │       │   └── UserManager.java
│       │       └── utils/
│       │           ├── PasswordUtil.java
│       │           └── Protocol.java
│       └── resources/
│           └── style.css
├── .classpath
├── .project
├── BaoCaoDACS3.md
├── DATABASE_SCHEMA.md
├── build.sh
├── fix_changes.txt
├── fix_issues.py
├── nbactions.xml
├── pom.xml
├── README.md
├── run_client.bat
├── run_server.bat
└── tienlencards.db
```

### Vai trò các thư mục và file quan trọng

| Thành phần | Vai trò | Bằng chứng |
|---|---|---|
| `pom.xml` | Cấu hình build Maven, khai báo Java 17 và thư viện JavaFX, MySQL JDBC | `pom.xml` (lines 10-33) |
| `src/main/java/tienlen/client/` | Giao diện JavaFX phía người chơi (đăng nhập, chọn bàn, bàn chơi) và luồng lắng nghe server | `ClientFX.java`, `TableSelectionUI.java`, `TienLenClientUI.java`, `ServerListener.java` |
| `src/main/java/tienlen/server/` | Server TCP, quản lý kết nối client, phiên chơi, tài khoản và giao diện quản trị server | `ServerMain.java`, `ServerUI.java`, `ServerBackend.java`, `ClientHandler.java`, `GameSession.java`, `UserManager.java`, `PlayerInfo.java`, `SessionInfo.java` |
| `src/main/java/tienlen/logic/` | Xử lý luật chơi Tiến Lên Miền Nam (nhận diện bộ bài, kiểm tra chặt bài) | `GameLogic.java` (lines 10-60) |
| `src/main/java/tienlen/model/` | Các lớp dữ liệu cốt lõi (`Card`, `Deck`, `Player`, `Move`, `Message`; riêng `GameState.java` bị comment toàn bộ) | `Card.java` (lines 3-15), `GameState.java` (lines 1-25) |
| `src/main/java/tienlen/utils/` | Quy ước giao thức truyền thông điệp và hàm băm mật khẩu | `Protocol.java` (lines 5-57), `PasswordUtil.java` (lines 6-22) |
| `src/main/resources/style.css` | File CSS định dạng giao diện JavaFX | `.github/copilot-instructions.md` (line 153) |
| `run_server.bat`, `run_client.bat`, `build.sh` | Script hỗ trợ biên dịch/chạy trực tiếp bằng `java`/`javac` | `run_server.bat` (lines 1-8), `run_client.bat` (lines 1-7), `build.sh` (lines 1-15) |
| `DATABASE_SCHEMA.md` | Tài liệu mô tả thiết kế các bảng MySQL | `DATABASE_SCHEMA.md` (lines 1-10) |
| `BaoCaoDACS3.md` | Báo cáo Đồ án cơ sở 3 (nằm ở thư mục gốc, không nằm trong `docs/`) | `BaoCaoDACS3.md` (lines 1-12) |

### Entry point của ứng dụng

- **Client**: `tienlen.client.ClientFX` (lớp `Application` của JavaFX).
  - Bằng chứng: `src/main/java/tienlen/client/ClientFX.java` (`public class ClientFX extends Application`, line 20) và `src/main/java/tienlen/client/ClientMain.java` (lines 4-10: `"Điểm khởi chạy của ứng dụng Client là ClientFX.java."`).
  - Lưu ý: `pom.xml` (line 49) ghi `<mainClass>tienlen.clientfx.GameClientFX</mainClass>`, không khớp với tên lớp thực tế `tienlen.client.ClientFX`.
- **Server**: `tienlen.server.ServerUI` (giao diện quản trị JavaFX khởi tạo `ServerBackend`) hoặc `tienlen.server.ServerMain` (gọi lại `ServerUI.main(args)`).
  - Bằng chứng: `src/main/java/tienlen/server/ServerMain.java` (`main`, lines 24-27: `ServerUI.main(args);`), `src/main/java/tienlen/server/ServerUI.java` (`public class ServerUI extends Application`, line 24), `run_server.bat` (line 7: `tienlen.server.ServerUI`).

### Git và README

- **Git**: Có dùng Git (thư mục `.git` tồn tại ở gốc project, kết quả `Test-Path .git` trả về `True`).
- **README**: Có file `README.md` ở thư mục gốc, nội dung 1 dòng: `Tien Len TCP v3 - JavaFX selectable cards + Swing client` (`README.md`, line 1).

---

## 2. Công nghệ

- **Ngôn ngữ lập trình và phiên bản**:
  - Java phiên bản **17** theo cấu hình biên dịch trong `pom.xml` (lines 11-12, 42: `<maven.compiler.source>17</maven.compiler.source>`, `<maven.compiler.target>17</maven.compiler.target>`, `<release>17</release>`).
  - Trên máy hiện tại ghi nhận `java version "24.0.1"`.
- **Framework và thư viện chính** (theo `pom.xml`):
  - `org.openjfx:javafx-controls` phiên bản **21.0.3** (`pom.xml`, lines 14, 19-22).
  - `org.openjfx:javafx-graphics` phiên bản **21.0.3** (`pom.xml`, lines 14, 24-27).
  - `com.mysql:mysql-connector-j` phiên bản **8.2.0** (`pom.xml`, lines 30-33).
  - Maven plugins: `maven-compiler-plugin` **3.11.0**, `javafx-maven-plugin` **0.0.8**, `exec-maven-plugin` **3.1.0** (`pom.xml`, lines 38-56).
  - Thư viện chuẩn Java tích hợp: `java.net.ServerSocket` / `Socket` (TCP), `java.security.MessageDigest` (SHA-256), `javax.sound.sampled.*` (thu âm voice chat), `javafx.scene.media.MediaPlayer` (phát voice chat).
- **Database và cách kết nối**:
  - Loại database: **MySQL**, tên database: `tienlencards` (`UserManager.java`, line 8: `DB_URL = "jdbc:mysql://localhost:3306/tienlencards"`).
  - Cách kết nối: Kết nối trực tiếp qua `DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)` bằng JDBC trong `UserManager.java` (lines 29, 36, 147...). Không dùng connection pool.
  - Lưu ý: Có file `tienlencards.db` (16.384 bytes) ở thư mục gốc, nhưng không thấy code tham chiếu tới SQLite.
- **Kiến trúc tổng thể**:
  - Mô hình **Client-Server tập trung qua giao thức TCP Socket** (cổng mặc định `12345`, `ServerBackend.java` line 13, `ClientFX.java` line 123).
  - Server nắm giữ toàn bộ trạng thái bàn chơi, chia bài, kiểm tra luật (`GameLogic`) và điều phối lượt đi (`GameSession`), đồng bộ tới các client bằng cơ chế gửi thông điệp (broadcast).
  - Client hiển thị giao diện JavaFX, gửi thao tác lên server và dùng luồng nền `ServerListener` đọc phản hồi rồi cập nhật UI qua `Platform.runLater()`.

---

## 3. Cách chạy project

### Các bước cài đặt và chạy theo source code / script hiện có

1. **Chuẩn bị MySQL**:
   - Đảm bảo MySQL server đang chạy tại `localhost:3306` (`UserManager.java`, lines 8-10).
   - Khi khởi động, `UserManager.initDatabase()` (`UserManager.java`, lines 25-135) tự động chạy `CREATE DATABASE IF NOT EXISTS tienlencards` và tạo 5 bảng: `users`, `game_sessions`, `game_records`, `player_sessions`, `player_statistics`.
2. **Biên dịch**:
   - Qua Maven (theo `.github/copilot-instructions.md`, line 108): `mvn clean package` (lưu ý: máy hiện tại báo chưa có `mvn` trong PATH).
   - Hoặc qua `build.sh` (dành cho Linux/macOS với `javac`, cần chỉnh đường dẫn JavaFX SDK; `build.sh`, lines 5-15).
3. **Chạy Server**:
   - Theo `run_server.bat` (lines 2-7): chạy `tienlen.server.ServerUI` với `--module-path "C:\javafx-sdk-25\lib"`, `--add-modules javafx.controls,javafx.graphics`, classpath gồm `target/classes` và `mysql-connector-j-8.2.0.jar`.
   - Hoặc theo `.github/copilot-instructions.md` (line 114): `mvn exec:java -Dexec.mainClass=tienlen.server.ServerMain`.
4. **Chạy Client**:
   - Theo `run_client.bat` (lines 2-6): chạy `tienlen.client.ClientFX` với `--module-path "C:\javafx-sdk-25\lib"` và `--add-modules javafx.controls,javafx.graphics,javafx.media`.
   - Hoặc theo `.github/copilot-instructions.md` (line 111): `mvn javafx:run -DmainClass=tienlen.client.ClientFX`.

### Biến môi trường hoặc file cấu hình cần có

- Không sử dụng biến môi trường hay file `.env` / `.properties` riêng. Thông tin kết nối MySQL (`DB_URL`, `DB_USER`, `DB_PASSWORD`) đang viết cứng trong `UserManager.java` (lines 8-10).
- Các file `.bat` phụ thuộc đường dẫn cục bộ trên máy tác giả (`C:\javafx-sdk-25\lib` và `C:\Users\ADMIN\.m2\repository\...`).

### Những thứ còn thiếu hoặc sai lệch phát hiện được khi chuẩn bị chạy

- **Thiếu bảng `game_history` trong `initDatabase()`**: Các hàm `getPlayerGameHistory` (`UserManager.java`, line 384) và `getPlayerStatistics` (`UserManager.java`, line 418) truy vấn bảng `game_history`, nhưng `initDatabase()` chỉ tạo `game_records` chứ không tạo `game_history`.
- **Sai `<mainClass>` trong `pom.xml`**: Line 49 khai báo `tienlen.clientfx.GameClientFX`, trong khi lớp thực tế là `tienlen.client.ClientFX`.
- **Thiếu file `.sql` độc lập**: Không có file `.sql` khởi tạo database riêng trong repo (chỉ có mô tả trong `DATABASE_SCHEMA.md` và SQL tạo bảng trong `UserManager.initDatabase()`).
- **Công cụ Maven (`mvn`)**: Chưa có sẵn trong `PATH` của môi trường hiện tại.

---

## 4. Danh sách chức năng sơ bộ

### Chức năng & Màn hình

| Tên chức năng / Màn hình | File chứa |
|---|---|
| Kết nối TCP tới server, Đăng nhập, Đăng ký tài khoản | `src/main/java/tienlen/client/ClientFX.java` (lines 39-255) |
| Chọn bàn chơi, tạo bàn mới, xem chi tiết bàn, xem thống kê cá nhân & số dư | `src/main/java/tienlen/client/TableSelectionUI.java` (lines 64-100, 302-751) |
| Bàn chơi Tiến Lên (hiển thị bài trên tay, bài đã đánh, danh sách người chơi, lượt đánh, đánh bài, bỏ lượt, ván mới, rời bàn) | `src/main/java/tienlen/client/TienLenClientUI.java` (lines 32-99, 196, 250, 403, 569) |
| Chat chữ và chat giọng nói (ghi âm WAV -> Base64, phát âm thanh) | `src/main/java/tienlen/client/TienLenClientUI.java` (lines 593-624), `src/main/java/tienlen/client/ServerListener.java` (lines 274-304) |
| Luồng lắng nghe và điều phối thông điệp từ server về UI | `src/main/java/tienlen/client/ServerListener.java` (lines 71-345) |
| Giao diện quản trị Server (xem người chơi online/offline, xem bàn chơi, thống kê, log hệ thống) | `src/main/java/tienlen/server/ServerUI.java` (lines 24-130) |
| Quản lý kết nối, phòng chơi, kick người chơi, đóng phòng từ server | `src/main/java/tienlen/server/ServerBackend.java` (lines 20-217) |
| Điều phối ván chơi (chia bài, lượt đi, kiểm tra nước đi, tính tiền cược, thắng/thua) | `src/main/java/tienlen/server/GameSession.java` (lines 56-420) |
| Luật chơi Tiến Lên Miền Nam (nhận diện bộ bài và so sánh chặn bài) | `src/main/java/tienlen/logic/GameLogic.java` (lines 10-106) |
| Quản lý người dùng, số dư, lưu kết quả & thống kê vào MySQL | `src/main/java/tienlen/server/UserManager.java` (lines 7-1147) |

### Các loại thông điệp Client-Server tìm thấy trong code

- Định nghĩa trong `src/main/java/tienlen/utils/Protocol.java` (lines 7-47):
  - Xác thực: `REGISTER`, `REGISTER_SUCCESS`, `REGISTER_FAILED`, `LOGIN`, `LOGIN_SUCCESS`, `LOGIN_FAILED`
  - Quản lý bàn/phiên: `GET_SESSIONS`, `SESSION_LIST`, `SELECT_SESSION`, `SESSION_INFO`, `SESSION_CLOSED`, `GET_SESSION_DETAILS`, `SESSION_DETAILS`
  - Chơi game & chat: `JOIN`, `PLAY`, `PASS`, `HAND`, `PLAYER_LIST`, `GAME_START`, `NEWGAME`, `CHAT`, `CHAT_VOICE`
  - Tiền cược & thống kê: `BET`, `BET_RESULT`, `BALANCE`, `GET_BALANCE`, `PLAYER_INFO`, `GAME_RESULT`, `INSUFFICIENT_BALANCE`, `GET_PLAYER_STATS`, `PLAYER_STATS`
- Thông điệp dùng trực tiếp dạng chuỗi (chưa khai báo hằng số trong `Protocol.java`), tìm thấy trong `ClientHandler.java`, `GameSession.java`, `ServerBackend.java`, `ServerListener.java`:
  - `CREATE_SESSION`, `LEAVE_SESSION`, `SESSION_SELECTED`, `TURN`, `WIN`, `END`, `NOTIFICATION`, `KICKED`, `ERROR`.

---

## 5. Những điểm chưa rõ

1. **Đề tài, yêu cầu chính và thời hạn của ĐACS4**: Người dùng chưa điền trong phần bối cảnh.
2. **Vai trò của các file phụ ở thư mục gốc**: `tienlencards.db` (file dữ liệu nhị phân 16 KB), `fix_issues.py`, `fix_changes.txt` chưa rõ nguồn gốc và mục đích sử dụng.
3. **Trạng thái hoàn thiện thực tế của từng chức năng phụ**: Ví dụ `GET_PLAYER_STATS` được gửi từ `TableSelectionUI.java` (line 302), nhưng chưa thấy `case Protocol.GET_PLAYER_STATS` trong `ClientHandler.java` (lines 95-189) — cần xác nhận lại ở lượt đánh giá chi tiết.
4. **Cơ sở dữ liệu thực tế trên máy nhóm**: Hiện tại kết nối MySQL đang dùng tài khoản `root` không mật khẩu; chưa rõ nhóm đang dùng XAMPP/Laragon hay MySQL Server riêng, và đã có sẵn dữ liệu mẫu hay chưa.

---

## 6. Đối chiếu nhanh báo cáo (`BaoCaoDACS3.md`) với code

### a. Định dạng thông điệp client-server thực tế
- **Kết luận**: **Không khớp**.
- **Báo cáo**: Mục 2.2.3 (dòng 491) và Kết luận (dòng 753) ghi thông điệp dùng định dạng **JSON**; mục 2.3 (dòng 553, 557, 569) lại minh họa lẫn lộn giữa `|` và ngoặc nhọn `{"PLAY|[3♠4♠5♠]"}`.
- **Code thực tế**: Toàn bộ thông điệp là **chuỗi văn bản phân tách bằng dấu `|`** theo dạng `ACTION|DATA` (không có thư viện JSON nào trong `pom.xml`).
  - Bằng chứng: `src/main/java/tienlen/utils/Protocol.java`, `encode`/`decode` (lines 48-57):
    ```java
    public static String encode(Message msg) {
        return msg.getAction() + "|" + (msg.getData() == null ? "" : msg.getData());
    }
    public static Message decode(String raw) {
        String[] parts = raw.split("\\|", 2);
    ```
  - Các loại thông điệp tìm thấy đã liệt kê đầy đủ ở **Mục 4**.

### b. Cách xử lý mật khẩu khi đăng ký / đăng nhập
- **Kết luận**: **Không đủ dữ liệu để đối chiếu đầy đủ với báo cáo** (báo cáo chỉ minh họa chuỗi hash ở ví dụ gói `LOGIN` dòng 553, không mô tả thuật toán, nơi băm hay salt).
- **Code thực tế**:
  - **Có băm**, sử dụng thuật toán **SHA-256**, **không có salt**.
    - Bằng chứng: `src/main/java/tienlen/utils/PasswordUtil.java`, `hashSHA256` (lines 8-18):
      ```java
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(password.getBytes());
      ```
  - **Băm ở cả Client và Server** (băm 2 lần):
    - Client băm mật khẩu trước khi gửi qua TCP: `src/main/java/tienlen/client/ClientFX.java`, `loginGame()` (line 159) và `showRegisterScreen()` (line 246): `String hashedPassword = PasswordUtil.hashSHA256(password);`.
    - Server nhận chuỗi đã băm đó và tiếp tục gọi `PasswordUtil.hashSHA256(password)` lần nữa trước khi lưu/so sánh với MySQL: `src/main/java/tienlen/server/UserManager.java`, `registerUser()` (line 144) và `loginUser()` (line 173).

### c. Tính năng chat trong phòng
- **Kết luận**: **Không khớp** (mâu thuẫn giữa các phần trong báo cáo và code).
- **Báo cáo**: Mục 2.1 và 2.2.2 (dòng 425, 469) có liệt kê "Chat trong phòng", nhưng mục 3.3 Kết luận chương 3 (dòng 717) và Hướng phát triển (dòng 763) lại ghi chat là tính năng chưa có, cần bổ sung ở hướng phát triển.
- **Code thực tế**: **Đã có** cả chat văn bản (`CHAT`) và chat giọng nói (`CHAT_VOICE`).
  - Bằng chứng: `TienLenClientUI.java`, `sendChat()` (lines 593-600) và `startRecording()` (lines 602-624); `ClientHandler.java` (lines 162-174); `GameSession.java`, `chat()` và `chatVoice()` (lines 333-347); `ServerListener.java` (lines 274-304).

### d. Mã nguồn Android trong project
- **Kết luận**: **Không khớp**.
- **Báo cáo**: Phần Mở đầu (dòng 211) ghi cho phép người chơi kết nối qua *"máy tính hoặc thiết bị Android"*.
- **Code thực tế**: **Không có** bất kỳ mã nguồn Android nào (`.kt`, `AndroidManifest.xml`, `gradle`, hay thư mục Android) trong toàn bộ workspace.

### e. Cấu trúc dữ liệu lưu trong bộ nhớ ngoài MySQL
- **Kết luận**: **Khớp**.
- **Báo cáo**: Mục 2.3 (dòng 533) và 2.4 (dòng 577) nêu server nắm giữ trạng thái phòng và người chơi trong bộ nhớ kết hợp cơ sở dữ liệu SQL.
- **Code thực tế**: Server lưu danh sách phòng, người chơi online, socket handler và trạng thái ván bài trong các collection trên RAM:
  - `src/main/java/tienlen/server/ServerBackend.java` (lines 14-16):
    ```java
    private final List<GameSession> gameSessions = new CopyOnWriteArrayList<>();
    private final Map<String, PlayerInfo> onlinePlayers = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, ClientHandler> clientHandlers = Collections.synchronizedMap(new HashMap<>());
    ```
  - `src/main/java/tienlen/server/GameSession.java` (lines 16-21): `players` (`ArrayList`), `passedPlayers` (`HashSet`), `connections` (`HashMap<Player, ClientHandler>`).

### f. Liệt kê file trong `server/` và `utils/` kèm vai trò
- **Kết luận**: **Không khớp** (báo cáo không mô tả `utils/`, còn mục `3.1.5 Thư mục server` ở dòng 653-669 bị dán nhầm nội dung của thư mục `model/`).
- **Danh sách thực tế trong code**:

| Thư mục | File | Vai trò | Bằng chứng |
|---|---|---|---|
| `server/` | `ServerMain.java` | Điểm khởi chạy cũ (chuyển tiếp sang `ServerUI.main`) và quản lý session kiểu cũ | `ServerMain.java` (lines 12-27) |
| `server/` | `ServerUI.java` | Giao diện JavaFX quản trị Server (bảng người chơi, bàn chơi, thống kê, log) | `ServerUI.java` (lines 24-103) |
| `server/` | `ServerBackend.java` | Lắng nghe cổng `12345`, nạp bàn từ DB, quản lý người chơi online và danh sách `GameSession` | `ServerBackend.java` (lines 12-57) |
| `server/` | `ClientHandler.java` | Luồng phục vụ riêng 1 client, đọc/giải mã thông điệp và điều hướng xử lý | `ClientHandler.java` (lines 15-191) |
| `server/` | `GameSession.java` | Quản lý 1 bàn chơi: nhận người chơi, chia bài, lượt đánh, luật thắng thua, tính tiền, chat | `GameSession.java` (lines 13-420) |
| `server/` | `UserManager.java` | Singleton kết nối MySQL, khởi tạo bảng, đăng ký/đăng nhập, số dư, lưu lịch sử & thống kê | `UserManager.java` (lines 7-135) |
| `server/` | `PlayerInfo.java` | Đối tượng lưu thông tin hiển thị của người chơi trên UI server (tên, IP, trạng thái, bàn) | `PlayerInfo.java` (lines 9-22) |
| `server/` | `SessionInfo.java` | Đối tượng lưu thông tin tóm tắt của bàn chơi trên UI server (ID, số người, trạng thái, cược) | `SessionInfo.java` (lines 9-24) |
| `utils/` | `Protocol.java` | Khai báo hằng số tên thông điệp và hàm `encode`/`decode` theo dấu `\|` | `Protocol.java` (lines 5-57) |
| `utils/` | `PasswordUtil.java` | Cung cấp hàm `hashSHA256(String)` để băm mật khẩu | `PasswordUtil.java` (lines 6-22) |

### g. Cách Server tạo luồng cho mỗi client
- **Kết luận**: **Khớp**.
- **Báo cáo**: Mục 2.3 (dòng 551) nêu server tạo một luồng (`Thread`) riêng cho mỗi kết nối; mục Hướng phát triển (dòng 761) xác nhận hiện dùng blocking I/O và đề xuất nâng cấp lên NIO sau.
- **Code thực tế**: Server dùng mô hình **1 luồng riêng cho mỗi client (Thread-per-client)** bằng `new Thread(handler).start()`, chưa dùng Thread Pool hay Java NIO.
  - Bằng chứng: `src/main/java/tienlen/server/ServerBackend.java` (lines 49-50):
    ```java
    ClientHandler handler = new ClientHandler(socket, this);
    new Thread(handler).start();
    ```
    (Tương tự ở `src/main/java/tienlen/server/ServerMain.java`, lines 38-39).

---

## Thuật ngữ cần biết

- **Entry point**: Điểm bắt đầu chạy của chương trình (thường là hàm `main` hoặc lớp `Application` của JavaFX).
- **TCP Socket (`ServerSocket` / `Socket`)**: Cơ chế giao tiếp mạng tin cậy, giữ kết nối liên tục và đảm bảo dữ liệu đến đúng thứ tự giữa Client và Server.
- **Thread-per-client**: Mô hình server tạo một luồng xử lý (`Thread`) độc lập cho mỗi người chơi kết nối vào. Dễ viết nhưng sẽ tốn tài nguyên nếu có quá nhiều người chơi cùng lúc.
- **Thread Pool**: Nhóm các luồng được tạo sẵn và tái sử dụng để xử lý nhiều kết nối mà không phải tạo mới liên tục.
- **Java NIO (Non-blocking I/O)**: Cơ chế đọc/ghi mạng không chặn luồng, giúp một số lượng nhỏ luồng có thể phục vụ hàng nghìn kết nối đồng thời.
- **JDBC (`DriverManager`)**: Thư viện chuẩn của Java để kết nối và gửi câu lệnh SQL tới cơ sở dữ liệu (như MySQL).
- **Connection Pool**: Bộ quản lý các kết nối database được mở sẵn để tái sử dụng, giúp truy vấn nhanh và ổn định hơn so với việc mỗi lần hỏi DB lại mở kết nối mới.
- **Hash (Băm) & SHA-256**: Thuật toán biến đổi mật khẩu thành một chuỗi ký tự cố định một chiều (không dịch ngược lại được).
- **Salt**: Chuỗi dữ liệu ngẫu nhiên được trộn thêm vào mật khẩu trước khi băm để hai người dùng có mật khẩu giống nhau vẫn ra chuỗi băm khác nhau, chống dò ngược bằng bảng tra cứu.
- **Singleton**: Mẫu thiết kế đảm bảo một lớp (như `UserManager`) chỉ có duy nhất một thực thể (instance) trong suốt vòng đời chương trình.
- **Broadcast**: Việc server gửi cùng một thông điệp cập nhật tới tất cả các client đang ở trong cùng một phòng chơi.
- **Platform.runLater()**: Lệnh của JavaFX dùng để đẩy việc cập nhật giao diện từ luồng nền (như luồng đọc mạng) về luồng giao diện chính, tránh lỗi xung đột giao diện.

---

## Danh sách file chưa đọc

Các file dưới đây chưa được đọc (hoặc chỉ đọc phần đầu) trong lượt khảo sát nhanh này:
- File nhị phân: `tienlencards.db`
- File phụ ở gốc: `fix_issues.py`, `fix_changes.txt`, `.classpath`, `.project`, `.settings/*`
- Tài nguyên giao diện: `src/main/resources/style.css`
- Phần nội dung chi tiết phía sau của các file dài: `ServerUI.java` (sau dòng 150), `TableSelectionUI.java` (sau dòng 100), `TienLenClientUI.java` (sau dòng 100), `UserManager.java` (sau dòng 800), `GameLogic.java` (sau dòng 60), và các phần thân lớp của `Card.java`, `Deck.java`, `Message.java`, `Move.java`, `Player.java` (sau dòng 25).
