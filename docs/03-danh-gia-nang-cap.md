# Đánh giá Nâng cấp & Phân loại Thành phần (Lượt C) — TienLenMienNamTCP

> Tài liệu này tổng hợp từ [01-khao-sat-nhanh.md](file:///d:/đacs3/project/TienLenMienNamTCP/docs/01-khao-sat-nhanh.md) và [02-phan-tich-sau.md](file:///d:/đacs3/project/TienLenMienNamTCP/docs/02-phan-tich-sau.md), phân loại từng thành phần hiện có theo 5 mức (**KEEP / IMPROVE / REFACTOR / REBUILD / REMOVE**) và sắp xếp rủi ro theo mức ưu tiên. **Chưa đề xuất tính năng mới.**

---

## 1. Phân loại từng thành phần

### 1.1. KEEP (Giữ nguyên thiết kế & cấu trúc, không cần sửa lớn)

| Thành phần | Lý do | Bằng chứng |
|---|---|---|
| `src/main/java/tienlen/model/Card.java` | Biểu diễn quân bài (rank `3..15`, suit `SPADES..HEARTS`), hỗ trợ parse chuỗi và `Comparable` rõ ràng. | `Card.java` (lines 3-89) |
| `src/main/java/tienlen/model/Deck.java` | Tạo bộ bài 52 lá, xáo bài và chia bài cho số người chơi gọn gàng. | `Deck.java` (lines 7-52) |
| `src/main/java/tienlen/model/Move.java` | Đóng gói nước đi, `ComboType`, độ dài và lá cao nhất dựa trên `GameLogic`. | `Move.java` (lines 8-57) |
| `src/main/java/tienlen/model/Player.java` | Lưu tên, bài trên tay, số dư, tiền cược của người chơi. | `Player.java` (lines 8-97) |
| `src/main/java/tienlen/model/Message.java` | Đóng gói cặp `action` và `data` đơn giản, phù hợp với giao thức hiện tại. | `Message.java` (lines 3-27) |
| `src/main/java/tienlen/server/PlayerInfo.java` | DTO đơn giản để hiển thị danh sách người chơi trên bảng của `ServerUI`. | `PlayerInfo.java` (lines 9-68) |
| `src/main/java/tienlen/server/SessionInfo.java` | DTO đơn giản để hiển thị danh sách bàn chơi trên bảng của `ServerUI`. | `SessionInfo.java` (lines 9-73) |

### 1.2. IMPROVE (Giữ nền tảng, sửa lỗi & trau chuốt/tối ưu)

| Thành phần | Lý do | Bằng chứng |
|---|---|---|
| `src/main/java/tienlen/utils/Protocol.java` | Giữ định dạng `ACTION\|DATA` gọn nhẹ; cần bổ sung các hằng số đang dùng rải rác dạng chuỗi cứng (`CREATE_SESSION`, `LEAVE_SESSION`, `TURN`, `WIN`, `END`, `KICKED`, `NOTIFICATION`, `ERROR`, `SESSION_SELECTED`). | `Protocol.java` (lines 5-57); `ClientHandler.java` (lines 118, 175, 286); `ServerListener.java` (lines 198, 220, 261, 305, 309) |
| `src/main/java/tienlen/utils/PasswordUtil.java` | Đang băm SHA-256 không salt ở cả client và server; cần thống nhất băm ở 1 phía (server) và cân nhắc bổ sung salt. | `PasswordUtil.java` (lines 6-22); `ClientFX.java` (lines 159, 246); `UserManager.java` (lines 144, 173) |
| `src/main/java/tienlen/logic/GameLogic.java` | Đã cài đặt nhận diện bộ bài cơ bản và chặt 2; cần bổ sung luật chặt còn thiếu (`FOUR_KIND` chặt `THREE_PAIRS`, `FOUR_PAIRS` chặt `THREE_PAIRS`) và kiểm tra điều kiện biên. | `GameLogic.java` (lines 12-105) |
| `src/main/java/tienlen/server/GameSession.java` | Lõi điều phối ván chơi hoạt động đúng luồng, nhưng cần sửa: kiểm tra bài trên tay khi `PLAY`, kiểm tra biên `winnerIndex`, kiểm tra số dư trước/sau ván, đồng bộ khóa khi truy cập `connections`. | `GameSession.java` (lines 172-181, 228-272, 359-419) |
| `src/main/java/tienlen/server/ClientHandler.java` | Đã xử lý hầu hết thông điệp; cần bổ sung `GET_PLAYER_STATS`, dọn `backend.unregisterPlayer()` trong `finally` khi client tự thoát, và thay chuỗi cứng bằng `Protocol.*`. | `ClientHandler.java` (lines 95-211) |
| `src/main/java/tienlen/server/ServerBackend.java` | Quản lý `gameSessions`, `onlinePlayers`, `clientHandlers` ổn định; cần đồng bộ trạng thái đóng bàn với DB và tránh `NullPointerException` nếu handler null. | `ServerBackend.java` (lines 14-197) |
| `src/main/java/tienlen/client/ClientFX.java` | Luồng chuyển màn hình rõ ràng; cần sửa `enableRegisterControls()` đang để trống, xử lý đóng/mở lại socket khi quay về Login, và trau chuốt UI Login/Register đồng bộ theme. | `ClientFX.java` (lines 39-255, 336-339, 355-358) |
| `src/main/java/tienlen/client/TableSelectionUI.java` | Giao diện chọn bàn đã có khung đẹp; cần bỏ `Thread.sleep(1000)` khi lấy thống kê, nối dữ liệu thật từ `PLAYER_STATS`, và trau chuốt hiển thị/bố cục. | `TableSelectionUI.java` (lines 64-266, 300-326, 702-733) |
| `src/main/java/tienlen/client/TienLenClientUI.java` | Giao diện bàn chơi đầy đủ; cần sửa: không tự xóa bài trước khi server duyệt, chặn NPE khi `currentTurn == null`, sửa màu nền/chữ chatArea, reset `scaleX`/`scaleY` sau animation, dọn ô người chơi trống, không truy cập cứng `getChildren().get(1)`. | `TienLenClientUI.java` (lines 362, 405-411, 424-451, 480-502) |
| `src/main/resources/style.css` | Cần sửa lỗi cú pháp ở cuối `.card:focused`, thống nhất màu sắc toàn bộ màn hình và khắc phục xung đột với inline style. | `style.css` (lines 46-56, 205-212) |
| `pom.xml` | Cần sửa `<mainClass>` từ `tienlen.clientfx.GameClientFX` thành `tienlen.client.ClientFX` và thêm dependency `javafx-media` để build/run chuẩn qua Maven. | `pom.xml` (lines 17-34, 48-50) |

### 1.3. REFACTOR (Tái cấu trúc bên trong để sửa lỗi kiến trúc & tối ưu hiệu năng)

| Thành phần | Lý do | Bằng chứng |
|---|---|---|
| `src/main/java/tienlen/server/UserManager.java` | File dài 1147 dòng ôm cả khởi tạo schema, auth, quản lý session, kết quả trận, thống kê; query sai bảng `game_history` và lệch cột `user_id`/`player_id`; mở JDBC connection mới mỗi truy vấn; thiếu Transaction khi cộng/trừ tiền; hardcode thông tin DB. Cần tách lớp (Config/Pool + DAO) và chuẩn hóa schema. | `UserManager.java` (lines 8-10, 40-127, 384, 418, 1085, 1135) |
| `src/main/java/tienlen/server/ServerUI.java` | File dài 903 dòng và đang gọi hàng loạt truy vấn SQL trong `Platform.runLater()` trên JavaFX UI thread mỗi 2 giây gây đơ giao diện. Cần tách truy vấn dữ liệu sang background thread và tách nhỏ các tab UI. | `ServerUI.java` (lines 562-577, 680-710) |
| `src/main/java/tienlen/client/ServerListener.java` | Cần bổ sung xử lý `PLAYER_STATS`, `ERROR`, `NOTIFICATION` hiển thị lên UI; dọn file tạm `.wav` sau khi phát `CHAT_VOICE`; tái cấu trúc khối `switch-case` dài và xử lý thông báo khi mất kết nối server. | `ServerListener.java` (lines 71-345) |

### 1.4. REBUILD (Làm lại hoàn toàn)

| Thành phần | Lý do | Bằng chứng |
|---|---|---|
| Tài liệu báo cáo (`BaoCaoDACS3.md` → Báo cáo ĐACS4) & `DATABASE_SCHEMA.md` | Báo cáo cũ có nhiều điểm không khớp code (ghi JSON thay vì `\|`, nhắc Android, copy nhầm nội dung `server/` từ `model/`, liệt kê `AuthListener.java` không tồn tại); `DATABASE_SCHEMA.md` mô tả 8 bảng khác với 5 bảng thực tế trong `initDatabase()`. Cần viết lại đồng bộ 100% với code sau khi nâng cấp. | `BaoCaoDACS3.md` (lines 211, 491, 607, 653-669, 717, 753); `DATABASE_SCHEMA.md` (lines 234-504) |

### 1.5. REMOVE (Xóa bỏ thành phần thừa / code chết)

| Thành phần | Lý do | Bằng chứng |
|---|---|---|
| `src/main/java/tienlen/model/GameState.java` | Toàn bộ file đang bị comment (`//`), không được sử dụng ở bất kỳ đâu. | `GameState.java` (lines 1-29) |
| `src/main/java/tienlen/client/ClientMain.java` | Lớp rỗng chỉ in thông báo hướng dẫn chạy `ClientFX.java`. | `ClientMain.java` (lines 1-11) |
| `src/main/java/tienlen/server/ServerMain.java` | Trùng lặp quản lý session kiểu cũ với `ServerBackend`; `main()` chỉ gọi chuyển tiếp sang `ServerUI.main()`. | `ServerMain.java` (lines 12-98) |
| `src/main/java/Test.java`, `src/main/java/TestProtocol.java` | File test thủ công tạm thời nằm ở default package, không phải unit test chuẩn. | `Test.java` (lines 1-16), `TestProtocol.java` (lines 1-13) |
| `fix_issues.py`, `fix_changes.txt`, `tienlencards.db` | File phụ/rác không được code Java tham chiếu tới (cần xác nhận với nhóm trước khi xóa). | Kết quả khảo sát cây thư mục trong `01-khao-sat-nhanh.md` |

---

## 2. Danh sách Rủi ro theo Mức độ Ưu tiên

### Mức P0 — Nghiêm trọng (Gây crash, sai lệch tiền/dữ liệu hoặc lỗ hổng gian lận trực tiếp)
1. **Lỗi SQL sai bảng & sai cột trong `UserManager.java`**:
   - Truy vấn `game_history` (lines 384, 418) và `player_id` trên `player_sessions` (lines 1085, 1135) gây lỗi SQL runtime khi đăng nhập/thoát hoặc xem thống kê.
2. **Không kiểm tra bài trên tay ở Server (`GameSession.processMove`, lines 244-260)**:
   - Client có thể gửi lá bài bất kỳ hợp lệ để đánh mà không cần sở hữu trên tay.
3. **Thiếu Transaction DB và kiểm tra số dư âm khi kết thúc ván (`GameSession.endGame`, lines 371-395)**:
   - Có thể trừ âm tiền hoặc mất cân bằng tiền giữa người thắng và người thua nếu lỗi DB xảy ra giữa chừng.
4. **Lệch đồng bộ tay bài Client-Server (`TienLenClientUI.playSelectedCards`, lines 362, 405-411)**:
   - Xóa bài trên UI trước khi server duyệt (làm mất bài trên màn hình nếu bị từ chối), và có nguy cơ `NullPointerException` khi `currentTurn == null`.
5. **Tràn chỉ số `winnerIndex` khi bắt đầu ván mới (`GameSession.startGame`, line 181)**:
   - Gây `IndexOutOfBoundsException` nếu người thắng ván trước rời bàn làm số người chơi giảm xuống.

### Mức P1 — Cao (Hỏng tính năng, treo UI, kẹt trạng thái người chơi)
1. **Đơ giao diện `ServerUI` (`updateStatisticsPanel`, lines 683-710)**:
   - Chạy nhiều câu SQL trực tiếp trên JavaFX Application Thread mỗi 2 giây.
2. **Kẹt người chơi trong danh sách online (`ClientHandler.finally`, lines 194-211)**:
   - Thiếu `backend.unregisterPlayer(currentUsername)` khi client tự ngắt kết nối.
3. **Tính năng xem thống kê (`GET_PLAYER_STATS`) bị đứt luồng**:
   - `TableSelectionUI` (lines 302, 704) gửi yêu cầu và `Thread.sleep(1000)` chờ, nhưng `ClientHandler` và `ServerListener` chưa xử lý.
4. **Nút Đăng ký bị khóa vĩnh viễn khi đăng ký thất bại (`ClientFX.enableRegisterControls`, lines 336-339)**:
   - Hàm để trống khiến người dùng không thể bấm đăng ký lại nếu nhập sai/trùng tên.
5. **Băm mật khẩu 2 lần không salt & hardcode thông tin DB (`ClientFX.java`, `UserManager.java`)**:
   - Rủi ro bảo mật và bất tiện khi triển khai trên máy khác.

### Mức P2 — Trung bình (Lỗi hiển thị UI/UX, rò rỉ tài nguyên, cấu hình build)
1. **Lỗi cú pháp `style.css` (lines 50-56) và chìm màu chữ khung chat (`TienLenClientUI.java`, line 445)**:
   - Ảnh hưởng trực tiếp thẩm mỹ và khả năng đọc thông báo tới lượt.
2. **Animation phóng to không reset & không dọn ô người chơi trống (`TienLenClientUI.java`, lines 432-451, 480-502)**:
   - Ô người chơi bị kẹt kích thước lớn hoặc giữ tên người đã rời bàn.
3. **File tạm `.wav` không được xóa (`ServerListener.java`, lines 287-289)**:
   - Tích tụ rác trên ổ đĩa khi dùng voice chat nhiều lần.
4. **Sai `mainClass` và thiếu `javafx-media` trong `pom.xml` (lines 17-34, 49)**:
   - Không chạy trực tiếp được bằng `mvn javafx:run` nếu chưa sửa.

### Mức P3 — Thấp (Nợ kỹ thuật, bảo trì lâu dài)
1. **Thiếu hằng số trong `Protocol.java` và dùng string literal rải rác**:
   - Dễ gõ nhầm tên thông điệp khi mở rộng/sửa code.
2. **Truy cập cứng chỉ số node UI (`box.getChildren().get(1)`) và code chết (`GameState.java`, `ClientMain.java`, `ServerMain.java`)**:
   - Gây khó khăn khi trau chuốt lại layout giao diện và làm rối cấu trúc dự án.
3. **Báo cáo `BaoCaoDACS3.md` & `DATABASE_SCHEMA.md` lệch với code thực tế**:
   - Dễ gây hiểu nhầm khi bảo vệ đồ án trước giảng viên.
