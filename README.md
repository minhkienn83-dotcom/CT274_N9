# 🎬 Movie Manager App - CT274_N9

Ứng dụng quản lý danh sách phim cá nhân chuyên nghiệp được xây dựng bằng **Jetpack Compose** và **Room Database**. Dự án tuân thủ chặt chẽ kiến trúc **MVVM** và các tiêu chuẩn phát triển Android hiện đại nhất 2024-2025.

---

## ✨ Tính năng nổi bật

### 🚀 Quản lý Phim Thông minh
*   **Gợi ý phim tự động:** Tích hợp **TMDB API**, tự động gợi ý tên phim, năm sản xuất và poster chất lượng cao ngay khi người dùng nhập liệu.
*   **Lưu trữ mốc thời gian (Timestamp):** Ghi nhớ chính xác thời gian bạn đang xem dở (Ví dụ: 01:25:30) để xem tiếp sau này.
*   **Chỉnh sửa linh hoạt:** Cho phép cập nhật ghi chú, đánh giá sao và mốc thời gian trực tiếp trong màn hình chi tiết.
*   **Đa phương tiện:** Hỗ trợ lấy ảnh từ URL hoặc chọn ảnh trực tiếp từ **thư viện điện thoại** (đã xử lý copy vào bộ nhớ cục bộ).

### 📊 Thống kê & Tìm kiếm
*   **Tìm kiếm thời gian thực:** Lọc phim nhanh chóng theo tên ngay trên màn hình chính.
*   **Sắp xếp đa dạng:** Sắp xếp theo ngày nhập (Mới/Cũ), Điểm đánh giá (Cao/Thấp), hoặc Tên (A-Z/Z-A) qua giao diện **Bottom Sheet** hiện đại.
*   **Biểu đồ Thống kê:** Theo dõi tổng số phim đã xem và phân bổ đánh giá sao qua biểu đồ trực quan.

### 🎨 Cá nhân hóa Giao diện
*   **Chế độ Tối (Dark Mode):** Bảo vệ mắt và tiết kiệm pin.
*   **Đổi màu chủ đạo (Primary Color):** Cho phép người dùng tự chọn tông màu yêu thích cho ứng dụng.
*   **Chế độ xem linh hoạt:** Chuyển đổi giữa dạng **Danh sách (List)** truyền thống và dạng **Lưới (Grid 2 cột)** hiện đại.

---

## 🛠 Công nghệ sử dụng (Tech Stack)

*   **Ngôn ngữ:** [Kotlin 2.2.10](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose (Material 3)](https://developer.android.com/compose)
*   **Cơ sở dữ liệu:** [Room Database 2.8.4](https://developer.android.com/training/data-storage/room)
*   **Kiến trúc:** MVVM + Repository Pattern
*   **Kết nối mạng:** [Ktor Client](https://ktor.io/) (Gọi TMDB API)
*   **Tải hình ảnh:** [Coil 3](https://coil-kt.github.io/coil/)
*   **Navigation:** Type-safe Navigation Compose
*   **Xử lý dữ liệu JSON:** Kotlinx Serialization

---

## 🏗 Cấu trúc dự án

```text
app/src/main/java/com/example/movie/
├── data/              # Lớp dữ liệu (Entities, DAO, Database, Repository, API Service)
├── ui/                # Lớp giao diện
│   ├── screens/       # Các màn hình độc lập (List, Detail, Add, Settings, Stats)
│   ├── theme/         # Cấu hình màu sắc, kiểu chữ và Theme hệ thống
│   └── MovieViewModel # Điều phối logic và trạng thái giao diện
└── MainActivity       # Điểm bắt đầu và cấu hình Navigation
```

---

## 📸 Ảnh chụp màn hình

*(Em có thể thêm ảnh vào thư mục screenshots và chèn link vào đây)*
| Màn hình chính | Thêm phim mới | Thống kê |
| :---: | :---: | :---: |
| ![Main](https://via.placeholder.com/200x400) | ![Add](https://via.placeholder.com/200x400) | ![Stats](https://via.placeholder.com/200x400) |

---

## 👨‍💻 Thành viên thực hiện
*   **Nhóm:** CT274_N9
*   **Dự án:** Lập trình thiết bị di động

---
*Dự án này được xây dựng với mục đích học tập và nghiên cứu các công nghệ Android mới nhất.*
