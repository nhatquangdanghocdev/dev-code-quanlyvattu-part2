package dao;

import Model.VatTu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_VATTU {

    // Thông tin kết nối SQL Server
    private static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;databaseName=QLVT;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "1";

    static {
        try {
            // Đăng ký driver SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không thể tải driver SQL Server", e);
        }
    }

    // Phương thức tạo kết nối
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // DAO_VATTU.java: Các phương thức thêm, xóa, sửa đã chỉnh sửa.
    public static void insertVatTu(VatTu vt) {
        String sqlCheckMaKho = "SELECT COUNT(*) FROM Kho WHERE MaKho = ?";
        String sqlInsert = "INSERT INTO VatTu (MaVatTu, TenVatTu, DonViTinh, SoLuong, MaKho) VALUES (?, ?, ?, ?, ?)";
        String sqlGetMaxMaVT = "SELECT MaVatTu FROM VatTu";

        try (Connection conn = getConnection(); PreparedStatement psCheckMaKho = conn.prepareStatement(sqlCheckMaKho); PreparedStatement psInsert = conn.prepareStatement(sqlInsert); PreparedStatement psGetMax = conn.prepareStatement(sqlGetMaxMaVT)) {

            // Kiểm tra mã kho tồn tại
            psCheckMaKho.setString(1, vt.getMaKho());
            ResultSet rsCheck = psCheckMaKho.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                throw new SQLException("Mã kho không tồn tại!");
            }

            // Lấy mã vật tư lớn nhất để sinh mã mới
            ResultSet rs = psGetMax.executeQuery();
            int maxNumber = 0;
            while (rs.next()) {
                String currentMaVT = rs.getString("MaVatTu");
                if (currentMaVT != null && currentMaVT.startsWith("VT")) {
                    int number = Integer.parseInt(currentMaVT.substring(2));
                    maxNumber = Math.max(maxNumber, number);
                }
            }
            String newMaVT = "VT" + (maxNumber + 1);

            // Thực hiện thêm
            psInsert.setString(1, newMaVT);
            psInsert.setString(2, vt.getTenVattu());
            psInsert.setString(3, vt.getDonvitinh());
            psInsert.setInt(4, vt.getSoluong());
            psInsert.setString(5, vt.getMaKho());

            psInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm vật tư: " + e.getMessage(), e);
        }
    }

    public static boolean deleteVatTu(String maVatTu) {
        String sqlDelete = "DELETE FROM VatTu WHERE MaVatTu = ?";

        try (Connection conn = getConnection(); PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {

            psDelete.setString(1, maVatTu);
            int rowsDeleted = psDelete.executeUpdate();

            // Trả về true nếu xóa thành công
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa vật tư: " + e.getMessage(), e);
        }
    }

    public static boolean updateVatTu(VatTu vt) {
        String sql = "UPDATE VatTu SET TenVatTu = ?, DonViTinh = ?, SoLuong = ?, MaKho = ? WHERE MaVatTu = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Kiểm tra Mã Kho có tồn tại không
            if (!checkMaKhoExists(vt.getMaKho(), conn)) {
                throw new SQLException("Mã kho mới không tồn tại.");
            }

            // Kiểm tra số lượng mới có hợp lệ không (số lượng phải đủ để thực hiện giao dịch)
            if (!checkSoLuongValid(vt.getMavattu(), vt.getSoluong(), conn)) {
                throw new SQLException("Số lượng mới không hợp lệ. Số lượng mới không đủ để thực hiện các giao dịch.");
            }

            // Cập nhật thông tin Vật Tư
            pstmt.setString(1, vt.getTenVattu());
            pstmt.setString(2, vt.getDonvitinh());
            pstmt.setInt(3, vt.getSoluong());
            pstmt.setString(4, vt.getMaKho());
            pstmt.setString(5, vt.getMavattu());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Trả về true nếu có bản ghi được cập nhật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật vật tư: " + e.getMessage(), e);
        }
    }

// Kiểm tra Mã Kho có tồn tại không
    private static boolean checkMaKhoExists(String maKho, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM KHO WHERE MaKho = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maKho);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Nếu số lượng dòng trả về > 0 thì Mã Kho tồn tại
                }
            }
        }
        return false;
    }

// Kiểm tra số lượng vật tư có đủ để xuất không
    private static boolean checkSoLuongValid(String maVatTu, int soLuongMoi, Connection conn) throws SQLException {
        String sql = "SELECT ISNULL(SUM(CTPN.SoLuong), 0) - ISNULL(SUM(CTPX.SoLuong), 0) AS SoLuongXuat FROM CHITIETPHIEUNHAP CTPN "
                + "LEFT JOIN CHITIETPHIEUXUAT CTPX ON CTPN.MaVatTu = CTPX.MaVatTu "
                + "WHERE CTPN.MaVatTu = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maVatTu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int soLuongXuat = rs.getInt("SoLuongXuat");
                    return soLuongMoi >= soLuongXuat; // Kiểm tra số lượng mới phải lớn hơn hoặc bằng số lượng đã xuất
                }
            }
        }
        return false;
    }

    // Lấy danh sách vật tư
    public static List<VatTu> getAllVatTu() {
        List<VatTu> vatTuList = new ArrayList<>();
        String sql = "SELECT * FROM VATTU";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VatTu vt = new VatTu(
                        rs.getString("MaVatTu"),
                        rs.getString("TenVatTu"),
                        rs.getString("DonViTinh"),
                        rs.getInt("SoLuong"),
                        rs.getString("MaKho")
                );
                vatTuList.add(vt);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách vật tư: " + e.getMessage());
            e.printStackTrace();
        }
        return vatTuList;
    }

    // CODE không cho nhập trùng trong bảng  VT 
    public static boolean checkMaVTTonTai(String maVatTu) {
        String sql = "SELECT COUNT(*) FROM VatTu WHERE MaVatTu = ?";
        boolean exists = false;

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maVatTu);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true; // Nếu COUNT > 0 thì Mã Vật Tư đã tồn tại
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra trùng lặp Mã Vật Tư: " + e.getMessage());
            e.printStackTrace();
        }
        return exists;
    }

// timf kiếm vật tư theo mã và tên
    public static List<VatTu> searchVatTuByKeyword(String keyword) {
        List<VatTu> list = new ArrayList<>();
        String sql = "SELECT MaVatTu, TenVatTu, DonViTinh, SoLuong, MaKho "
                + "FROM VatTu "
                + "WHERE MaVatTu LIKE ? OR TenVatTu LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                VatTu vt = new VatTu(
                        rs.getString("MaVatTu"),
                        rs.getString("TenVatTu"),
                        rs.getString("DonViTinh"),
                        rs.getInt("SoLuong"),
                        rs.getString("MaKho")
                );
                list.add(vt);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm vật tư: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

}
