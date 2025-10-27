DROP DATABASE IF EXISTS QuanLyGaraOto;

-- Tạo cơ sở dữ liệu
CREATE DATABASE IF NOT EXISTS QuanLyGaraOto
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
USE QuanLyGaraOto;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'admin123@'@'%' IDENTIFIED BY 'admin123@';
GRANT ALL PRIVILEGES ON QuanLyGaraOto.* TO 'admin123@'@'%';
FLUSH PRIVILEGES;

-- Bảng Khách Hàng
CREATE TABLE KhachHang (
    MaKhachHang INT PRIMARY KEY AUTO_INCREMENT,
    TenKhachHang NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL UNIQUE,
    Email VARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(50) DEFAULT 'Hoạt động',
    DiaChi NVARCHAR(200) NOT NULL,
    LoaiKhach NVARCHAR(50) NOT NULL,
    GhiChu NVARCHAR(200) NULL
);

-- Bảng Xe
CREATE TABLE Xe (
    MaXe INT PRIMARY KEY AUTO_INCREMENT,
    BienSo VARCHAR(15) NOT NULL UNIQUE,
    HangXe NVARCHAR(50) NOT NULL,
    DongXe VARCHAR(15) NOT NULL,
    NamSanXuat INT NOT NULL CHECK (NamSanXuat >= 2000),
    MauSac NVARCHAR(15) NOT NULL,
    TrangThai NVARCHAR(50) DEFAULT 'Hoạt động',
    MaKhachHang INT,
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang)
);

-- Bảng Loại Dịch Vụ
CREATE TABLE LoaiDichVu (
    MaLoai INT PRIMARY KEY AUTO_INCREMENT,
    TenLoai NVARCHAR(100) NOT NULL UNIQUE,
    TrangThai NVARCHAR(50) DEFAULT 'Hoạt động',
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Bảng Dịch Vụ
CREATE TABLE DichVu (
    MaDichVu INT PRIMARY KEY AUTO_INCREMENT,
    TenDichVu NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255) NULL,
    AnhDichVu TEXT NULL,
    SoLuongTon INT NOT NULL CHECK (SoLuongTon >= 0),
    SoLuongBan INT NOT NULL CHECK (SoLuongBan >= 0),
    Gia DECIMAL(18,2) NOT NULL CHECK (Gia >= 0),
    ThoiGianUocTinh INT CHECK (ThoiGianUocTinh > 0),
    TrangThai NVARCHAR(50) DEFAULT 'Còn hàng',
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    MaLoai INT,
    FOREIGN KEY (MaLoai) REFERENCES LoaiDichVu(MaLoai)
);

-- Bảng Thợ
CREATE TABLE Tho (
    MaTho INT PRIMARY KEY AUTO_INCREMENT,
    TenTho NVARCHAR(100) NOT NULL,
    ChuyenMon NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL UNIQUE,
    Email VARCHAR(100) NOT NULL UNIQUE,
    TrangThai NVARCHAR(50) DEFAULT 'Hoạt động',
    KinhNghiem INT CHECK (KinhNghiem >= 0),
    NgayVaoLam DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Phiếu Sửa Chữa
CREATE TABLE PhieuSuaChua (
    MaPhieu INT PRIMARY KEY AUTO_INCREMENT,
    MaXe INT,
    MaTho INT,
    NgayLap DATETIME DEFAULT CURRENT_TIMESTAMP,
    MoTa NVARCHAR(255) NULL,
    TrangThai NVARCHAR(50) DEFAULT 'Chờ xử lý',
    TongTien DECIMAL(18,2) CHECK (TongTien >= 0),
    FOREIGN KEY (MaXe) REFERENCES Xe(MaXe),
    FOREIGN KEY (MaTho) REFERENCES Tho(MaTho)
);

-- Bảng Chi Tiết Phiếu Sửa Chữa
CREATE TABLE ChiTietPhieuSuaChua (
    MaPhieu INT,
    MaDichVu INT,
    SoLuong INT DEFAULT 1 CHECK (SoLuong >= 1),
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) AS (SoLuong * DonGia) STORED,
    PRIMARY KEY (MaPhieu, MaDichVu),
    FOREIGN KEY (MaPhieu) REFERENCES PhieuSuaChua(MaPhieu),
    FOREIGN KEY (MaDichVu) REFERENCES DichVu(MaDichVu)
);

-- Bảng Hóa Đơn
CREATE TABLE HoaDon (
    MaHoaDon INT PRIMARY KEY AUTO_INCREMENT,
    MaPhieu INT,
    NgayLapHoaDon DATETIME DEFAULT CURRENT_TIMESTAMP,
    ThoiGianThanhCong DATETIME NULL,
    KieuThanhToan NVARCHAR(50) NOT NULL DEFAULT 'Tiền mặt',
    TrangThai NVARCHAR(50) DEFAULT 'Chưa thanh toán',
    TongTien DECIMAL(18,2) CHECK (TongTien >= 0),
    FOREIGN KEY (MaPhieu) REFERENCES PhieuSuaChua(MaPhieu)
);

-- Bảng Tài Khoản
CREATE TABLE TaiKhoan (
    MaTaiKhoan INT PRIMARY KEY AUTO_INCREMENT,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    VaiTro NVARCHAR(50) NOT NULL DEFAULT 'Quản lý',
    TrangThai NVARCHAR(50) DEFAULT 'Hoạt động',
    Email VARCHAR(50) NOT NULL,
    NgayTao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- THÊM DỮ LIỆU MẪU
-- ============================================

-- Thêm 11 Khách Hàng
INSERT INTO KhachHang (TenKhachHang, SoDienThoai, Email, TrangThai, DiaChi, LoaiKhach, GhiChu) VALUES
('Nguyễn Văn An', '0901234567', 'nguyenvanan@gmail.com', 'Hoạt động', '123 Lê Lợi, Quận 1, TP.HCM', 'Cá nhân', 'Khách hàng thân thiết'),
('Trần Thị Bình', '0912345678', 'tranthibinh@gmail.com', 'Hoạt động', '45 Nguyễn Huệ, Quận 1, TP.HCM', 'Cá nhân', NULL),
('Công ty TNHH ABC', '0923456789', 'contact@abc.com.vn', 'Hoạt động', '789 Điện Biên Phủ, Quận 3, TP.HCM', 'Doanh nghiệp', 'Hợp đồng bảo dưỡng định kỳ'),
('Lê Minh Cường', '0934567890', 'leminhcuong@yahoo.com', 'Hoạt động', '56 Trần Hưng Đạo, Quận 5, TP.HCM', 'Cá nhân', NULL),
('Phạm Thu Hà', '0945678901', 'phamthuha@gmail.com', 'Hoạt động', '12 Lý Thường Kiệt, Quận 10, TP.HCM', 'Cá nhân', 'Khách VIP'),
('Công ty CP XYZ', '0956789012', 'info@xyz.vn', 'Hoạt động', '234 Võ Văn Tần, Quận 3, TP.HCM', 'Doanh nghiệp', NULL),
('Hoàng Văn Đức', '0967890123', 'hoangvanduc@gmail.com', 'Hoạt động', '78 Cách Mạng Tháng 8, Quận Tân Bình, TP.HCM', 'Cá nhân', NULL),
('Vũ Thị Mai', '0978901234', 'vuthimai@gmail.com', 'Hoạt động', '90 Phan Xích Long, Quận Phú Nhuận, TP.HCM', 'Cá nhân', 'Giới thiệu từ KH001'),
('Đặng Hoàng Nam', '0989012345', 'danghoangnam@gmail.com', 'Hoạt động', '156 Phan Đăng Lưu, Quận Phú Nhuận, TP.HCM', 'Cá nhân', NULL),
('Bùi Thanh Tú', '0990123456', 'buithanhtu@gmail.com', 'Hoạt động', '234 Hai Bà Trưng, Quận 3, TP.HCM', 'Cá nhân', NULL),
('Ngô Văn Phong', '0992345678', 'ngovanphong@gmail.com', 'Hoạt động', '890 Lê Văn Sỹ, Quận Tân Bình, TP.HCM', 'Cá nhân', 'Khách hàng mới');

-- Thêm 11 Xe với đủ các loại biển số
INSERT INTO Xe (BienSo, HangXe, DongXe, NamSanXuat, MauSac, TrangThai, MaKhachHang) VALUES
('29A-12345', 'Toyota', 'Vios', 2020, 'Trắng', 'Hoạt động', 1),
('30B-67890', 'Honda', 'City', 2019, 'Đen', 'Hoạt động', 2),
('51C-11111', 'Mazda', 'CX-5', 2021, 'Đỏ', 'Hoạt động', 3),
('59D-22222', 'Hyundai', 'Accent', 2018, 'Bạc', 'Hoạt động', 4),
('43E-33333', 'Toyota', 'Camry', 2022, 'Đen', 'Hoạt động', 5),
('17F-44444', 'Ford', 'Ranger', 2020, 'Xanh', 'Hoạt động', 6),
('92G-55555', 'Kia', 'Morning', 2017, 'Trắng', 'Hoạt động', 7),
('37H-66666', 'Vinfast', 'Lux A2.0', 2023, 'Xám', 'Hoạt động', 8),
('51K-77777', 'Honda', 'CR-V', 2021, 'Trắng ngọc trai', 'Hoạt động', 9),
('72L-88888', 'Mercedes', 'C-Class', 2022, 'Đen', 'Hoạt động', 10),
('88M-99999', 'BMW', '320i', 2021, 'Xanh dương', 'Hoạt động', 11);

-- Thêm 11 Loại Dịch Vụ
INSERT INTO LoaiDichVu (TenLoai, TrangThai, NgayTao) VALUES
('Bảo dưỡng định kỳ', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa động cơ', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa hệ thống điện', 'Hoạt động', '2024-01-01 08:00:00'),
('Thay thế lốp xe', 'Hoạt động', '2024-01-01 08:00:00'),
('Sơn xe', 'Hoạt động', '2024-01-01 08:00:00'),
('Rửa xe & Chăm sóc ngoại thất', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa gầm xe', 'Hoạt động', '2024-01-01 08:00:00'),
('Thay dầu động cơ', 'Hoạt động', '2024-01-01 08:00:00'),
('Kiểm tra phanh', 'Hoạt động', '2024-01-01 08:00:00'),
('Bảo trì hệ thống làm mát', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa hộp số', 'Hoạt động', '2024-01-01 08:00:00');

-- Thêm 18 Dịch Vụ (phân bổ đều vào 11 loại)
-- 3 dịch vụ sắp hết (SoLuongTon < 3), 2 dịch vụ hết hàng (SoLuongTon = 0)
INSERT INTO DichVu (TenDichVu, MoTa, AnhDichVu, SoLuongTon, SoLuongBan, Gia, ThoiGianUocTinh, TrangThai, NgayTao, MaLoai) VALUES
('Thay dầu máy', 'Thay dầu động cơ toàn phần, bao gồm dầu và lọc dầu', 'a1.jpg', 50, 120, 350000, 30, 'Còn hàng', '2024-01-01 08:30:00', 8),
('Bảo dưỡng 10000km', 'Bảo dưỡng định kỳ: thay dầu, lọc gió, kiểm tra toàn bộ', 'a2.jpg', 30, 85, 800000, 90, 'Còn hàng', '2024-01-01 08:30:00', 1),
('Thay phanh trước', 'Thay má phanh trước, bao gồm công và phụ tùng', 'a3.jpg', 2, 65, 1200000, 60, 'Sắp hết', '2024-01-01 08:30:00', 9),
('Thay bình ắc quy', 'Thay bình điện mới, bảo hành 12 tháng', 'a4.jpg', 0, 48, 1500000, 20, 'Hết hàng', '2024-01-01 08:30:00', 3),
('Sửa máy lạnh', 'Kiểm tra, bơm ga, sửa chữa hệ thống điều hòa', 'a5.jpg', 15, 32, 800000, 120, 'Còn hàng', '2024-01-01 08:30:00', 10),
('Thay lốp xe 1 bánh', 'Thay lốp mới, cân bằng, bao gồm công lắp đặt', 'a6.jpg', 100, 210, 1800000, 30, 'Còn hàng', '2024-01-01 08:30:00', 4),
('Rửa xe ô tô', 'Rửa xe bên ngoài và bên trong, hút bụi nội thất', 'a7.jpg', 45, 350, 100000, 45, 'Còn hàng', '2024-01-01 08:30:00', 6),
('Đánh bóng xe', 'Đánh bóng toàn bộ xe, phục hồi độ bóng sơn', 'a8.jpg', 1, 95, 500000, 180, 'Sắp hết', '2024-01-01 08:30:00', 6),
('Sơn phần cản trước', 'Sơn lại cản trước xe, bao gồm sơn và công', 'a9.jpg', 10, 28, 2500000, 480, 'Còn hàng', '2024-01-01 08:30:00', 5),
('Thay giảm xóc', 'Thay giảm xóc bộ 4, bao gồm phụ tùng và công', 'a10.jpg', 20, 42, 3500000, 150, 'Còn hàng', '2024-01-01 08:30:00', 7),
('Cân bằng động', 'Cân bằng động 4 bánh xe', 'a20.jpg', 0, 180, 200000, 30, 'Hết hàng', '2024-01-01 08:30:00', 4),
('Kiểm tra tổng quát', 'Kiểm tra toàn bộ xe trước khi đi xa', 'a12.jpg', 25, 145, 300000, 60, 'Còn hàng', '2024-01-01 08:30:00', 1),
('Thay dây curoa', 'Thay dây curoa mới, kiểm tra căng dây', 'a13.jpg', 35, 52, 450000, 40, 'Còn hàng', '2024-01-01 08:30:00', 2),
('Vệ sinh buồng đốt', 'Vệ sinh buồng đốt động cơ', 'a14.jpg', 18, 68, 600000, 90, 'Còn hàng', '2024-01-01 08:30:00', 2),
('Phủ ceramic', 'Phủ ceramic bảo vệ sơn xe', 'a15.jpg', 5, 15, 3000000, 300, 'Còn hàng', '2024-01-01 08:30:00', 6),
('Thay lọc gió động cơ', 'Thay lọc gió động cơ mới', 'a16.jpg', 2, 92, 150000, 15, 'Sắp hết', '2024-01-01 08:30:00', 1),
('Vệ sinh kim phun', 'Vệ sinh kim phun nhiên liệu', 'a17.jpg', 12, 38, 700000, 75, 'Còn hàng', '2024-01-01 08:30:00', 2),
('Thay dầu hộp số', 'Thay dầu hộp số tự động/số sàn', 'a18.jpg', 28, 56, 950000, 50, 'Còn hàng', '2024-01-01 08:30:00', 11);

-- Thêm 11 Thợ
INSERT INTO Tho (TenTho, ChuyenMon, SoDienThoai, Email, TrangThai, KinhNghiem, NgayVaoLam) VALUES
('Nguyễn Văn Tài', 'Sửa chữa động cơ', '0981111111', 'nguyenvantai@gara.vn', 'Hoạt động', 10, '2015-03-15 08:00:00'),
('Trần Minh Tuấn', 'Sửa chữa điện', '0982222222', 'tranminhtuan@gara.vn', 'Hoạt động', 8, '2017-06-01 08:00:00'),
('Lê Văn Hoàng', 'Sơn xe', '0983333333', 'levanhoang@gara.vn', 'Hoạt động', 12, '2013-01-10 08:00:00'),
('Phạm Đức Anh', 'Bảo dưỡng tổng hợp', '0984444444', 'phamducanh@gara.vn', 'Hoạt động', 6, '2019-09-20 08:00:00'),
('Hoàng Văn Nam', 'Sửa chữa gầm xe', '0985555555', 'hoangvannam@gara.vn', 'Hoạt động', 9, '2016-11-05 08:00:00'),
('Vũ Quốc Thắng', 'Thay lốp, cân chỉnh', '0986666666', 'vuquocthang@gara.vn', 'Hoạt động', 5, '2020-02-14 08:00:00'),
('Đỗ Minh Quân', 'Chăm sóc ngoại thất', '0987777777', 'dominhquan@gara.vn', 'Hoạt động', 4, '2021-07-01 08:00:00'),
('Bùi Văn Hùng', 'Sửa chữa động cơ', '0988888888', 'buivanhung@gara.vn', 'Hoạt động', 7, '2018-05-10 08:00:00'),
('Trần Quang Huy', 'Sửa chữa hộp số', '0989999999', 'tranquanghuy@gara.vn', 'Hoạt động', 11, '2014-08-20 08:00:00'),
('Lý Văn Sơn', 'Điện và điện tử', '0980000000', 'lyvanson@gara.vn', 'Hoạt động', 6, '2019-03-12 08:00:00'),
('Phùng Minh Đức', 'Bảo dưỡng định kỳ', '0981234567', 'phungminhduc@gara.vn', 'Hoạt động', 5, '2020-05-18 08:00:00');

-- Thêm 18 Phiếu Sửa Chữa (phân bổ đều trong năm 2025 đến hiện tại 27/10/2025)
-- 10 phiếu Đã giao, 2 phiếu Hoàn thành, 2 phiếu Đang sửa, 4 phiếu Chờ xử lý
INSERT INTO PhieuSuaChua (MaXe, MaTho, NgayLap, MoTa, TrangThai, TongTien) VALUES
(1, 4, '2025-01-15 09:00:00', 'Bảo dưỡng định kỳ 10000km', 'Đã giao', 800000),
(2, 2, '2025-02-10 10:30:00', 'Thay bình ắc quy', 'Đã giao', 1500000),
(3, 1, '2025-02-28 08:15:00', 'Vệ sinh buồng đốt động cơ', 'Đã giao', 900000),
(4, 6, '2025-03-18 14:00:00', 'Thay 2 lốp trước', 'Đã giao', 3600000),
(5, 5, '2025-04-05 11:20:00', 'Thay má phanh trước', 'Đã giao', 1200000),
(6, 3, '2025-04-25 09:45:00', 'Sơn lại cản trước bị xước', 'Đã giao', 2500000),
(7, 7, '2025-05-12 13:00:00', 'Rửa xe và đánh bóng', 'Đã giao', 600000),
(8, 4, '2025-06-02 08:30:00', 'Thay dầu máy', 'Đã giao', 350000),
(9, 2, '2025-06-20 15:00:00', 'Sửa máy lạnh không mát', 'Đã giao', 800000),
(10, 1, '2025-07-08 10:00:00', 'Kiểm tra tổng quát trước khi đi xa', 'Đã giao', 300000),
(11, 9, '2025-07-28 09:30:00', 'Thay dầu hộp số', 'Hoàn thành', 950000),
(1, 7, '2025-08-15 10:00:00', 'Rửa xe định kỳ', 'Hoàn thành', 100000),
(5, 8, '2025-09-05 11:30:00', 'Vệ sinh kim phun, thay lọc gió', 'Đang sửa', 850000),
(6, 11, '2025-09-22 08:45:00', 'Bảo dưỡng 20000km', 'Đang sửa', 1250000),
(8, 10, '2025-10-03 14:15:00', 'Kiểm tra hệ thống điện', 'Chờ xử lý', 500000),
(9, 6, '2025-10-12 09:20:00', 'Cân bằng động, kiểm tra lốp', 'Chờ xử lý', 200000),
(10, 4, '2025-10-20 10:30:00', 'Thay dầu máy, lọc gió cabin', 'Chờ xử lý', 550000),
(11, 5, '2025-10-25 15:00:00', 'Thay giảm xóc', 'Chờ xử lý', 3500000);

-- Thêm Chi Tiết Phiếu Sửa Chữa (18 phiếu)
INSERT INTO ChiTietPhieuSuaChua (MaPhieu, MaDichVu, SoLuong, DonGia) VALUES
(1, 2, 1, 800000),
(2, 4, 1, 1500000),
(3, 14, 1, 600000),
(3, 12, 1, 300000),
(4, 6, 2, 1800000),
(5, 3, 1, 1200000),
(6, 9, 1, 2500000),
(7, 7, 1, 100000),
(7, 8, 1, 500000),
(8, 1, 1, 350000),
(9, 5, 1, 800000),
(10, 12, 1, 300000),
(11, 18, 1, 950000),
(12, 7, 1, 100000),
(13, 17, 1, 700000),
(13, 16, 1, 150000),
(14, 2, 1, 800000),
(14, 13, 1, 450000),
(15, 12, 1, 500000),
(16, 11, 1, 200000),
(17, 1, 1, 350000),
(17, 16, 1, 200000),
(18, 10, 1, 3500000);

-- Thêm 18 Hóa Đơn (10 đã thanh toán cho phiếu đã giao, 8 chưa thanh toán cho các phiếu còn lại)
INSERT INTO HoaDon (MaPhieu, NgayLapHoaDon, ThoiGianThanhCong, KieuThanhToan, TrangThai, TongTien) VALUES
(1, '2025-01-15 16:00:00', '2025-01-15 16:05:00', 'Tiền mặt', 'Đã thanh toán', 800000),
(2, '2025-02-10 17:30:00', '2025-02-10 17:32:00', 'Chuyển khoản', 'Đã thanh toán', 1500000),
(3, '2025-02-28 15:20:00', '2025-02-28 15:25:00', 'Tiền mặt', 'Đã thanh toán', 900000),
(4, '2025-03-18 18:00:00', '2025-03-18 18:03:00', 'Thẻ', 'Đã thanh toán', 3600000),
(5, '2025-04-05 16:45:00', '2025-04-05 16:50:00', 'Chuyển khoản', 'Đã thanh toán', 1200000),
(6, '2025-04-25 17:30:00', '2025-04-25 17:35:00', 'Tiền mặt', 'Đã thanh toán', 2500000),
(7, '2025-05-12 15:30:00', '2025-05-12 15:35:00', 'Tiền mặt', 'Đã thanh toán', 600000),
(8, '2025-06-02 14:00:00', '2025-06-02 14:05:00', 'Chuyển khoản', 'Đã thanh toán', 350000),
(9, '2025-06-20 18:00:00', '2025-06-20 18:10:00', 'Thẻ', 'Đã thanh toán', 800000),
(10, '2025-07-08 17:00:00', '2025-07-08 17:02:00', 'Tiền mặt', 'Đã thanh toán', 300000),
(11, '2025-07-28 16:30:00', NULL, 'Chuyển khoản', 'Chưa thanh toán', 950000),
(12, '2025-08-15 12:30:00', NULL, 'Tiền mặt', 'Chưa thanh toán', 100000),
(13, '2025-09-05 11:30:00', NULL, 'Chuyển khoản', 'Chưa thanh toán', 850000),
(14, '2025-09-22 08:45:00', NULL, 'Thẻ', 'Chưa thanh toán', 1250000),
(15, '2025-10-03 14:15:00', NULL, 'Tiền mặt', 'Chưa thanh toán', 500000),
(16, '2025-10-12 09:20:00', NULL, 'Chuyển khoản', 'Chưa thanh toán', 200000),
(17, '2025-10-20 10:30:00', NULL, 'Tiền mặt', 'Chưa thanh toán', 550000),
(18, '2025-10-25 15:00:00', NULL, 'Chuyển khoản', 'Chưa thanh toán', 3500000);

-- Thêm 2 Tài Khoản (1 Quản lý, 1 Nhân viên)
INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThai, Email, NgayTao) VALUES
('admin123', '$2a$12$k6tA8Fr8wFwNL3uUTGyUwO4CJmT/W3MMwRs01ZGh.uCc.CpUco8hi', 'Quản lý', 'Hoạt động', 'admin@gara.vn', '2024-01-01 08:00:00'),
('nhanvien123', '$2a$12$k6tA8Fr8wFwNL3uUTGyUwO4CJmT/W3MMwRs01ZGh.uCc.CpUco8hi', 'Nhân viên', 'Hoạt động', 'nhanvien01@gara.vn', '2024-01-15 08:00:00');

-- Trigger tự động cập nhật trạng thái dịch vụ
DELIMITER $

CREATE TRIGGER trg_cap_nhat_trang_thai_dich_vu
BEFORE UPDATE ON DichVu
FOR EACH ROW
BEGIN
    -- Kiểm tra xem SoLuongTon có thay đổi hay không
    IF NEW.SoLuongTon <> OLD.SoLuongTon THEN
        IF NEW.SoLuongTon = 0 THEN
            SET NEW.TrangThai = 'Hết hàng';
        ELSEIF NEW.SoLuongTon <= 3 THEN
            SET NEW.TrangThai = 'Sắp hết';
        ELSE
            SET NEW.TrangThai = 'Còn hàng';
        END IF;
    END IF;
END$

DELIMITER ;