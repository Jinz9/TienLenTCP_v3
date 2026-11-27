# Báo cáo Đánh giá Code & Phân loại (Lượt B) — TienLenMienNamTCP

Mục tiêu của ĐACS4 là **trau chuốt giao diện và tối ưu các tính năng hiện có** trên nền tảng code ĐACS3. Dưới đây là đánh giá chi tiết các lỗi/điểm nghẽn hiện tại kèm bảng phân loại hành động cho từng file.

---

## 1. Các lỗi và điểm nghẽn phát hiện được trong code

### 1.1. Lỗi cơ sở dữ liệu & SQL (Gây lỗi runtime khi chạy các tính năng thống kê/session)
1. **Truy vấn bảng `game_history` không tồn tại**:
   - `UserManager.getPlayerGameHistory()` (`UserManager.java`, line 384) và `UserManager.getPlayerStatistics()` (line 418) truy vấn `FROM game_history`, trong khi `initDatabase()` (lines 40-127) chỉ tạo bảng `game_records`.
2. **Lệch tên cột `user_id` và `player_id` trong bảng `player_sessions`**:
   - `initDatabase()` (`UserManager.java`, line 89) tạo cột `user_id INT NOT NULL`, nhưng `createPlayerSession(String, String)` (line 1085) và `deletePlayerSession()` (line 1135) lại dùng `player_id`.
3. **Mở kết nối DB mới cho mỗi truy vấn & viết cứng thông tin kết nối**:
   - `UserManager.java` (lines 8-10, 147, 176...) dùng `DriverManager.getConnection(...)` trực tiếp trong từng hàm và hardcode `DB_URL`, `DB_USER`, `DB_PASSWORD`.

### 1.2. Lỗi luồng chơi & luật Tiến Lên (`GameLogic`, `GameSession`, `TienLenClientUI`)
1. **Client tự xóa bài trước khi Server xác nhận**:
   - `TienLenClientUI.playSelectedCards()` (`TienLenClientUI.java`, lines 402-411) gửi `PLAY` lên server rồi gọi `cardButtons.removeIf(...)` xóa bài khỏi giao diện ngay lập tức. Nếu server từ chối nước đi, bài trên tay người chơi vẫn bị mất.
2. **Nguy cơ `NullPointerException` khi bấm Đánh Bài lúc chưa có lượt**:
   - `TienLenClientUI.playSelectedCards()` (`TienLenClientUI.java`, line 362) gọi `if (!currentTurn.equals(username))` khi `currentTurn` khởi tạo là `null` (line 59).
3. **Nguy cơ `IndexOutOfBoundsException` khi bắt đầu ván mới**:
   - `GameSession.startGame()` (`GameSession.java`, line 181): gán `currentTurnIndex = winnerIndex` từ ván trước mà không kiểm tra lại xem `winnerIndex` có nhỏ hơn `players.size()` hiện tại hay không (nếu có người vừa rời phòng).
4. **Chưa kiểm tra bài trên tay ở Server**:
   - `GameSession.processMove()` (`GameSession.java`, lines 244-260) chỉ kiểm tra `move.isValid()` và `GameLogic.canBeat()`, chưa kiểm tra người chơi có thực sự giữ các lá bài đó trong `player.getHand()` hay không.
5. **Chưa kiểm tra số dư tối thiểu & tiền âm**:
   - `GameSession.endGame()` (`GameSession.java`, lines 380-387) gọi `subtractBalance` trực tiếp mà không kiểm tra người chơi có đủ `betAmount` trước khi vào ván/khi bị trừ tiền hay không.
6. **Thiếu một số luật chặt bài trong `GameLogic.canBeat()`**:
   - `GameLogic.java` (lines 90-103) chưa xử lý `FOUR_KIND` chặt `THREE_PAIRS` và `FOUR_PAIRS` chặt `THREE_PAIRS`.

### 1.3. Lỗi tính năng & đồng bộ Client-Server
1. **Tính năng xem thống kê người chơi (`GET_PLAYER_STATS`) chưa hoạt động**:
   - `TableSelectionUI.java` (lines 302, 704) gửi `Protocol.GET_PLAYER_STATS`, nhưng `ClientHandler.java` (lines 95-189) không có `case` xử lý, và `ServerListener.java` cũng chưa bắt `PLAYER_STATS`.
   - `TableSelectionUI.java` (lines 304, 709) dùng `Thread.sleep(1000)` để chờ phản hồi mạng thay vì cập nhật theo sự kiện nhận được từ `ServerListener`.
2. **Nút Đăng ký bị khóa vĩnh viễn nếu đăng ký thất bại**:
   - `ClientFX.java` (line 244) disable `submitRegBtn`, nhưng `enableRegisterControls()` (lines 336-339) lại để trống.
3. **Đơ giao diện `ServerUI` khi DB chậm**:
   - `ServerUI.updateStatisticsPanel()` (`ServerUI.java`, lines 683-710) thực hiện hàng loạt câu lệnh SQL (`UserManager`) **ngay bên trong `Platform.runLater()`** trên JavaFX Application Thread mỗi 2 giây.
4. **Rác file tạm khi dùng Voice Chat**:
   - `ServerListener.java` (lines 287-289) tạo file `Files.createTempFile("tienlen_voice_", ".wav")` mỗi lần nhận voice chat nhưng không dọn dẹp sau khi phát xong.

### 1.4. Lỗi giao diện (UI/UX & CSS)
1. **Lỗi cú pháp trong `style.css`**:
   - `src/main/resources/style.css` (lines 50-56) có dấu `}` thừa và các thuộc tính CSS nằm ngoài selector.
2. **Chữ trong khung chat bị chìm màu khi tới lượt**:
   - `TienLenClientUI.highlightTurn()` (`TienLenClientUI.java`, line 445) đổi nền `chatArea` thành vàng nhạt `#fff3cd`, trong khi `.chat-area` (`style.css`, line 207) đặt màu chữ `#e0e0e0` (xám trắng), gây khó đọc.
3. **Animation phóng to ô người chơi không reset kích thước**:
   - `TienLenClientUI.highlightTurn()` (`TienLenClientUI.java`, lines 432-451) chạy `ScaleTransition` vô hạn, khi hết lượt chỉ gọi `box.setStyle("")` mà không đưa `scaleX`/`scaleY` về `1.0`.
4. **Ô người chơi cũ không được dọn khi người khác rời bàn**:
   - `TienLenClientUI.updatePlayerList()` (`TienLenClientUI.java`, lines 480-502) chỉ ghi đè lên các ô tương ứng số lượng người chơi hiện tại, không reset các ô trống còn lại.
5. **Truy cập cứng chỉ số node UI**:
   - `TienLenClientUI.java` (lines 424, 487-500) ép kiểu `(Label) box.getChildren().get(1)` và `get(2)`, dễ lỗi nếu tinh chỉnh layout.

### 1.5. Bảo mật & Cấu hình Build
1. **Băm mật khẩu 2 lần không salt**:
   - `ClientFX.java` (lines 159, 246) băm SHA-256 ở client rồi gửi đi, sau đó `UserManager.java` (lines 144, 173) lại băm SHA-256 thêm một lần nữa (đều không có salt).
2. **`pom.xml` sai `mainClass` và thiếu `javafx-media`**:
   - `pom.xml` (line 49) ghi `tienlen.clientfx.GameClientFX` (không tồn tại) thay vì `tienlen.client.ClientFX`, và chưa khai báo dependency `javafx-media` (dù `ServerListener.java` dùng `MediaPlayer`).

---

## 2. Bảng phân loại chi tiết (KEEP / IMPROVE / REFACTOR / REBUILD / REMOVE)

| File / Module | Phân loại | Lý do & Hướng xử lý dự kiến cho ĐACS4 |
|---|---|---|
| `model/Card.java`, `Deck.java`, `Move.java`, `Player.java`, `Message.java` | **KEEP** | Cấu trúc dữ liệu gọn, rõ ràng, đáp ứng đúng nghiệp vụ cơ bản. Chỉ bổ sung kiểm tra biên nhỏ nếu cần. |
| `server/PlayerInfo.java`, `server/SessionInfo.java` | **KEEP** | DTO đơn giản phục vụ hiển thị bảng trên `ServerUI`, hoạt động đúng vai trò. |
| `utils/Protocol.java` | **IMPROVE** | Giữ định dạng `ACTION\|DATA` gọn nhẹ; bổ sung đầy đủ hằng số còn thiếu (`CREATE_SESSION`, `LEAVE_SESSION`, `TURN`, `WIN`, `END`, `KICKED`, `NOTIFICATION`, `ERROR`, `SESSION_SELECTED`). |
| `logic/GameLogic.java` | **IMPROVE** | Giữ thuật toán nhận diện bộ bài; bổ sung đầy đủ luật chặt (tứ quý chặt 3 đôi thông, 4 đôi thông chặt 3 đôi thông) và kiểm tra điều kiện biên. |
| `server/GameSession.java` | **IMPROVE** | Sửa lỗi `winnerIndex` khi đổi số người, kiểm tra bài trên tay khi `PLAY`, kiểm tra số dư trước/sau ván, xử lý an toàn khi `connections.get(p)` null. |
| `server/ClientHandler.java` | **IMPROVE** | Bổ sung xử lý `GET_PLAYER_STATS`, thay toàn bộ chuỗi action cứng bằng hằng số `Protocol.*`, xử lý ngắt kết nối gọn gàng. |
| `server/ServerBackend.java` | **IMPROVE** | Đồng bộ việc đóng/xóa bàn với DB, tách khỏi phụ thuộc cứng vào `ServerUI` khi cần log. |
| `client/ClientFX.java` | **IMPROVE** | Sửa nút đăng ký bị khóa, hỗ trợ phím Enter, quản lý đóng/mở lại socket khi quay về màn hình Login, trau chuốt giao diện Login/Register đồng bộ theme. |
| `client/TableSelectionUI.java` | **IMPROVE** | Bỏ `Thread.sleep(1000)` khi tải thống kê, kết nối dữ liệu thật từ `PLAYER_STATS`, trau chuốt thẻ bàn chơi, thêm tìm kiếm/lọc bàn và hiển thị số dư/trạng thái rõ ràng. |
| `client/TienLenClientUI.java` | **IMPROVE** | Sửa lỗi mất bài khi server từ chối, sửa `NullPointerException` lúc `currentTurn == null`, sửa màu chat khi tới lượt, reset scale animation & ô trống, trau chuốt lá bài/bố cục bàn chơi. |
| `resources/style.css` | **IMPROVE** | Sửa lỗi cú pháp lines 50-56, chuẩn hóa bảng màu và style cho cả Login, Chọn bàn và Bàn chơi. |
| `pom.xml` | **IMPROVE** | Sửa `mainClass` thành `tienlen.client.ClientFX`, bổ sung `javafx-media` để build/run chuẩn bằng Maven. |
| `server/UserManager.java` | **REFACTOR** | File quá lớn (1147 dòng), lệch tên bảng (`game_history` vs `game_records`) và lệch tên cột (`user_id` vs `player_id`), mở connection mới mỗi query. Cần sửa chuẩn schema, tách cấu hình DB ra file riêng và chia nhỏ trách nhiệm truy vấn. |
| `server/ServerUI.java` | **REFACTOR** | File dài (903 dòng) và đang gọi SQL trực tiếp trên UI thread (`Platform.runLater()`). Cần đưa toàn bộ truy vấn DB ra background thread, chỉ cập nhật UI trên FX thread. |
| `client/ServerListener.java` | **REFACTOR** | Bổ sung xử lý `PLAYER_STATS`, `ERROR`, `NOTIFICATION` hiển thị lên UI thay vì chỉ `System.out`, dọn file tạm WAV sau khi phát voice chat, tách nhỏ khối `switch-case`. |
| `utils/PasswordUtil.java` | **IMPROVE** | Thống nhất chỉ băm mật khẩu ở **1 nơi** (phía server, hoặc bổ sung salt nếu nâng cấp), tránh băm lặp 2 lần như hiện tại. |
| `BaoCaoDACS3.md` | **REBUILD** | Cập nhật/viết lại thành Báo cáo ĐACS4 cho khớp 100% với kiến trúc, giao thức `\|`, cấu trúc thư mục và giao diện mới. |
| `model/GameState.java` | **REMOVE** | Toàn bộ file đang bị comment, không được sử dụng ở đâu. |
| `client/ClientMain.java` | **REMOVE** | Lớp rỗng chỉ in nhắc nhở dùng `ClientFX.java`. |
| `server/ServerMain.java` | **REMOVE** | Logic cũ trùng lặp với `ServerBackend`, `main()` chỉ gọi chuyển tiếp sang `ServerUI.main()`. |
| `Test.java`, `TestProtocol.java` | **REMOVE** | File test tạm nằm ở default package; nên xóa hoặc chuyển thành unit test chuẩn trong `src/test/java`. |
| `fix_issues.py`, `fix_changes.txt`, `tienlencards.db` | **REMOVE** | File phụ/rác còn sót lại trong repo (cần xác nhận với nhóm trước khi xóa). |

---

## Thuật ngữ cần biết

- **KEEP / IMPROVE / REFACTOR / REBUILD / REMOVE**: 5 mức phân loại khi nâng cấp dự án: giữ nguyên (Keep), giữ khung và sửa lỗi/nâng cấp (Improve), viết lại cấu trúc bên trong cho sạch và tối ưu hơn mà không đổi tính năng (Refactor), làm lại từ đầu (Rebuild), và xóa bỏ phần thừa (Remove).
- **UI Thread (JavaFX Application Thread)**: Luồng duy nhất được phép vẽ và thay đổi giao diện JavaFX. Nếu chạy truy vấn database nặng trên luồng này, toàn bộ cửa sổ sẽ bị đơ (treo).
- **DTO (Data Transfer Object)**: Lớp đơn giản chỉ chứa dữ liệu để truyền hoặc hiển thị (như `PlayerInfo`, `SessionInfo`).
- **DAO (Data Access Object)**: Lớp chuyên trách giao tiếp với database (thực hiện SELECT/INSERT/UPDATE), giúp tách code SQL ra khỏi logic nghiệp vụ.
- **Race condition / Desync (Lệch đồng bộ)**: Tình trạng giao diện Client hiển thị một đằng (ví dụ đã xóa bài trên tay) nhưng Server lại xử lý một nẻo (từ chối nước đi), khiến dữ liệu hai bên không khớp nhau.
