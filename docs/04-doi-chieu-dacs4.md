# Đối chiếu ĐACS4 & Đề xuất Phương án Phạm vi (Lượt D)

## 1. Thông tin đầu vào ĐACS4
- **Tên đề tài**: *Ứng dụng chơi bài Tiến Lên Miền Nam sử dụng giao thức TCP/IP và mô hình Client-Server*
- **Kiến trúc & công nghệ giữ nguyên**: Mô hình Client-Server, giao thức TCP/IP (`Socket` / `ServerSocket`), ngôn ngữ Java 17, giao diện JavaFX, cơ sở dữ liệu MySQL.
- **Định hướng chính của nhóm (2 người)**: Kế thừa nền tảng ĐACS3, **trau chuốt lại giao diện và tối ưu các tính năng hiện có** cho ổn định, mượt mà và hoàn chỉnh.
- **Thông tin chưa có**: Số tuần còn lại cụ thể và danh sách yêu cầu bắt buộc chi tiết từ đề cương giảng viên (các phương án bên dưới được chia theo 3 mức thời gian tham khảo để nhóm tự chọn).

---

## 2. Bảng đối chiếu Thành phần: ĐACS3 vs ĐACS4

| Thành phần | ĐACS3 (Hiện trạng trong code) | ĐACS4 (Mục tiêu nâng cấp) | Hành động |
|---|---|---|---|
| **Kiến trúc mạng TCP** | Thread-per-client (`new Thread(handler).start()`), giao thức `ACTION\|DATA`, chưa xử lý `unregisterPlayer` khi tự thoát | Giao thức `Protocol` đầy đủ hằng số, dọn sạch trạng thái khi ngắt kết nối; có thể tối ưu bằng Thread Pool / Heartbeat | **Cải tiến** |
| **Mô hình dữ liệu (`model/`)** | `Card`, `Deck`, `Move`, `Player`, `Message` hoạt động ổn; `GameState` bị comment toàn bộ | Giữ nguyên 5 lớp lõi, xóa `GameState` chết | **Kế thừa** (xóa file thừa) |
| **Luật chơi (`GameLogic`, `GameSession`)** | Chặt 2 cơ bản; thiếu chặt chồng (`FOUR_KIND` chặt 3 đôi thông...), thiếu kiểm tra bài trên tay, lỗi `winnerIndex`, chưa check số dư | Đủ luật chặt chồng, kiểm tra bài trên tay ở Server, an toàn chỉ số lượt đánh, kiểm tra số dư trước/sau ván | **Cải tiến** |
| **Cơ sở dữ liệu (`UserManager`)** | 1147 dòng ôm mọi thứ; lỗi tên bảng `game_history` & cột `player_id`; mở connection mới mỗi query; thiếu Transaction khi trả thưởng | Sửa khớp schema, bọc Transaction lúc kết thúc ván, tách DAO + cấu hình DB ngoài code (không hardcode mật khẩu) | **Cải tiến / Tái cấu trúc** |
| **Bảo mật xác thực (`PasswordUtil`, `ClientFX`)** | Băm SHA-256 **2 lần** không salt ở cả Client và Server | Thống nhất băm 1 lần phía Server (hoặc kèm salt) | **Cải tiến** |
| **Thống kê & Lịch sử (`GET_PLAYER_STATS`)** | Client gửi request + `Thread.sleep(1000)` nhưng Server & Listener chưa xử lý | Nối thông suốt Client ↔ Server ↔ DB (`game_records`), hiển thị bất đồng bộ không sleep | **Cải tiến** (hoàn thiện phần dở dang) |
| **Giao diện Client (`ClientFX`, `TableSelectionUI`, `TienLenClientUI`, `style.css`)** | Đủ 3 màn hình + chat chữ/voice; nhưng `style.css` lỗi cú pháp, tự xóa bài trước khi server duyệt, chìm chữ khung chat, không reset scale, nút Đăng ký bị khóa | Sửa toàn bộ lỗi UI/đồng bộ, thống nhất theme & CSS cho cả 3 màn hình, hiệu ứng lá bài & lượt đánh mượt mà | **Cải tiến** |
| **Giao diện & Backend Server (`ServerUI`, `ServerBackend`)** | Bảng player/session/stats + kick/đóng bàn; `ServerUI` chạy SQL ngay trên JavaFX UI thread mỗi 2s | Đưa truy vấn DB ra background thread để UI server mượt, đồng bộ đóng bàn với DB | **Cải tiến** |
| **Build & File thừa (`pom.xml`, `ClientMain`, `ServerMain`, `Test*`)** | `pom.xml` sai `mainClass` và thiếu `javafx-media`; tồn tại nhiều file rỗng/test tạm | Sửa chuẩn `pom.xml`, dọn các file không sử dụng | **Kế thừa + Dọn dẹp** |
| **Tài liệu báo cáo (`BaoCaoDACS3.md`, `DATABASE_SCHEMA.md`)** | Lệch code (ghi JSON, nhắc Android, sai danh sách file `server/`, thiếu `utils/`, sai số bảng DB) | Viết lại Báo cáo ĐACS4 và `DATABASE_SCHEMA.md` khớp 100% với code và giao diện mới | **Mới** (viết lại đồng bộ) |

---

## 3. Đề xuất 3 Phương án Phạm vi (Nhóm 2 người)

### Phương án 1: Tinh gọn — Sửa lỗi cốt lõi & Trau chuốt UI (Phù hợp nếu còn ~2–3 tuần)
- **Phạm vi**:
  - Sửa toàn bộ lỗi **P0 & P1**: sửa tên bảng/cột SQL trong `UserManager`, thêm Transaction khi kết thúc ván, kiểm tra bài trên tay & `winnerIndex` trong `GameSession`, sửa `ClientHandler.finally` dọn `onlinePlayers`, hoàn thiện `GET_PLAYER_STATS`, đưa SQL của `ServerUI` ra background thread.
  - Sửa lỗi UI & trau chuốt giao diện: sửa `style.css`, chỉ xóa bài trên Client khi Server xác nhận `PLAY`, sửa màu khung chat, reset animation, sửa `enableRegisterControls()`, thống nhất giao diện Login / Chọn bàn / Bàn chơi, sửa `pom.xml`.
  - Cập nhật lại Báo cáo ĐACS4 khớp với code.
- **Đánh đổi**:
  - *Ưu điểm*: Rủi ro thấp nhất, chắc chắn hoàn thành đúng hạn, ứng dụng chạy ổn định và giao diện đẹp hơn rõ rệt.
  - *Nhược điểm*: Chưa tái cấu trúc `UserManager` (vẫn giữ cấu trúc cũ đã sửa lỗi), chưa bổ sung luật Tiến Lên nâng cao (tới trắng, phạt thối heo) hay cơ chế reconnect.

### Phương án 2 (Khuyên dùng): Cân bằng — Tối ưu tính năng, Luật chuẩn & Giao diện hoàn thiện (Phù hợp nếu còn ~4–6 tuần)
- **Phạm vi**: Bao gồm toàn bộ **Phương án 1**, cộng thêm:
  - **Hoàn thiện luật chơi (`GameLogic` + `GameSession`)**: bổ sung đủ luật chặt chồng, bắt buộc 3♠ ở lượt đầu ván đầu, xử lý phạt thối 2 / tới trắng (nếu nhóm thống nhất áp dụng).
  - **Tối ưu trải nghiệm chơi**: thêm bộ đếm thời gian mỗi lượt đánh (hết giờ tự `PASS`), hiển thị số lá bài còn lại của đối thủ trên bàn, dọn file tạm `.wav` của voice chat, xử lý thông báo rõ ràng khi mất kết nối server.
  - **Làm sạch code & cấu hình**: tách cấu hình DB ra file `.properties`, sửa cơ chế băm mật khẩu 1 lần có salt, tách nhỏ `UserManager` và dọn toàn bộ file chết (`GameState`, `ClientMain`, `ServerMain`).
- **Đánh đổi**:
  - *Ưu điểm*: Đáp ứng trọn vẹn mục tiêu *"trau chuốt giao diện và tối ưu các tính năng"*, vừa sạch code vừa có điểm nhấn rõ ràng so với ĐACS3 khi bảo vệ.
  - *Nhược điểm*: Cần phối hợp chặt giữa 2 thành viên (người làm Server/Logic/DB, người làm Client/UI/CSS) và dành thời gian test nhiều client cùng lúc để tránh lệch giao thức.

### Phương án 3: Mở rộng sâu — Tái cấu trúc kiến trúc mạng & Tự động hóa kiểm thử (Phù hợp nếu còn ~6–8+ tuần)
- **Phạm vi**: Bao gồm toàn bộ **Phương án 2**, cộng thêm:
  - Chuyển server sang **Thread Pool (`ExecutorService`)** và dùng **Connection Pool** cho MySQL thay cho `DriverManager.getConnection` thủ công.
  - Bổ sung cơ chế **Heartbeat + Reconnect** (cho phép người chơi rớt mạng vào lại đúng ván đang chơi).
  - Viết **Unit Test (JUnit)** cho `GameLogic` và `Protocol`.
- **Đánh đổi**:
  - *Ưu điểm*: Chất lượng kỹ thuật rất cao, trả lời tốt các câu hỏi chuyên sâu về hiệu năng và chịu lỗi của giảng viên.
  - *Nhược điểm*: Độ phức tạp cao (đặc biệt là cơ chế reconnect giữa ván), dễ trễ tiến độ nếu thời gian ít hoặc thành viên chưa quen lập trình đa luồng nâng cao.

---

## 4. Gợi ý phân công khung cho nhóm 2 người (tham khảo)
- **Thành viên A (Backend / Server / DB / Luật chơi)**: `UserManager` (schema, transaction, config), `GameLogic`, `GameSession`, `ClientHandler`, `ServerBackend`, `ServerUI` (background thread).
- **Thành viên B (Frontend / Client / UI-UX / Giao thức)**: `ClientFX`, `TableSelectionUI`, `TienLenClientUI`, `ServerListener`, `style.css`, `pom.xml`, đồng bộ hằng số `Protocol`.
- **Cả 2 cùng làm**: Kiểm thử tích hợp (chạy 1 Server + 2–4 Client cùng lúc) và cập nhật Báo cáo ĐACS4.
