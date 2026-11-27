# Báo cáo Phân tích sâu (Lượt B) — TienLenMienNamTCP

> **Lưu ý về độ chắc chắn**: Do chưa có ghi chú chạy thử thực tế từ nhóm, toàn bộ nhận định dưới đây đều có mức độ chắc chắn là **"suy luận từ code"**.

---

## 1. Phân tích Chức năng

| Chức năng | File / Hàm liên quan | Trạng thái | Mức độ chắc chắn | Bằng chứng |
|---|---|---|---|---|
| Kết nối TCP & Đăng nhập | `ClientFX.java` (`connectToServer`, `loginGame`), `ClientHandler.java` (`handleLogin`), `UserManager.java` (`loginUser`) | **Có dấu hiệu lỗi** (băm 2 lần, chưa xử lý quay lại Login sạch sẽ) | Suy luận từ code | `ClientFX.java` (lines 121-174); `UserManager.java` (lines 167-197) |
| Đăng ký tài khoản | `ClientFX.java` (`showRegisterScreen`, `enableRegisterControls`), `ClientHandler.java` (`handleRegister`), `UserManager.java` (`registerUser`) | **Có dấu hiệu lỗi** (nút Đăng ký bị khóa vĩnh viễn nếu thất bại vì `enableRegisterControls()` rỗng) | Suy luận từ code | `ClientFX.java` (lines 244, 336-339) |
| Chọn bàn & Tạo bàn mới | `TableSelectionUI.java` (`requestTablesList`, `requestNewTable`, `selectTable`), `ClientHandler.java` (`handleGetSessions`, `CREATE_SESSION`, `handleSelectSession`) | **Hoàn chỉnh (cơ bản)** (nhưng thiếu kiểm tra số dư tối thiểu khi vào bàn) | Suy luận từ code | `TableSelectionUI.java` (lines 370-420, 749-757); `ClientHandler.java` (lines 118-137, 248-300) |
| Xem thống kê cá nhân / người chơi | `TableSelectionUI.java` (`showMyStatistics`, `updatePlayerStats`), `UserManager.java` (`getPlayerStatistics`) | **Chưa hoàn chỉnh / Lỗi** (`ClientHandler` không xử lý `GET_PLAYER_STATS`, `ServerListener` không bắt `PLAYER_STATS`, `UserManager` query sai bảng `game_history`) | Suy luận từ code | `TableSelectionUI.java` (lines 300-326, 702-733); `ClientHandler.java` (lines 95-189); `UserManager.java` (line 418) |
| Chia bài & Chơi bài (Đánh / Bỏ lượt / Ván mới) | `GameSession.java` (`startGame`, `processMove`, `handlePass`, `endGame`), `TienLenClientUI.java` (`playSelectedCards`, `passTurn`) | **Có dấu hiệu lỗi** (Client xóa bài trước khi server duyệt; NPE nếu `currentTurn == null`; server chưa check bài có trên tay) | Suy luận từ code | `TienLenClientUI.java` (lines 362, 405-411); `GameSession.java` (lines 172-181, 228-272) |
| Tính tiền cược & Lưu kết quả | `GameSession.java` (`endGame`), `UserManager.java` (`addBalance`, `subtractBalance`, `saveGameResult`, `endGameSession`) | **Có dấu hiệu lỗi** (không kiểm tra số dư âm khi trừ tiền, không bọc transaction DB) | Suy luận từ code | `GameSession.java` (lines 359-398); `UserManager.java` (lines 294-335, 356-379) |
| Chat chữ & Chat giọng nói trong phòng | `TienLenClientUI.java` (`sendChat`, `startRecording`), `ClientHandler.java` (`CHAT`, `CHAT_VOICE`), `GameSession.java` (`chat`, `chatVoice`), `ServerListener.java` | **Hoàn chỉnh (có nhược điểm nhỏ)** (tạo file tạm `.wav` không xóa sau khi phát) | Suy luận từ code | `TienLenClientUI.java` (lines 593-624); `ServerListener.java` (lines 274-304) |
| Quản trị Server (Xem player, session, kick, đóng bàn, thống kê) | `ServerUI.java`, `ServerBackend.java` (`kickPlayer`, `closeSession`, `getAllPlayersWithOffline`) | **Có dấu hiệu lỗi hiệu năng** (`updateStatisticsPanel` chạy SQL ngay trên JavaFX UI thread mỗi 2 giây) | Suy luận từ code | `ServerUI.java` (lines 562-577, 680-710); `ServerBackend.java` (lines 145-197) |

---

## 2. Phân tích Database

### 2.1. Các bảng hiện có trong code (`UserManager.initDatabase()`, lines 40-127)

| Tên bảng | Khóa chính | Khóa ngoại & Quan hệ | Nơi truy vấn (`SELECT`) | Nơi cập nhật (`INSERT`/`UPDATE`/`DELETE`) |
|---|---|---|---|---|
| `users` | `id` (AI), `username` (`UNIQUE`) | Bảng gốc, được `player_sessions.user_id` và `player_statistics.player_id` tham chiếu | `loginUser`, `userExists`, `getUserCount`, `getBalance`, `getAllUsers`, `getPlayerInfo`, `getAllPlayerStats`, `getTopPlayers*` (`UserManager.java`) | `registerUser` (`INSERT`), `updateLastLogin`, `updateBalance`, `addBalance`, `subtractBalance` (`UPDATE`) |
| `game_sessions` | `id` (AI), `session_id` (`UNIQUE`) | Không có FK vật lý; liên kết logic qua `session_id` với `game_records` | `loadActiveSessions`, `getActiveSessionCount`, `getCompletedSessionCountToday`, `getTotalFlow`, `getWaitingSessionCount` | `createGameSession` (`INSERT`), `updateSessionStatus`, `endGameSession` (`UPDATE`) |
| `game_records` | `id` (AI) | Không có FK vật lý; liên kết logic qua `session_id` và `player_name` | `getTotalBetToday`, `getTotalWinToday`, `getAverageBetToday`, `getTopPlayerToday`, `getPlayerInfo`, `getAllPlayerStats`, `getTopPlayers*` | `saveGameResult` (`INSERT`, line 357), `saveGameResultNew` (`INSERT`, line 703) |
| `player_sessions` | `id` (AI) | `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE` | `getOfflinePlayerCount`, `getActivePlayers30Min` | `createPlayerSession` (`INSERT`), `updatePlayerActivity`, `updatePlayerStatus` (`UPDATE`), `deletePlayerSession` (`DELETE`) |
| `player_statistics` | `id` (AI), `player_id` (`UNIQUE`) | `FOREIGN KEY (player_id) REFERENCES users(id) ON DELETE CASCADE` | Không thấy `SELECT` trong `UserManager.java` | `updatePlayerStats` (`INSERT ... ON DUPLICATE KEY UPDATE`, line 661) |

### 2.2. Lỗi & Lỗ hổng Database phát hiện được
1. **Truy vấn bảng không tồn tại (`game_history`)**:
   - `UserManager.java`, `getPlayerGameHistory()` (line 384) và `getPlayerStatistics()` (line 418) truy vấn `FROM game_history`, trong khi `initDatabase()` chỉ tạo `game_records`. Nếu gọi 2 hàm này sẽ ném `SQLSyntaxErrorException`.
2. **Lệch tên cột `user_id` vs `player_id` trong `player_sessions`**:
   - `initDatabase()` (`UserManager.java`, line 89) định nghĩa cột `user_id INT NOT NULL`, nhưng `createPlayerSession(String, String)` (line 1085) và `deletePlayerSession()` (line 1135) lại ghi `player_id`. Khi client đăng nhập/ngắt kết nối, các câu SQL này sẽ lỗi `Unknown column 'player_id'`.
3. **SQL Injection**:
   - **An toàn trước SQL Injection**: Toàn bộ câu truy vấn có tham số đầu vào từ người dùng trong `UserManager.java` đều dùng `PreparedStatement` với placeholder `?` (ví dụ lines 147-151, 176-179, 203-206). Các câu dùng `Statement` đều là truy vấn tĩnh không ghép chuỗi đầu vào.
4. **Thiếu Transaction khi tính tiền kết thúc ván**:
   - `GameSession.endGame()` (lines 380-394) gọi `subtractBalance`, `addBalance`, `saveGameResult`, `endGameSession` thành nhiều kết nối JDBC rời rạc. Nếu lỗi giữa chừng (ví dụ mất kết nối DB sau khi trừ tiền người thua), tiền của người thắng sẽ không được cộng, gây sai lệch dữ liệu.
5. **Mở kết nối mới liên tục & thông tin DB viết cứng**:
   - `UserManager.java` (lines 8-10) hardcode `DB_URL`, `DB_USER`, `DB_PASSWORD` (mật khẩu rỗng) và gọi `DriverManager.getConnection(...)` ở mọi hàm.

---

## 3. Phân tích Luật chơi (`GameLogic` & `GameSession`)

### 3.1. Luật đã cài đặt
- **Nhận diện bộ bài** (`GameLogic.identifyType()`, `GameLogic.java`, lines 12-79):
  - `SINGLE` (1 lá), `PAIR` (đôi), `TRIPLE` (sám cô), `FOUR_KIND` (tứ quý).
  - `THREE_PAIRS` (3 đôi thông, line 40-53), `FOUR_PAIRS` (4 đôi thông, line 56-69).
  - `STRAIGHT` (sảnh từ 3 lá trở lên, cấm lá 2 ở `cards.get(i).getRank() == 15`, lines 71-77).
- **So sánh & Chặt bài** (`GameLogic.canBeat()`, `GameLogic.java`, lines 81-105):
  - Cùng loại và cùng độ dài: so sánh lá cao nhất (`next.getHighest().compareTo(prev.getHighest()) > 0`).
  - Chặt **2 lẻ**: `FOUR_KIND`, `THREE_PAIRS`, hoặc `FOUR_PAIRS` (lines 91-95).
  - Chặt **đôi 2**: `FOUR_KIND` hoặc `FOUR_PAIRS` (lines 97-100).
  - `FOUR_PAIRS` chặt `FOUR_KIND` (lines 101-102).
- **Lượt đánh đầu ván**: Tìm người giữ `3♠` nếu `winnerIndex == -1`, ngược lại lấy `winnerIndex` (`GameSession.startGame()`, lines 172-181).
- **Vòng đánh & Bỏ lượt**: Khi `players.size() - 1` người bỏ lượt (`PASS`), reset `lastMove = null` và bắt đầu vòng mới (`GameSession.handlePass()`, lines 201-211).

### 3.2. Luật Tiến Lên Miền Nam còn thiếu
- **Chặt chồng thiếu**: `FOUR_KIND` chưa chặt được `THREE_PAIRS`; `FOUR_PAIRS` chưa chặt được `THREE_PAIRS`; `FOUR_PAIRS` nhỏ hơn chưa so sánh với `FOUR_PAIRS` lớn hơn? (Thực tế cùng loại `FOUR_PAIRS` cùng độ dài 8 rơi vào nhánh line 86-88 nên so lá cao nhất được, nhưng **khác loại** như Tứ quý chặt 3 đôi thông, hoặc 4 đôi thông chặt 3 đôi thông bị trả về `false`).
- **Tới trắng (Instant Win)**: Chưa cài đặt (tứ quý 2, 6 đôi, sảnh rồng 3-A, 5 đôi thông...).
- **Luật đánh 3 bích đầu ván đầu**: Chỉ gán lượt cho người có `3♠`, nhưng không bắt buộc nước đi đầu tiên phải chứa `3♠`.
- **Thối 2 / Phạt cóng / Đếm lá**: Kết thúc ván chỉ tính thắng/thua cố định `betAmount` (`GameSession.endGame()`, line 373), không phạt thêm khi thối heo/hàng hay còn nguyên 13 lá.

### 3.3. Rủi ro logic phát hiện được
1. **Không kiểm tra bài có thực sự nằm trên tay người chơi**:
   - `GameSession.processMove()` (lines 244-260) không kiểm tra `player.getHand().containsAll(move.getCards())`. Client gửi chuỗi bài bất kỳ hợp lệ vẫn đánh được (lỗ hổng gian lận).
2. **`winnerIndex` có thể gây `IndexOutOfBoundsException`**:
   - `GameSession.startGame()` (line 181): Nếu ván trước người ở vị trí index 3 thắng (`winnerIndex = 3`), sau đó 1 người thoát chỉ còn 3 người (`players.size() == 3`), ván sau `currentTurnIndex = winnerIndex` sẽ văng lỗi khi gọi `players.get(currentTurnIndex)`.
3. **Sảnh không kiểm tra lá đầu tiên có phải là `2` hay không**:
   - `GameLogic.identifyType()` (lines 71-75): Vòng lặp `for (int i = 1; i < cards.size(); i++)` chỉ check `cards.get(i).getRank() == 15`. (Do đã `Collections.sort(cards)` ở line 16 và `2` có `rank = 15` nên `2` luôn ở cuối; tuy nhiên nếu dữ liệu lỗi thì lá đầu vẫn không được check).
4. **Client xóa bài trước khi server xác nhận**:
   - `TienLenClientUI.playSelectedCards()` (lines 405-411) xóa nút bài ngay sau `out.println(...)`, dẫn tới lệch trạng thái tay bài nếu server báo lỗi.

---

## 4. Giao thức mạng (`Protocol` & Socket)

### 4.1. Bảng thông điệp Client-Server

Định dạng chung: `ACTION|DATA` (`Protocol.java`, lines 48-57).

| Tên thông điệp | Chiều gửi | Nội dung (`DATA`) | Nơi xử lý |
|---|---|---|---|
| `REGISTER` / `REGISTER_SUCCESS` / `REGISTER_FAILED` | C ↔ S | `user=...&pass=...` / thông báo | `ClientFX` → `ClientHandler.handleRegister` → `ServerListener` |
| `LOGIN` / `LOGIN_SUCCESS` / `LOGIN_FAILED` | C ↔ S | `user=...&pass=...` / `username` hoặc lỗi | `ClientFX` → `ClientHandler.handleLogin` → `ServerListener` |
| `GET_SESSIONS` / `SESSION_LIST` | C ↔ S | rỗng / `id,count,status,name,bet;...` | `TableSelectionUI` → `ClientHandler.handleGetSessions` → `ServerListener` |
| `CREATE_SESSION` | C → S | `name=...&bet=...` | `TableSelectionUI` → `ClientHandler` (line 118) |
| `SELECT_SESSION` / `SESSION_SELECTED` | C ↔ S | `sessionId` | `TableSelectionUI` → `ClientHandler.handleSelectSession` → `ServerListener` |
| `GET_SESSION_DETAILS` / `SESSION_DETAILS` | C ↔ S | `sessionId` / `name,balance;...` | `TableSelectionUI` → `ClientHandler.handleGetSessionDetails` → `ServerListener` |
| `GET_PLAYER_STATS` / `PLAYER_STATS` | C ↔ S | `username` / thống kê | `TableSelectionUI` (line 302) — **Server & Listener chưa xử lý** |
| `BALANCE` | S → C | `long` (số dư VND) | `ClientHandler` (lines 218, 359) → `ServerListener` (line 120) |
| `JOIN` | C → S | `playerName` | `ClientHandler` (line 138) |
| `PLAYER_LIST` | S → C | `name1:bal1,name2:bal2,...` | `GameSession.broadcastPlayerList` → `ServerListener` (line 189) |
| `NEWGAME` | C → S | `username` | `TienLenClientUI` (line 248) → `ClientHandler` (line 152) |
| `HAND` | S → C | `3♠,4♦,...` | `ClientHandler.sendHand` → `ServerListener` (line 180) |
| `TURN` | S → C | `currentPlayerName` | `GameSession.broadcastTurn` → `ServerListener` (line 198) |
| `PLAY` | C ↔ S | Client: `3♠,4♠`; Server: `name,3♠,4♠` | `TienLenClientUI` → `ClientHandler` → `GameSession` → `ServerListener` |
| `PASS` | C ↔ S | rỗng | `TienLenClientUI` → `ClientHandler` → `GameSession` → `ServerListener` |
| `WIN` / `END` | S → C | `winnerName\|name1:bal1,...` | `GameSession.endGame` → `ServerListener` (lines 220, 261) |
| `CHAT` | C ↔ S | `username: nội dung` | `TienLenClientUI` → `ClientHandler` → `GameSession.chat` → `ServerListener` |
| `CHAT_VOICE` | C ↔ S | `username\|base64Wav` | `TienLenClientUI` → `ClientHandler` → `GameSession.chatVoice` → `ServerListener` |
| `LEAVE_SESSION` | C → S | rỗng | `TienLenClientUI` (line 196) → `ClientHandler` (line 175) |
| `SESSION_CLOSED` | S → C | `sessionId\|reason` | `ServerBackend.closeSession` → `ServerListener` (line 324) |
| `KICKED` | S → C | lý do bị kick | `ServerBackend.kickPlayer` → `ServerListener` (line 309) |
| `NOTIFICATION` / `ERROR` | S → C | thông báo văn bản | `GameSession` / `ClientHandler` → `ServerListener` (`NOTIFICATION` chỉ in console; `ERROR` chưa bắt) |

### 4.2. Cách xử lý ngắt kết nối
- **Khi Client ngắt kết nối đột ngột**:
  - `ClientHandler.run()` bắt `IOException` và chạy khối `finally` (`ClientHandler.java`, lines 192-211): gọi `UserManager.deletePlayerSession(currentUsername)`, xóa player khỏi `gameSession`, `broadcastPlayerList()`, và nếu `gameSession.getPlayers().size() < 2` thì gọi `gameSession.endGame("END")`, sau đó đóng `socket`.
  - **Thiếu sót**: Trong `finally` của `ClientHandler` (lines 194-211) **không gọi** `backend.unregisterPlayer(currentUsername)` (chỉ gọi trong `closeConnection()` lúc bị kick, lines 57-61). Do đó người chơi thoát app ngang vẫn bị kẹt trạng thái online trong `ServerBackend.onlinePlayers`!
- **Khi Server ngắt kết nối**:
  - `ServerListener.run()` (`ServerListener.java`, lines 342-344) chỉ `e.printStackTrace()` rồi kết thúc luồng, không thông báo cho người dùng hay đưa về màn hình kết nối.

---

## 5. Đánh giá Chất lượng Code

| Hạng mục | Mức độ | Vấn đề cụ thể & Bằng chứng |
|---|---|---|
| **Xử lý lỗi & An toàn dữ liệu** | **Cao** | Truy vấn sai bảng `game_history` (`UserManager.java`, lines 384, 418) và sai cột `player_id` (`UserManager.java`, line 1085); không dọn `onlinePlayers` khi client tự thoát (`ClientHandler.java`, lines 194-211); không check bài trên tay khi `PLAY` (`GameSession.java`, line 259). |
| **Đa luồng & Đồng bộ (Concurrency)** | **Cao** | `ServerUI.updateStatisticsPanel()` (lines 683-710) chạy nhiều câu SQL ngay trong `Platform.runLater()` trên UI thread mỗi 2s; `TableSelectionUI` dùng `Thread.sleep(1000)` chờ mạng (lines 304, 709); `GameSession` dùng `HashMap connections` nhưng một số hàm đọc không `synchronized` (`broadcastPlayerList`, `chat`, `endGame`). |
| **Bảo mật** | **Cao** | Băm SHA-256 **2 lần không salt** (`ClientFX.java` lines 159, 246 + `UserManager.java` lines 144, 173); thông tin DB hardcode (`UserManager.java`, lines 8-10); truyền dữ liệu TCP dạng văn bản thuần (không mã hóa TLS). |
| **Tách lớp & Kiến trúc** | **Trung bình** | `UserManager.java` dài 1147 dòng ôm quá nhiều trách nhiệm (khởi tạo schema, auth, game records, thống kê); `ServerMain.java` trùng lặp với `ServerBackend.java`; `GameState.java` và `ClientMain.java` là code chết. |
| **Giao diện & CSS** | **Trung bình** | `style.css` lỗi cú pháp (lines 50-56); `TienLenClientUI` dùng inline style đè CSS gây chìm chữ khung chat (line 445), không reset `ScaleTransition` (line 451), truy cập cứng index `getChildren().get(1)` (line 424). |
| **Trùng lặp & Đặt tên** | **Thấp** | `Protocol.java` thiếu một số hằng số nên nhiều nơi dùng string literal (`"TURN"`, `"WIN"`, `"CREATE_SESSION"`, `"ERROR"`); `pom.xml` sai `mainClass` (`GameClientFX` thay vì `ClientFX`); tên biến kiểu `winningSAmount`, `sessionInfo_` thiếu nhất quán. |

---

## 6. Đối chiếu Báo cáo (`BaoCaoDACS3.md`) với Code

| Nội dung | Báo cáo nói gì | Code thực tế | Cần sửa ở đâu |
|---|---|---|---|
| **Định dạng giao thức** | Ghi dùng JSON (dòng 491, 753) và ví dụ lẫn `\|` với `{}` (dòng 553, 557) | Dùng chuỗi phân tách bởi `\|` (`ACTION\|DATA`) | Sửa lại mục 2.2.3, 2.3 và Kết luận trong báo cáo theo đúng `Protocol.java` |
| **Hỗ trợ Android** | Mở đầu ghi chơi trên máy tính hoặc thiết bị Android (dòng 211) | Chỉ có JavaFX desktop, không có code Android | Bỏ cụm "hoặc thiết bị Android" khỏi báo cáo |
| **Chat trong phòng** | Mục 2.1 có liệt kê (dòng 425), nhưng mục 3.3 & Hướng phát triển ghi chưa có (dòng 717, 763) | Đã cài đặt cả chat chữ (`CHAT`) và voice (`CHAT_VOICE`) | Cập nhật báo cáo ghi nhận đã hoàn thành chat chữ & voice |
| **Cấu trúc thư mục `server/` & `utils/`** | Mục 3.1.5 `server` bị copy nhầm danh sách class của `model` (dòng 653-669); thiếu mục `utils` | `server/` có 8 class (`ServerUI`, `ServerBackend`, `GameSession`, `ClientHandler`, `UserManager`...); `utils/` có `Protocol`, `PasswordUtil` | Viết lại mục 3.1.5 và bổ sung 3.1.6 trong báo cáo |
| **Tên file trong `client/`** | Mục 3.1.2 (dòng 607) liệt kê `AuthListener.java` | Không có `AuthListener.java`; xác thực đã gộp vào `ServerListener.java` (line 79) | Bỏ `AuthListener.java` khỏi báo cáo, cập nhật mô tả `ServerListener.java` |
| **Số lượng bảng Database** | `DATABASE_SCHEMA.md` (lines 234-504) mô tả 8 bảng (gồm `transactions`, `system_log`, `server_statistics`) | `UserManager.initDatabase()` tạo 5 bảng; còn `game_history` được query nhưng chưa được tạo | Đồng bộ SQL trong `UserManager.java` và cập nhật lại `DATABASE_SCHEMA.md` + sơ đồ CSDL trong báo cáo |

---

## Thuật ngữ cần biết

- **SQL Injection**: Lỗi bảo mật xảy ra khi ghép trực tiếp dữ liệu người dùng nhập vào câu lệnh SQL, giúp kẻ tấn công chèn lệnh SQL độc hại. Dùng `PreparedStatement` (dấu `?`) giúp phòng tránh lỗi này.
- **Database Transaction**: Cơ chế gộp nhiều câu lệnh SQL (ví dụ trừ tiền người thua + cộng tiền người thắng) thành một khối "hoặc thành công tất cả, hoặc hủy bỏ tất cả", tránh mất cân bằng tiền.
- **NullPointerException (NPE) / IndexOutOfBoundsException**: Hai lỗi runtime phổ biến trong Java khi gọi hàm trên đối tượng đang `null` hoặc truy cập phần tử ngoài độ dài của danh sách (`List`/mảng).
- **UI Thread Blocking**: Hiện tượng giao diện bị đơ do chạy tác vụ nặng (như truy vấn MySQL hoặc `Thread.sleep`) ngay trên luồng vẽ giao diện JavaFX (`Platform.runLater`).
- **Desync (Lệch trạng thái)**: Khi giao diện Client hiển thị trạng thái khác với dữ liệu thật đang lưu trên Server (ví dụ Client tự xóa lá bài trước khi Server đồng ý).

---

## 5 câu hỏi giảng viên có thể hỏi về phần này

1. *"Tại sao ở màn hình đăng nhập các em băm mật khẩu bằng SHA-256 ở cả Client lẫn Server, và việc không dùng Salt có rủi ro gì?"*
2. *"Nếu người chơi ở Client tự sửa code để gửi gói `PLAY` gồm những lá bài họ không có trên tay, hiện tại `GameSession` trên Server có chặn được không?"*
3. *"Khi một client đang chơi bị rớt mạng đột ngột hoặc tắt ngang ứng dụng, Server xử lý danh sách người chơi online (`ServerBackend`) và ván đang chơi (`GameSession`) như thế nào?"*
4. *"Tại sao không nên gọi các hàm truy vấn MySQL của `UserManager` bên trong `Platform.runLater()` trên giao diện `ServerUI`?"*
5. *"Trong `GameSession.endGame()`, nếu database bị lỗi ngay sau khi trừ tiền người thua nhưng chưa kịp cộng tiền người thắng thì chuyện gì xảy ra, và cách khắc phục bằng Transaction là gì?"*
