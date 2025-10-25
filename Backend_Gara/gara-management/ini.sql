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


-- Tạo cơ sở dữ liệu
CREATE DATABASE IF NOT EXISTS QuanLyGaraOto;
USE QuanLyGaraOto;

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

-- Thêm dữ liệu Khách Hàng
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
('Bùi Thanh Tú', '0990123456', 'buithanhtu@gmail.com', 'Đã xóa', '234 Hai Bà Trưng, Quận 3, TP.HCM', 'Cá nhân', 'Khách hàng cũ');

-- Thêm dữ liệu Xe
INSERT INTO Xe (BienSo, HangXe, DongXe, NamSanXuat, MauSac, TrangThai, MaKhachHang) VALUES
('51A-12345', 'Toyota', 'Vios', 2020, 'Trắng', 'Hoạt động', 1),
('51B-67890', 'Honda', 'City', 2019, 'Đen', 'Hoạt động', 2),
('51C-11111', 'Mazda', 'CX-5', 2021, 'Đỏ', 'Hoạt động', 3),
('51D-22222', 'Hyundai', 'Accent', 2018, 'Bạc', 'Hoạt động', 4),
('51E-33333', 'Toyota', 'Camry', 2022, 'Đen', 'Hoạt động', 5),
('51F-44444', 'Ford', 'Ranger', 2020, 'Xanh', 'Hoạt động', 6),
('51G-55555', 'Kia', 'Morning', 2017, 'Trắng', 'Hoạt động', 7),
('51H-66666', 'Vinfast', 'Lux A2.0', 2023, 'Xám', 'Hoạt động', 8),
('51K-77777', 'Honda', 'CR-V', 2021, 'Trắng ngọc trai', 'Hoạt động', 3),
('51L-88888', 'Mercedes', 'C-Class', 2022, 'Đen', 'Hoạt động', 5),
('51M-99999', 'BMW', '320i', 2021, 'Xanh dương', 'Hoạt động', 9),
('51N-00000', 'Audi', 'A4', 2020, 'Trắng', 'Đã xóa', 10);

-- Thêm dữ liệu Loại Dịch Vụ
INSERT INTO LoaiDichVu (TenLoai, TrangThai, NgayTao) VALUES
('Bảo dưỡng định kỳ', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa động cơ', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa hệ thống điện', 'Hoạt động', '2024-01-01 08:00:00'),
('Thay thế lốp xe', 'Hoạt động', '2024-01-01 08:00:00'),
('Sơn xe', 'Hoạt động', '2024-01-01 08:00:00'),
('Rửa xe & Chăm sóc ngoại thất', 'Hoạt động', '2024-01-01 08:00:00'),
('Sửa chữa gầm xe', 'Hoạt động', '2024-01-01 08:00:00');

-- Thêm dữ liệu Dịch Vụ
INSERT INTO DichVu (TenDichVu, MoTa, AnhDichVu, SoLuongTon, SoLuongBan, Gia, ThoiGianUocTinh, TrangThai, NgayTao, MaLoai) VALUES
('Thay dầu máy', 'Thay dầu động cơ toàn phần, bao gồm dầu và lọc dầu', NULL, 50, 120, 350000, 30, 'Còn hàng', '2024-01-01 08:30:00', 1),
('Bảo dưỡng 10000km', 'Bảo dưỡng định kỳ: thay dầu, lọc gió, kiểm tra toàn bộ', NULL, 30, 85, 800000, 90, 'Còn hàng', '2024-01-01 08:30:00', 1),
('Thay phanh trước', 'Thay má phanh trước, bao gồm công và phụ tùng', NULL, 40, 65, 1200000, 60, 'Còn hàng', '2024-01-01 08:30:00', 7),
('Thay bình ắc quy', 'Thay bình điện mới, bảo hành 12 tháng', NULL, 25, 48, 1500000, 20, 'Còn hàng', '2024-01-01 08:30:00', 3),
('Sửa máy lạnh', 'Kiểm tra, bơm ga, sửa chữa hệ thống điều hòa', NULL, 15, 32, 800000, 120, 'Còn hàng', '2024-01-01 08:30:00', 3),
('Thay lốp xe 1 bánh', 'Thay lốp mới, cân bằng, bao gồm công lắp đặt', NULL, 100, 210, 1800000, 30, 'Còn hàng', '2024-01-01 08:30:00', 4),
('Rửa xe ô tô', 'Rửa xe bên ngoài và bên trong, hút bụi nội thất', NULL, 0, 350, 100000, 45, 'Còn hàng', '2024-01-01 08:30:00', 6),
('Đánh bóng xe', 'Đánh bóng toàn bộ xe, phục hồi độ bóng sơn', NULL, 0, 95, 500000, 180, 'Còn hàng', '2024-01-01 08:30:00', 6),
('Sơn phần cản trước', 'Sơn lại cản trước xe, bao gồm sơn và công', NULL, 10, 28, 2500000, 480, 'Còn hàng', '2024-01-01 08:30:00', 5),
('Thay giảm xóc', 'Thay giảm xóc bộ 4, bao gồm phụ tùng và công', NULL, 20, 42, 3500000, 150, 'Còn hàng', '2024-01-01 08:30:00', 7),
('Cân bằng động', 'Cân bằng động 4 bánh xe', NULL, 0, 180, 200000, 30, 'Còn hàng', '2024-01-01 08:30:00', 4),
('Kiểm tra tổng quát', 'Kiểm tra toàn bộ xe trước khi đi xa', NULL, 0, 145, 300000, 60, 'Còn hàng', '2024-01-01 08:30:00', 1),
('Thay dây curoa', 'Thay dây curoa mới, kiểm tra căng dây', NULL, 35, 52, 450000, 40, 'Còn hàng', '2024-01-01 08:30:00', 2),
('Vệ sinh buồng đốt', 'Vệ sinh buồng đốt động cơ', NULL, 0, 68, 600000, 90, 'Còn hàng', '2024-01-01 08:30:00', 2),
('Phủ ceramic', 'Phủ ceramic bảo vệ sơn xe', NULL, 5, 15, 3000000, 300, 'Còn hàng', '2024-01-01 08:30:00', 6),
('Thay lọc gió động cơ', 'Thay lọc gió động cơ mới', NULL, 0, 92, 150000, 15, 'Hết hàng', '2024-01-01 08:30:00', 1);

-- Thêm dữ liệu Thợ
INSERT INTO Tho (TenTho, ChuyenMon, SoDienThoai, Email, TrangThai, KinhNghiem, NgayVaoLam) VALUES
('Nguyễn Văn Tài', 'Sửa chữa động cơ', '0981111111', 'nguyenvantai@gara.vn', 'Hoạt động', 10, '2015-03-15 08:00:00'),
('Trần Minh Tuấn', 'Sửa chữa điện', '0982222222', 'tranminhtuan@gara.vn', 'Hoạt động', 8, '2017-06-01 08:00:00'),
('Lê Văn Hoàng', 'Sơn xe', '0983333333', 'levanhoang@gara.vn', 'Hoạt động', 12, '2013-01-10 08:00:00'),
('Phạm Đức Anh', 'Bảo dưỡng tổng hợp', '0984444444', 'phamducanh@gara.vn', 'Hoạt động', 6, '2019-09-20 08:00:00'),
('Hoàng Văn Nam', 'Sửa chữa gầm xe', '0985555555', 'hoangvannam@gara.vn', 'Hoạt động', 9, '2016-11-05 08:00:00'),
('Vũ Quốc Thắng', 'Thay lốp, cân chỉnh', '0986666666', 'vuquocthang@gara.vn', 'Hoạt động', 5, '2020-02-14 08:00:00'),
('Đỗ Minh Quân', 'Chăm sóc ngoại thất', '0987777777', 'dominhquan@gara.vn', 'Hoạt động', 4, '2021-07-01 08:00:00'),
('Bùi Văn Hùng', 'Sửa chữa động cơ', '0988888888', 'buivanhung@gara.vn', 'Đã xóa', 7, '2018-05-10 08:00:00');

-- Thêm dữ liệu Phiếu Sửa Chữa
INSERT INTO PhieuSuaChua (MaXe, MaTho, NgayLap, MoTa, TrangThai, TongTien) VALUES
(1, 4, '2024-10-01 09:00:00', 'Bảo dưỡng định kỳ 10000km', 'Hoàn thành', 800000),
(2, 2, '2024-10-03 10:30:00', 'Sửa đèn pha không sáng, thay bình ắc quy', 'Hoàn thành', 1800000),
(3, 1, '2024-10-05 08:15:00', 'Kiểm tra tiếng kêu động cơ', 'Đang sửa', 300000),
(4, 6, '2024-10-06 14:00:00', 'Thay 2 lốp trước', 'Hoàn thành', 3600000),
(5, 5, '2024-10-07 11:20:00', 'Thay má phanh trước và sau', 'Đã giao', 2400000),
(6, 3, '2024-10-08 09:45:00', 'Sơn lại cản trước bị xước', 'Đang sửa', 2500000),
(7, 7, '2024-10-09 13:00:00', 'Rửa xe và đánh bóng', 'Hoàn thành', 600000),
(8, 4, '2024-10-10 08:30:00', 'Bảo dưỡng định kỳ, thay dầu', 'Chờ xử lý', 350000),
(9, 2, '2024-10-11 15:00:00', 'Sửa máy lạnh không mát', 'Đang sửa', 800000),
(10, 1, '2024-10-12 10:00:00', 'Kiểm tra tổng quát trước khi đi xa', 'Hoàn thành', 300000),
(11, 4, '2024-10-13 09:30:00', 'Bảo dưỡng định kỳ 20000km', 'Hoàn thành', 1250000),
(12, 3, '2024-10-14 14:20:00', 'Phủ ceramic toàn xe', 'Hoàn thành', 3000000),
(1, 7, '2024-10-15 10:00:00', 'Rửa xe định kỳ', 'Hủy sửa', 0);

-- Thêm dữ liệu Chi Tiết Phiếu Sửa Chữa
INSERT INTO ChiTietPhieuSuaChua (MaPhieu, MaDichVu, SoLuong, DonGia) VALUES
(1, 2, 1, 800000),
(2, 4, 1, 1500000),
(2, 12, 1, 300000),
(3, 12, 1, 300000),
(4, 6, 2, 1800000),
(5, 3, 2, 1200000),
(6, 9, 1, 2500000),
(7, 7, 1, 100000),
(7, 8, 1, 500000),
(8, 1, 1, 350000),
(9, 5, 1, 800000),
(10, 12, 1, 300000),
(11, 2, 1, 800000),
(11, 13, 1, 450000),
(12, 15, 1, 3000000);

-- Thêm dữ liệu Hóa Đơn
INSERT INTO HoaDon (MaPhieu, NgayLapHoaDon, ThoiGianThanhCong, KieuThanhToan, TrangThai, TongTien) VALUES
(1, '2024-10-01 16:00:00', '2024-10-01 16:05:00', 'Tiền mặt', 'Đã thanh toán', 800000),
(2, '2024-10-03 17:30:00', '2024-10-03 17:32:00', 'Chuyển khoản', 'Đã thanh toán', 1800000),
(4, '2024-10-06 18:00:00', '2024-10-06 18:03:00', 'Thẻ', 'Đã thanh toán', 3600000),
(5, '2024-10-07 16:45:00', '2024-10-07 16:50:00', 'Chuyển khoản', 'Đã thanh toán', 2400000),
(7, '2024-10-09 15:30:00', '2024-10-09 15:35:00', 'Tiền mặt', 'Đã thanh toán', 600000),
(10, '2024-10-12 17:00:00', '2024-10-12 17:02:00', 'Tiền mặt', 'Đã thanh toán', 300000),
(6, '2024-10-13 09:00:00', NULL, 'Chuyển khoản', 'Chưa thanh toán', 2500000),
(8, '2024-10-13 10:30:00', NULL, 'Tiền mặt', 'Chưa thanh toán', 350000),
(11, '2024-10-13 17:00:00', '2024-10-13 17:10:00', 'Chuyển khoản', 'Đã thanh toán', 1250000),
(12, '2024-10-14 18:00:00', '2024-10-14 18:05:00', 'Thẻ', 'Đã thanh toán', 3000000);

-- Thêm dữ liệu Tài Khoản
INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThai, Email, NgayTao) VALUES
('admin', 'admin123', 'Quản lý', 'Hoạt động', 'admin@gara.vn', '2024-01-01 08:00:00'),
('nhanvien01', 'nv123456', 'Nhân viên', 'Hoạt động', 'nhanvien01@gara.vn', '2024-01-15 08:00:00');

-- Trigger tự động cập nhật trạng thái dịch vụ
DELIMITER $$

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
END$$

DELIMITER ;
