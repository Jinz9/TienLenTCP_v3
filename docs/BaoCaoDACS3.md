TRƯỜNG ĐẠI HỌC CÔNG NGHỆ THÔNG TIN &

TRUYỀN THÔNG VIỆT – HÀN

> **Khoa Khoa Học Máy Tính**
>
> <img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image1.png" style="width:1.375in;height:0.80417in" />
>
> ĐỒ ÁN CƠ SỞ 3
>
> **TÊN ĐỀ TÀI: ỨNG DỤNG CHƠI BÀI TIẾN LÊN MIỀN NAM SỬ DỤNG GIAO THỨC TCP/IP VÀ MÔ HÌNH CLIENT-SERVER**

<table>
<colgroup>
<col style="width: 40%" />
<col style="width: 59%" />
</colgroup>
<thead>
<tr class="header">
<th>Sinh viên thực hiện:</th>
<th><p>Nguyễn Hữu Trí – 24IT.B204</p>
<p>Đào Duy Nhật – 23IT.B148</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Lớp :</td>
<td><p>24SE6</p>
<p>23SE4</p></td>
</tr>
<tr class="even">
<td>Giảng viên hướng dẫn:</td>
<td>TS.Lê Tân</td>
</tr>
</tbody>
</table>

Đà Nẵng, tháng 6 năm 2026

TRƯỜNG ĐẠI HỌC CÔNG NGHỆ THÔNG TIN &

TRUYỀN THÔNG VIỆT – HÀN

> **Khoa Khoa Học Máy Tính**
>
> <img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image1.png" style="width:1.375in;height:0.80417in" />
>
> ĐỒ ÁN CƠ SỞ 3
>
> **TÊN ĐỀ TÀI: ỨNG DỤNG CHƠI BÀI TIẾN LÊN MIỀN NAM SỬ DỤNG GIAO THỨC TCP/IP VÀ MÔ HÌNH CLIENT-SERVER**

<table>
<colgroup>
<col style="width: 40%" />
<col style="width: 59%" />
</colgroup>
<thead>
<tr class="header">
<th>Sinh viên thực hiện:</th>
<th><p>Nguyễn Hữu Trí – 24IT.B204</p>
<p>Đào Duy Nhật – 23IT.B148</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Lớp :</td>
<td><p>24SE6</p>
<p>23SE4</p></td>
</tr>
<tr class="even">
<td>Giảng viên hướng dẫn:</td>
<td>TS.Lê Tân</td>
</tr>
</tbody>
</table>

Đà Nẵng, tháng 6 năm 2026

  
  
**NHẬN XÉT CỦA GIẢNG VIÊN HƯỚNG DẪN**

**LỜI CẢM ƠN**

Để thực hiện và hoàn thành tốt bài tập lớn này, chúng em đã nhận được sự giúp đỡ và hướng dẫn rất tận tình của các thầy cô thuộc Đại học Công Nghệ Thông Tin và Truyền Thông Việt - Hàn – Đại Học Đà Nẵng. Chúng em xin cảm ơn các thầy cô thuộc bộ môn chuyên ngành đã cung cấp cho chúng em các thông tin, kiến thức vô cùng quý báu và cần thiết trong suốt thời gian quá để chúng em có thể thực hiện và hoàn thành đồ án của mình. Đặc biệt chúng em xin chân thành cảm ơn thầy TS. Lê Tân người giảng dạy và hướng dẫn chúng em trong thời gian thực hiện đồ án này.

Do giới hạn về mặt thời gian và kiến thức cũng như kinh nghiệm thực tiễn nên đề tài không tránh khỏi những sai sót. Chúng em rất mong nhận được sự thông cảm của quý thầy cô và mong đón nhận những góp ý của thầy cô và các bạn.

Em xin chân thành cảm ơn!

**MỤC LỤC**

[DANH MỤC HÌNH VẼ [i](#_Toc148951237)](#_Toc148951237)

[MỞ ĐẦU [1](#_Toc148951239)](#_Toc148951239)

[1. Giới thiệu [1](#_Toc148951240)](#_Toc148951240)

[2. Mục tiêu [1](#_Toc148951241)](#_Toc148951241)

[3. Đối tượng và phạm vi [1](#_Toc148951242)](#_Toc148951242)

[4. Các bước thực hiện [2](#_Toc148951243)](#_Toc148951243)

[5. Cấu trúc [3](#_Toc218677515)](#_Toc218677515)

[Chương 1. CƠ SỞ LÝ THUYẾT [4](#chương-1.-cơ-sở-lý-thuyết)](#chương-1.-cơ-sở-lý-thuyết)

[1.1. Ngôn ngữ lập trình Java [4](#ngôn-ngữ-lập-trình-java)](#ngôn-ngữ-lập-trình-java)

[1.2. Giao thức TCP/IP [5](#giao-thức-tcpip)](#giao-thức-tcpip)

[1.3. Mô hình Client-Server [5](#mô-hình-client-server)](#mô-hình-client-server)

[1.4. JavaFX cho giao diện đồ họa [6](#javafx-cho-giao-diện-đồ-họa)](#javafx-cho-giao-diện-đồ-họa)

[1.5. Hệ quản trị cơ sở dữ liệu MySQL [7](#hệ-quản-trị-cơ-sở-dữ-liệu-mysql)](#hệ-quản-trị-cơ-sở-dữ-liệu-mysql)

[Chương 2. PHÂN TÍCH THIẾT KẾ HỆ THỐNG [9](#phân-tich-thiêt-kê-hê-thông)](#phân-tich-thiêt-kê-hê-thông)

[2.1. Sơ lược về bài toán cần giải quyết (về mặt chức năng) [9](#sơ-lược-về-bài-toán-cần-giải-quyết-về-mặt-chức-năng)](#sơ-lược-về-bài-toán-cần-giải-quyết-về-mặt-chức-năng)

[2.2. Phân tích thiết kế [9](#_Toc218677524)](#_Toc218677524)

[2.2.1. Danh sách actor [9](#danh-sách-actor)](#danh-sách-actor)

[2.2.2. Danh sách use case [10](#danh-sách-use-case)](#danh-sách-use-case)

[2.2.3. Đặc tả use case [10](#đặc-tả-use-case)](#đặc-tả-use-case)

[2.2.4. Biểu đồ hoạt động [11](#biểu-đồ-hoạt-động)](#biểu-đồ-hoạt-động)

[2.2.5. Biểu đồ tuần tự [12](#biểu-đồ-tuần-tự)](#biểu-đồ-tuần-tự)

[2.2.6. Biểu đồ cơ sở dữ liệu [12](#biểu-đồ-cơ-sở-dữ-liệu)](#biểu-đồ-cơ-sở-dữ-liệu)

[2.3. Ứng dụng mô hình/giao thức [12](#ứng-dụng-mô-hìnhgiao-thức)](#ứng-dụng-mô-hìnhgiao-thức)

[2.4. Tổng kết chương 2 [14](#tổng-kết-chương-2)](#tổng-kết-chương-2)

[Chương 3. XÂY DỰNG HỆ THỐNG [15](#xây-dựng-hệ-thống)](#xây-dựng-hệ-thống)

[3.1. Cấu trúc dự án [15](#câu-truc-dư-an)](#câu-truc-dư-an)

[3.1.1. Tổng quan [15](#tông-quan)](#tông-quan)

[3.1.2. Thư mục client [15](#thư-mục-client)](#thư-mục-client)

[3.1.3. Thư mục logic [15](#thư-muc-logic)](#thư-muc-logic)

[3.1.4. Thư mục model [16](#thư-muc-model)](#thư-muc-model)

[3.1.5. Thư mục server [16](#thư-muc-server)](#thư-muc-server)

[3.2. Giao diện ứng dụng [17](#giao-diên-ưng-dung)](#giao-diên-ưng-dung)

[3.2.1. Giao diện trang đăng nhập, đăng ký [17](#giao-diên-trang-đăng-nhập-đăng-ký)](#giao-diên-trang-đăng-nhập-đăng-ký)

[3.2.2. Giao diện trang chính ứng dụng [19](#giao-diện-trang-chính-ứng-dụng)](#giao-diện-trang-chính-ứng-dụng)

[3.2.3. Giao diện server [20](#giao-diên-server)](#giao-diên-server)

[3.3. Kết luận chương 3: [22](#kết-luận-chương-3)](#kết-luận-chương-3)

[KẾT LUẬN [23](#_Toc148951262)](#_Toc148951262)

[1.Kết quả đạt được [23](#_Toc148951263)](#_Toc148951263)

[2.Hướng phát triển [23](#_Toc148951264)](#_Toc148951264)

[TÀI LIỆU THAM KHẢO [25](#tài-liệu-tham-khảo)](#tài-liệu-tham-khảo)

<span id="_Toc148951237" class="anchor"></span>DANH MỤC HÌNH VẼ

[Hình 1 Ngôn ngữ lập trình java [4](#_Toc218677681)](#_Toc218677681)

[Hình 2 Giao thức TCP/IP [5](#_Toc218677682)](#_Toc218677682)

[Hình 3 Mô hình client-server [6](#_Toc218677683)](#_Toc218677683)

[Hình 4 MySQL [7](#_Toc218677684)](#_Toc218677684)

[Hình 5 Biểu đồ hoạt động [11](#_Toc218677685)](#_Toc218677685)

[Hình 6 Biểu đồ tuần tự [12](#_Toc218677686)](#_Toc218677686)

[Hình 7 Biểu đồ CSDL [13](#_Toc218677687)](#_Toc218677687)

[Hình 8 Giao diện trang đăng nhập đăng ký [18](#_Toc218677688)](#_Toc218677688)

[Hình 9 Giao diện đăng nhập không thành công [19](#_Toc218677689)](#_Toc218677689)

[Hình 10 Giao diện trang chủ [20](#_Toc218677690)](#_Toc218677690)

[Hình 11 Giao diện bàn chơi [21](#_Toc218677691)](#_Toc218677691)

[Hình 12 Giao diện quản lý người dùng [21](#_Toc218677692)](#_Toc218677692)

[Hình 13Giao diện quản lý bàn chơi [22](#_Toc218677693)](#_Toc218677693)

[Hình 14 Giao diện thống kê [22](#_Toc218677694)](#_Toc218677694)

<span id="_Toc148951239" class="anchor"></span>

MỞ ĐẦU

1.  <span id="_Toc148951240" class="anchor"></span>Giới thiệu

Trò chơi Tiến Lên Miền Nam (TLMN) là một trò chơi bài phổ biến tại Việt Nam, yêu cầu người chơi phải tính toán và phối hợp chiến thuật để đánh hết bài trước đối thủ. Trong bối cảnh phát triển phần mềm hiện nay, việc xây dựng một ứng dụng cho phép nhiều người chơi trải nghiệm TLMN qua mạng Internet sẽ giúp nâng cao tính tiện ích và trải nghiệm người dùng.

Đồ án “Ứng dụng chơi bài Tiến Lên Miền Nam sử dụng giao thức TCP/IP và mô hình Client-Server” được thực hiện nhằm thiết kế và triển khai một hệ thống trò chơi trực tuyến, cho phép nhiều người chơi kết nối và chơi bài thông qua máy tính hoặc thiết bị Android, đảm bảo giao tiếp ổn định và đồng bộ giữa các client.

2.  <span id="_Toc148951241" class="anchor"></span>Mục tiêu

Đồ án nhằm đạt được các mục tiêu chính sau:

- Xây dựng ứng dụng Client-Server: Thiết kế mô hình server quản lý trò chơi và các client kết nối để chơi bài.

- Ứng dụng giao thức TCP/IP: Sử dụng TCP để truyền dữ liệu giữa server và client, đảm bảo độ tin cậy và tuần tự của thông tin.

- Đồng bộ trò chơi: Cập nhật trạng thái bàn chơi, bài người chơi và lượt đi một cách chính xác giữa các client.

- Giao diện trực quan: Cung cấp giao diện dễ sử dụng, hiển thị bài, lượt đi và thông báo kết quả.

- Đảm bảo tính ổn định và bảo mật: Xử lý lỗi kết nối, ngắt kết nối bất ngờ, và ngăn chặn các thao tác không hợp lệ.

3.  <span id="_Toc148951242" class="anchor"></span>Đối tượng và phạm vi

- Đối tượng nghiên cứu:

<!-- -->

- Quá trình truyền và xử lý dữ liệu giữa các thực thể trong hệ thống (Server-Client) thông qua giao thức TCP/IP.

- Các giao thức mạng và mô hình ứng dụng liên quan đến việc truyền thông tin thời gian thực trpng game nhiều người chơi.

- Các kỹ thuật lập trình đồng thời (concurrency) và xử lý song song trong Java nhằm quản lý nhiều kết nối Client cùng tham giao một phòng chơi, một ván chơi.

- Cơ chế trao đổi thông điệp (message), đồng bộ trạng thái game, quản lý phiên chơi và xử lý các sự kiện trò chơi Tiến Lên Miền Nam.

<!-- -->

- Phạm vi nghiên cứu:

> Đề tài tập trung nghiên cứu và triển khai ở Lớp Ứng Dụng (Application Layer) thông qua việc xây dựng giao thức trao đổi dữ liệu tùy chỉnh giữa Client và Server (ví dụ: thông điệp tạo phòng, tham gia phòng, đánh bài, báo lượt...)
>
> Theo mô hình mạng: Đề tài nghiên cứu sâu cả hai mô hình:

- Server: đóng vai trò điều phối toàn bộ hệ thống-quảng lý người chơi, phòng chơi, luật chơi và xử lý mọi thông điệp.

- Client: gửi yêu cầu (repuest) và nhận phản hồi (response) từ Server, động thời hiển thị giao diện người dùng và thực hiện các thao tác chơi bài.

> Khác với các mô hình phân tán như P2P, đề tài không triển khai P2P, mà chỉnh tập trung vào mô hình Client-Server thuần túy, phù hợp với yêu cầu của môn học và đặc thù hệ thống game bài nhiều người chơi.

4.  <span id="_Toc148951243" class="anchor"></span>Các bước thực hiện

• **Phân tích yêu cầu**: Xác định chức năng chính của server và client, các tình huống chơi bài và luồng dữ liệu.

• **Thiết kế hệ thống**: Vẽ sơ đồ mô hình client-server, xác định các lớp, module, và cấu trúc dữ liệu.

• **Triển khai server**:

Xử lý kết nối client qua TCP/IP.

Quản lý trạng thái bàn chơi và lượt đi.

• **Triển khai client**:

Giao diện hiển thị bài và bàn chơi.

Gửi yêu cầu đánh bài và nhận cập nhật từ server.

• **Kiểm thử và hiệu chỉnh**: Thử nghiệm nhiều client kết nối, xử lý các tình huống lỗi và đồng bộ trạng thái.

• **Hoàn thiện báo cáo**: Tổng hợp kết quả, mô tả cấu trúc chương trình, hình ảnh minh họa và đánh giá hiệu quả.

5.  <span id="_Toc218677515" class="anchor"></span>Cấu trúc

# Chương 1. CƠ SỞ LÝ THUYẾT

## Ngôn ngữ lập trình Java

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image2.png" style="width:3.15972in;height:1.72917in" />

<span id="_Toc218677681" class="anchor"></span>Hình Ngôn ngữ lập trình java

**Java** là ngôn ngữ lập trình hướng đối tượng, được phát triển bởi Sun Microsystems (hiện thuộc Oracle) vào những năm 1995. Java nổi bật với các đặc điểm:

- **Hướng đối tượng**: Java cho phép tổ chức chương trình theo lớp, đối tượng, giúp quản lý mã nguồn lớn dễ dàng và tái sử dụng.

- **Độc lập nền tảng**: Chương trình Java biên dịch thành **bytecode**, chạy trên **Java Virtual Machine (JVM)**, vì vậy có thể chạy trên nhiều hệ điều hành mà không cần chỉnh sửa.

- **Thư viện phong phú**: Java cung cấp nhiều API cho lập trình mạng (TCP/IP, UDP), xử lý file, quản lý luồng (thread), GUI, cơ sở dữ liệu, v.v.

- **Bảo mật và ổn định**: Java kiểm soát bộ nhớ tự động (garbage collector) và có cơ chế xử lý ngoại lệ mạnh mẽ, giảm lỗi runtime.

- **Tại sao chọn Java cho ứng dụng TLMN:**

<!-- -->

- Khả năng **xử lý mạng TCP/IP** dễ dàng qua các lớp Socket, ServerSocket.

- Hỗ trợ **đa luồng (multithreading)**, cần thiết để server phục vụ nhiều client cùng lúc.

- Dễ dàng xây dựng **giao diện đồ họa** bằng JavaFX, giúp client trực quan.

- Tính ổn định cao, tránh crash khi người dùng đánh bài hoặc ngắt kết nối đột ngột.

## Giao thức TCP/IP

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image3.png" style="width:3.0625in;height:1.78472in" />

<span id="_Toc218677682" class="anchor"></span>Hình Giao thức TCP/IP

TCP/IP (Transmission Control Protocol / Internet Protocol) là bộ giao thức chuẩn cho việc truyền dữ liệu trên mạng Internet. Bộ giao thức này bao gồm hai phần chính:

- IP (Internet Protocol): Chịu trách nhiệm định tuyến và chuyển gói dữ liệu từ thiết bị gửi đến thiết bị nhận.

- TCP (Transmission Control Protocol): Đảm bảo dữ liệu được truyền đi một cách đầy đủ, đúng thứ tự và không bị lỗi. TCP thiết lập kết nối kết nối hướng (connection-oriented) giữa hai thiết bị trước khi truyền dữ liệu.

Đặc điểm quan trọng của TCP:

- Đảm bảo tính tin cậy: Dữ liệu bị mất sẽ được gửi lại.

- Truyền dữ liệu theo thứ tự: Gói dữ liệu đến client đúng thứ tự gửi.

- Hỗ trợ kiểm tra lỗi: Mỗi gói dữ liệu có checksum.

- Hỗ trợ đa kết nối: Server có thể phục vụ nhiều client cùng lúc.

Trong ứng dụng TLMN: TCP được sử dụng để đảm bảo rằng mỗi lượt đánh bài, thông báo kết quả hay trạng thái bàn chơi đều chính xác và đồng bộ giữa các client và server.

## Mô hình Client-Server

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image4.png" style="width:4.20928in;height:2.29784in" alt="A diagram of a server connection Description automatically generated" />

<span id="_Toc218677683" class="anchor"></span>Hình Mô hình client-server

Mô hình Client-Server là kiến trúc phổ biến trong lập trình mạng, bao gồm:

Server:

Là trung tâm quản lý trò chơi.

Chịu trách nhiệm lưu trữ trạng thái bàn chơi, quản lý lượt đi, phân phát bài và nhận các lệnh từ client.

Thường chạy liên tục, sẵn sàng phục vụ nhiều client.

Client:

Là ứng dụng mà người chơi sử dụng để tham gia trò chơi.

Gửi yêu cầu đánh bài, nhận thông tin cập nhật từ server và hiển thị giao diện cho người chơi.

Luồng dữ liệu cơ bản trong TLMN theo mô hình Client-Server:

Client \<---TCP---\> Server \<---TCP---\> Client khác

Server luôn là điểm trung tâm, đảm bảo trạng thái trò chơi đồng bộ và công bằng.

## JavaFX cho giao diện đồ họa

JavaFX là thư viện GUI hiện đại của Java, thay thế Swing, cho phép xây dựng giao diện người dùng phong phú và trực quan.

Đặc điểm chính:

- Scene Graph: JavaFX tổ chức các thành phần GUI theo dạng cây (scene graph), dễ quản lý các nút (button, label, canvas…).

- FXML: Hỗ trợ tách GUI ra file FXML, giúp lập trình viên dễ quản lý giao diện và logic riêng biệt.

- Animation & Effects: Hỗ trợ các hiệu ứng, chuyển động, phù hợp để hiển thị các quân bài và thông báo lượt đánh.

- Binding & Property: Cho phép liên kết dữ liệu giữa model và giao diện, ví dụ đồng bộ số lượng bài còn lại với hiển thị trên màn hình.

Ứng dụng JavaFX trong client TLMN:

- Hiển thị bàn chơi và quân bài của từng người chơi.

- Cập nhật thông tin lượt đi, bài đánh, người thắng theo dữ liệu nhận từ server.

- Cho phép người chơi chọn bài và bấm nút đánh dễ dàng.

- Tương tác mượt mà, trực quan hơn so với giao diện console truyền thống.

## Hệ quản trị cơ sở dữ liệu MySQL

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image5.png" style="width:3.125in;height:1.75in" alt="A logo with a dolphin Description automatically generated" />

<span id="_Toc218677684" class="anchor"></span>Hình MySQL

MySQL là một hệ quản trị cơ sở dữ liệu quan hệ (RDBMS – Relational Database Management System) mã nguồn mở, được sử dụng rộng rãi trong các hệ thống ứng dụng client-server và web. MySQL cho phép lưu trữ, truy vấn và quản lý dữ liệu một cách hiệu quả, ổn định và bảo mật.

Đặc điểm chính của MySQL:

- Sử dụng ngôn ngữ SQL (Structured Query Language) để thao tác dữ liệu.

- Hỗ trợ mô hình client-server, phù hợp với các ứng dụng mạng.

- Hiệu năng cao, dễ cài đặt và cấu hình.

- Dễ dàng tích hợp với nhiều ngôn ngữ lập trình, đặc biệt là Java thông qua JDBC.

# PHÂN TÍCH THIẾT KẾ HỆ THỐNG

## Sơ lược về bài toán cần giải quyết (về mặt chức năng) 

Hệ thống cần đáp ứng các yêu cầu chức năng sau:

- Quản lý kết nối (Connection Management): Thiết lập và duy trì kết nối TCP ổn định giữ Client và Server. Hỗ trợ người chơi đăng nhập (định danh) và ngắt kết nối an toàn mà không làm treo hệ thống.

- Quản lý phòng chơi (Room Management): Cho phép người chơi tạo phòng mới hoặc tham gia vào các phòng chờ. Server cần quản lý danh sách người chơi trong từng phòng (tối đa 4 người) và trạng thái của phòng (đang chờ/đang chơi).

- Xử lý Logic game (Game Logic): Đây là chức năng quan trọng nhất do Server đảm nhiệm, bao gồm:

<!-- -->

- Tự động chia bài ngẫu nhiên và đảm bảo tính công bằng.

- Kiểm tra tính hợp lệ của từng nước đi (chặt heo, chặt đôi, tứ quý…) theo luật Tiến Lên Miền Nam.

- Điều phối lượt đánh (Turn-based) và phát hiện người thắng cuộc.

- Đồng bộ thời gian thực (Real-time Synchronization): Khi một người chơi thực hiện hành động (đánh bài, bỏ lượt), thông tin này phải được truyền ngya lập tức đến tất cả các người chơi khác trong cùng phòng để cập nhật giao diện.

- Tương tác hỗ trợ (Chat & Status): Hỗ trợ tính năng nhắn tin (Chat) trong phòng chơi và cập nhật trạng thái người chơi (Sẵn sàng/Bắt đầu) để tăng tính tương tác.

## Phân tích thiết kế 

###  Danh sách actor

1\. Người chơi:

Là người dùng cuối sử dụng Client App (JavaFX) Thực hiện các hành động: Đăng nhập, tham gia phòng, thao tác chơi bài và nhận kết quả

2\. Hệ thống Server :

Mặc dù là hệ thống, nhưng trong biểu đồ Use Case, Server đóng vai trò là tác nhân phản hồi và điều phối.

Thực hiện: Xác thực yêu cầu, chia bài, tính toán luật chơi, điều phối lượt và đồng bộ dữ liệu.

### Danh sách use case

Các chức năng chính của hệ thống được chia thành 2 nhóm:

- Nhóm Quản lý kết nối & Phòng:

<!-- -->

- Đăng nhập / Đăng ký.

- Tạo phòng chơi.

- Tham gia phòng.

- Rời phòng.

<!-- -->

- Nhóm Chơi game:

<!-- -->

- Sẵn sàng / Bắt đầu.

- Đánh bài.

- Bỏ lượt.

- Chat trong phòng.

###  Đặc tả use case

Chọn Use Case quan trọng nhất “Đánh bài” để đặc tả.

Tên User Case: Đánh bài

Mô tả: Người chơi chọn các lá bài hợp lệ từ tay mình và đánh ra bàn.

Tác nhân: Người chơi.

Điều kiện tiên quyết:

Người chơi đnag ở trong phòng và ván bài đang diễn ra.

Đến lượt đi của người chơi đó (Server xác nhận lượt).

Luồng sự kiện chính:

- Người chơi chọn các lá bài trên giao diện Client và nhấn nút “Đánh”.

- Client đóng gói thông tin (ID người chơi, danh sách bài) thành thông điệp JSON và gửi qua Socket đến Server.

- Server nhận thông điệp, kiểm tra tính hợp lệ (bài có trong tay không, có được chặn được bài trước đó không, đúng luật TLMN không).

- Nếu hợp lệ:

- Server cập nhật trạng thái ván bài (bài trên bàn mới, số bài còn lại của người chơi).

- Server chuyển lượt cho người kế tiếp.

- Server gửi thông điệp BROADCAST_MOVE đến tất cả người chơi trong phòng.

- Client của mọi người chơi cập nhật giao diện (hiển thị bài vừa đánh).

- Nếu không hợp lệ: Sererver gửi thông báo lỗi “Nước đi không hợp lệ” về tiêng cho Client đó.

### Biểu đồ hoạt động

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image6.png" style="width:6.16667in;height:3.40251in" alt="A diagram of a flowchart Description automatically generated" />

<span id="_Toc218677685" class="anchor"></span>Hình Biểu đồ hoạt động

### Biểu đồ tuần tự

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image7.png" style="width:6.16806in;height:3.36736in" alt="A diagram of a diagram Description automatically generated" />

<span id="_Toc218677686" class="anchor"></span>Hình Biểu đồ tuần tự

###  Biểu đồ cơ sở dữ liệu

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image8.png" style="width:6.3in;height:3.96042in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677687" class="anchor"></span>Hình Biểu đồ CSDL

## Ứng dụng mô hình/giao thức

Hai phần này được lồng ghép để mô tả luồng hoạt động của hệ thống, mô phỏng trên 2 máy (Client-Server).

Kiến trúc tổng thể:

- Máy A (Server): Chạy ứng dụng JavaFX.

  - Vai trò: Là trọng tài trung tâm. Nó nắm giữ trạng thái duy nhất và chính xác nhất của toàn bộ hệ thống (danh sách phòng, danh sách người chơi).

  - Công nghệ: Sử dụng java.net.ServerSocket để lắng nghe kết nối và một tạo các clienthandle để quản lý đồng thời hàng chục hàng trăm người chơi.

- Máy B (Client): Chạy JavaFX-Client.

<!-- -->

- Vai trò: Là "cổng giao tiếp" cho người chơi. Nó chỉ có nhiệm vụ hiển thị dữ liệu do Server gửi về và gửi hành động của người dùng lên Server. Client không bao giờ tự quyết định kết quả ván bài.

- Công nghệ: Sử dụng java.net.Socket để kết nối đến Server và JavaFX để vẽ giao diện.

Luồng hoạt động (Data Flow):

Bước 1: Thiết lập kết nối và Định danh (Handshake & Authentication)

1.  Client (Máy B): Khi người dùng mở game, Client khởi tạo một Socket TCP kết nối đến địa chỉ IP và cổng (Port) cố định của Máy A (ví dụ: 192.168.1.10:8080).

2.  Server (Máy A): ServerSocket chấp nhận kết nối. Server ngay lập tức tạo một luồng (Thread) riêng biệt (ví dụ: ClientHandler) để phục vụ duy nhất kết nối này.

3.  Định danh: Client gửi gói tin chứa thông tin đăng nhập (ví dụ: " LOGIN \| user=nhat&pass=8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92 "). Server xác thực với cơ sở dữ liệu và phản hồi kết quả kèm theo danh sách các phòng chờ hiện có. Nội dung gói tin đã được server và client thống nhất theo quy ước trong lớp utils.protocol

Bước 2: Vòng lặp Game và Đồng bộ trạng thái (The Game Loop & Synchronization)

1.  Hành động từ Client: Khi người chơi tại Máy B chọn bài và nhấn "Đánh". Client không tự trừ bài trên giao diện ngay. Nó đóng gói hành động thành thông điệp (ví dụ: {"PLAY\|\[3♠4♠5♠\]”}) và gửi qua Socket lên Server.

2.  Xử lý tại Server (Máy A):

    - Kiểm tra Logic (Game Rule): Server kiểm tra xem có đúng lượt Máy B không? Bài đánh ra có hợp lệ theo luật Tiến Lên không?

    - Cập nhật trạng thái: Nếu hợp lệ, Server cập nhật dữ liệu bộ nhớ (trừ bài của B, cập nhật bài trên bàn, chuyển lượt cho người kế tiếp).

3.  Phát sóng (Broadcast):

    - Server cần thông báo sự thay đổi này cho tất cả người chơi trong phòng (bao gồm cả Máy B và các máy C, D khác).

    - Server duyệt qua danh sách các Socket đang kết nối trong phòng đó và gửi gói tin cập nhật trạng thái mới nhất (ví dụ: PLAY \| nhat, \[6♠,6♣,6♥\]).

4.  Cập nhật giao diện: Client (Máy B) nhận được gói tin Broadcast từ Server. Lúc này, nó mới chính thức cập nhật giao diện JavaFX để phản ánh nước đi vừa thực hiện.

## Tổng kết chương 2

Chương 2 đã trình bày toàn diện phân tích và thiết kế kiến trúc của hệ thống Game bài Tiến Lên Miền Nam. Việc lựa chọn mô hình Client-Server tập trung kết hợp với giao thức truyền tải tin cậy TCP/IP được khẳng định là nền tảng vững chắc để đảm bảo tính công bằng của trò chơi và ngăn chặn các hành vi gian lận từ phía người dùng.

Thông qua hệ thống các biểu đồ UML (Use Case, Hoạt động, Tuần tự) và thiết kế cơ sở dữ liệu lai (kết hợp SQL bền vững và In-Memory tốc độ cao), chương này đã mô tả rõ ràng luồng dữ liệu và cơ chế tương tác thời gian thực. Cốt lõi của thiết kế nằm ở việc tập trung hóa toàn bộ logic luật chơi tại Server, sử dụng kỹ thuật lập trình đa luồng để phục vụ nhiều người chơi đồng thời, và cơ chế đồng bộ hóa (synchronization) để quản lý an toàn trạng thái phòng chơi trước khi phát sóng (broadcast) cập nhật đến các Client

# XÂY DỰNG HỆ THỐNG

##  Cấu trúc dự án 

### Tổng quan

Dự án Ứng dụng chơi bài Tiến Lên Miền Nam sử dụng TCP/IP và mô hình Client–Server được tổ chức theo hướng phân tầng rõ ràng, nhằm đảm bảo tính dễ mở rộng, dễ bảo trì và tái sử dụng mã nguồn. Cấu trúc thư mục của dự án như sau:

G:.

├── client

├── logic

├── model

├── server

└── utils

### Thư mục client

- ClientFX.java: Lớp chính xây dựng giao diện người dùng bằng JavaFX, quản lý scene và các thành phần giao diện.

- TienLenClientUI.java: Giao diện bàn chơi chính, hiển thị bài, lượt đánh và trạng thái ván chơi.

- TableSelectionUI.java: Giao diện cho phép người chơi chọn bàn chơi hoặc phòng chơi.

- AuthListener.java: Lắng nghe phản hồi xác thực (đăng nhập/đăng ký) từ server.

- ServerListener.java: Chạy trên một luồng riêng, liên tục nhận dữ liệu từ server và cập nhật giao diện client.

### Thư mục logic

Thư mục logic chứa các lớp xử lý luật chơi và nghiệp vụ chính của trò chơi.

- GameLogic.java

<!-- -->

- Cài đặt các luật chơi Tiến Lên Miền Nam như:

- Kiểm tra nước đi hợp lệ

- So sánh bộ bài

- Xác định lượt đi tiếp theo

- Kiểm tra điều kiện thắng

- Việc tách logic ra khỏi client và server giúp:

- Tránh trùng lặp mã nguồn

- Dễ bảo trì và mở rộng luật chơi

### Thư mục model

Thư mục model chứa các lớp mô hình dữ liệu, đại diện cho các thực thể trong trò chơi.

Các lớp chính:

- Card.java:Biểu diễn một quân bài (chất, giá trị).

- Deck.java:Quản lý bộ bài, xáo bài và chia bài cho người chơi.

- Player.java:Lưu thông tin người chơi, bài trên tay và trạng thái.

- Move.java:Biểu diễn một lượt đánh bài của người chơi.

- GameState.java:Lưu trạng thái hiện tại của ván chơi (lượt đánh, bài đã đánh, người chơi còn bài).

- Message.java:Đóng gói dữ liệu trao đổi giữa client và server.

### Thư mục server

Thư mục model chứa các lớp mô hình dữ liệu, đại diện cho các thực thể trong trò chơi.

Các lớp chính:

- Card.java:Biểu diễn một quân bài (chất, giá trị).

- Deck.java:Quản lý bộ bài, xáo bài và chia bài cho người chơi.

- Player.java:Lưu thông tin người chơi, bài trên tay và trạng thái.

- Move.java:Biểu diễn một lượt đánh bài của người chơi.

- GameState.java:Lưu trạng thái hiện tại của ván chơi (lượt đánh, bài đã đánh, người chơi còn bài).

- Message.java:Đóng gói dữ liệu trao đổi giữa client và server.

##  Giao diện ứng dụng

### Giao diện trang đăng nhập, đăng ký

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image9.png" style="width:5.18118in;height:4.37501in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677688" class="anchor"></span>Hình Giao diện trang đăng nhập đăng ký

Giao diện trang đăng nhập, đăng ký của người dùng. Người dùng cần nhập đầy đủ các thông tin cần thiết để tiến hành đăng nhập / đăng ký. Trường hợp đăng nhập không thành công, app sẽ hiện thông báo.

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image10.png" style="width:4.734in;height:4.19085in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677689" class="anchor"></span>Hình Giao diện đăng nhập không thành công

###  Giao diện trang chính ứng dụng

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image11.png" style="width:6.3in;height:4.61667in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677690" class="anchor"></span>Hình Giao diện trang chủ

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image12.png" style="width:6.3in;height:3.76042in" alt="A screenshot of a computer game Description automatically generated" />

<span id="_Toc218677691" class="anchor"></span>Hình Giao diện bàn chơi

### Giao diện server

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image13.png" style="width:6.3in;height:4.18264in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677692" class="anchor"></span>Hình Giao diện quản lý người dùng

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image14.png" style="width:6.3in;height:4.18264in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677693" class="anchor"></span>Hình Giao diện quản lý bàn chơi

<img src="/tmp/claude-0/-home-claude/165565a4-8518-5868-b513-c9289b704f23/scratchpad/media/media/image15.png" style="width:6.3in;height:4.18264in" alt="A screenshot of a computer Description automatically generated" />

<span id="_Toc218677694" class="anchor"></span>Hình Giao diện thống kê

## Kết luận chương 3:

Qua quá trình triển khai và thử nghiệm thực tế, hệ thống "Game đánh bài Tiến Lên Miền Nam" đã cho thấy những kết quả khả quan, khẳng định tính khả thi và hiệu quả của giải pháp kỹ thuật đã đề xuất. Hệ thống đáp ứng tốt các mục tiêu thiết kế đặt ra ban đầu cho cả hai thành phần Server và Client trong bối cảnh một trò chơi thời gian thực.

Đối với Hệ thống Máy chủ (Game Server): Kết quả thử nghiệm cho thấy nền tảng Server Socket (Java) hoạt động ổn định và tin cậy dưới các điều kiện tải khác nhau. Các chức năng cốt lõi như quản lý phiên người chơi (session), tạo và quản lý phòng game, chia bài ngẫu nhiên, xử lý logic luật chơi (rule engine), điều phối lượt đánh và đặc biệt là cơ chế phát sóng (broadcast) để đồng bộ trạng thái bàn chơi đến tất cả người tham gia đều vận hành chính xác, tuân thủ chặt chẽ mô hình kiến trúc Client-Server đã thiết kế.

Đối với Ứng dụng Người dùng (Game Client): Giao diện được phát triển trên nền tảng JavaFX đảm bảo tính trực quan và tương tác tốt. Các thao tác người dùng cơ bản như vào phòng, chọn bài, đánh bài, bỏ lượt và các hiệu ứng hình ảnh (hiển thị lá bài, thông báo kết quả) hoạt động mượt mà, mang lại trải nghiệm chơi game hấp dẫn và thuận tiện.

Nhìn chung, kết quả đánh giá chương này khẳng định hệ thống game hoạt động ổn định, đáp ứng được các yêu cầu khắt khe về tính thời gian thực và sự công bằng trong xử lý luật chơi. Kiến trúc dựa trên giao thức TCP/IP (thông qua Java Socket) đã chứng minh là một nền tảng kỹ thuật vững chắc để triển khai trò chơi trực tuyến nhiều người chơi. Tuy nhiên, để hệ thống trở nên toàn diện và sẵn sàng cho quy mô người dùng lớn hơn, các hướng nghiên cứu và phát triển tiếp theo cần tập trung vào việc tối ưu hóa hiệu suất xử lý của Server khi lượng người chơi đồng thời tăng cao, bổ sung cơ chế xử lý khi người chơi mất kết nối đột ngột, và tích hợp thêm các tính năng xã hội như chat trong phòng hay bảng xếp hạng.

<span id="_Toc148951262" class="anchor"></span>KẾT LUẬN

> <span id="_Toc148951263" class="anchor"></span>

1.Kết quả đạt được

Về mặt thực nghiệm, đồ án đã xây dựng thành công một hệ thống game bài Tiến Lên Miền Nam trực tuyến nhiều người chơi hoàn chỉnh, hoạt động ổn định theo mô hình Client-Server. Các kết quả cụ thể bao gồm:

- Xây dựng thành công Game Server (Java Core):

<!-- -->

- Server có khả năng lắng nghe và chấp nhận nhiều kết nối đồng thời từ các Client thông qua Java ServerSocket (TCP).

- Quản lý hiệu quả danh sách người chơi và trạng thái các phòng game.

- Tích hợp thành công bộ máy xử lý luật chơi (Rule Engine) của Tiến Lên Miền Nam, đảm bảo tính chính xác trong việc kiểm tra nước đi hợp lệ, chặt bài, và xác định người thắng cuộc.

<!-- -->

- Xây dựng hoàn thiện Game Client (JavaFX):

<!-- -->

- Giao diện người dùng (UI) trực quan, thân thiện, hiển thị rõ ràng trạng thái bàn chơi, các lá bài trên tay và lượt đi hiện tại.

- Xử lý mượt mà các tương tác người dùng (chọn bài, đánh bài, bỏ lượt) và phản hồi tức thời các sự kiện từ Server.

<!-- -->

- Thiết lập cơ chế giao tiếp thời gian thực hiệu quả:

<!-- -->

- Định nghĩa và triển khai thành công một giao thức ứng dụng tùy chỉnh (sử dụng định dạng JSON) để trao đổi dữ liệu giữa Client và Server (ví dụ: các gói tin LOGIN, ACTION_PLAY, GAME_UPDATE, GAME_OVER).

- Hoàn thiện cơ chế phát sóng (Broadcast), cho phép Server đồng bộ trạng thái bàn chơi đến tất cả người chơi trong phòng gần như tức thời.

<span id="_Toc148951264" class="anchor"></span>2.Hướng phát triển

- Cải thiện cơ chế xử lý sự cố mạng: Phát triển tính năng tự động phát hiện người chơi mất kết nối (Heartbeat mechanism) và cho phép họ kết nối lại vào đúng trạng thái ván đang chơi.

- Tối ưu hiệu năng Server: Nghiên cứu áp dụng Java NIO (Non-blocking I/O) để cải thiện khả năng chịu tải của Server thay vì dùng blocking I/O truyền thống.

- Bổ sung tính năng và cải thiện UI/UX: Thêm tính năng chat, biểu tượng cảm xúc trong trận đấu; nâng cấp giao diện đồ họa và thêm hiệu ứng âm thanh để tăng trải nghiệm người dùng.

- Triển khai thực tế: Đưa Server lên một môi trường máy chủ đám mây (Cloud VPS) để thử nghiệm trong môi trường mạng Internet thực tế.

# TÀI LIỆU THAM KHẢO

> **Website:**

1.  Stackoverflow.com

2.  Google.com

3.  Github.com

4.  W3schools.com

5.  colab.research.google.com

> **Reference books:**

\[1\].Computer Networking: A Top-Down Approach

James F. Kurose • Keith W. Ross

Seventh Edition, Pearson Education

\[2\].TCP/IP Illustrated, Volume 1: The Protocols

W. Richard Stevens

Addison-Wesley Professional

\[3\].Java: The Complete Reference

Herbert Schildt

Eleventh Edition, McGraw-Hill Education
