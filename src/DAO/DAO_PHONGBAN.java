package dao;

import java.sql.*;
import Model.PhongBan;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public class DAO_PHONGBAN {

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

    public static boolean insertIntoPHONGBAN(PhongBan pb) {
        String sqlGetMaxMaPhongBan = "SELECT MAX(MaPhongBan) FROM PHONGBAN";  // Lấy mã phòng ban lớn nhất.
        String sqlInsert = "INSERT INTO PHONGBAN (MaPhongBan, TenPhongBan, DiaChi, SoDienThoai, Email) VALUES (?, ?, ?, ?, ?)";

        // Kiểm tra dữ liệu đầu vào
        if (pb.getTenPB().isEmpty()
                || pb.getDiachi().isEmpty()
                || pb.getSoDienThoai().isEmpty()
                || pb.getEmail().isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin!");
            return false;
        }

        try (Connection conn = getConnection(); PreparedStatement psGetMaxMaPhongBan = conn.prepareStatement(sqlGetMaxMaPhongBan); PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            // Lấy mã phòng ban lớn nhất trong bảng
            ResultSet rs = psGetMaxMaPhongBan.executeQuery();
            String newMaPhongBan = "PB01";  // Mặc định nếu không có phòng ban nào trong bảng
            if (rs.next()) {
                String maxMaPhongBan = rs.getString(1);
                if (maxMaPhongBan != null) {
                    int nextMaPhongBan = Integer.parseInt(maxMaPhongBan.substring(2)) + 1;  // Lấy số sau "PB"
                    newMaPhongBan = String.format("PB%02d", nextMaPhongBan);  // Tạo mã phòng ban mới, ví dụ "PB02"
                }
            }

            // Cập nhật thông tin phòng ban mới
            psInsert.setString(1, newMaPhongBan);  // Sử dụng mã phòng ban mới
            psInsert.setString(2, pb.getTenPB());
            psInsert.setString(3, pb.getDiachi());
            psInsert.setString(4, pb.getSoDienThoai());
            psInsert.setString(5, pb.getEmail());

            // Thực thi câu lệnh INSERT và kiểm tra kết quả
            return psInsert.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phòng ban: " + e.getMessage());
            e.printStackTrace();  // In ra thông báo lỗi chi tiết
        }
        return false;
    }

    // kiem tra ma phong ban co ton tại hay không 
    public static boolean checkMaPbExists(String maPb) {
        String sql = "SELECT COUNT(*) FROM PhongBan WHERE MaPhongBan = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maPb);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Mã phòng ban đã tồn tại
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã phòng ban: " + e.getMessage());
        }
        return false; // Mã phòng ban không tồn tại
    }

    public static boolean checkEmailExistsForOtherPhongBan(String email, String maPB) {
        String sql = "SELECT 1 FROM PhongBan WHERE Email = ? AND MaPhongBan != ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, maPB);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Nếu có kết quả trả về, nghĩa là email đã tồn tại trong phòng ban khác
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra email: " + e.getMessage());
            e.printStackTrace();
        }

        // Nếu không có kết quả nào, email không bị trùng
        return false;
    }

    public static PhongBan searchPhongBanByMa(String maPB) {
        String sql = "SELECT * FROM PhongBan WHERE MaPhongBan = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maPB);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Nếu tìm thấy phòng ban, tạo đối tượng PhongBan và trả về
                return new PhongBan(
                        rs.getString("MaPhongBan"),
                        rs.getString("TenPhongBan"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm phòng ban theo tên: " + e.getMessage());
            e.printStackTrace();
        }

        // Nếu không tìm thấy phòng ban, trả về null
        return null;
    }

    public static boolean updatePhg(PhongBan pb) {
        if (!checkMaPbExists(pb.getMaPB())) {
            System.err.println("Mã phòng ban không tồn tại trong cơ sở dữ liệu!");
            return false;
        }

        String sql = "UPDATE PhongBan SET TenPhongBan = ?, SoDienThoai = ?, Diachi = ?,  Email = ? WHERE MaPhongBan = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pb.getTenPB());
            pstmt.setString(3, pb.getDiachi());
            pstmt.setString(2, pb.getSoDienThoai());
            pstmt.setString(4, pb.getEmail());
            pstmt.setString(5, pb.getMaPB());
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phòng ban: " + e.getMessage());
            return false;
        }
    }
    // Xóa vật tư

    public static boolean deletePhog(String maPhongBan) {
        // Code thực hiện xóa phòng ban từ cơ sở dữ liệu
        try {
            String query = "DELETE FROM PHONGBAN WHERE maPhongBan = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, maPhongBan);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//
//    public static boolean isPhongBanReferenced(String maPhongBan) {
//        String sql = "SELECT COUNT(*) FROM PhieuXuat WHERE MaPhongBan = ?";
//        try (Connection conn = DAO_PhieuXuat.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, maPhongBan);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1) > 0; // Nếu COUNT > 0, nghĩa là đang được tham chiếu
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    // Lấy danh sách vật tư
    public static List<PhongBan> getAllPhongban() {
        List<PhongBan> PhogList = new ArrayList<>();
        String sql = "SELECT MaPhongBan, TenPhongBan, DiaChi, SoDienThoai,Email FROM PhongBan";

        try (Connection conn = getConnection(); java.sql.Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PhongBan pb = new PhongBan(
                        rs.getString("MaPhongBan"),
                        rs.getString("TenPhongBan"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email")
                );
                PhogList.add(pb);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách Phong Ban: " + e.getMessage());
            e.printStackTrace();
        }
        return PhogList;
    }

    public static List<PhongBan> searchPhongBanByKeyword(String keyword) {
        List<PhongBan> list = new ArrayList<>();
        String sql = "SELECT MaPhongBan, TenPhongBan, Diachi, SoDienThoai, Email "
                + "FROM PhongBan "
                + "WHERE MaPhongBan LIKE ? OR TenPhongBan LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhongBan pb = new PhongBan(
                        rs.getString("MaPhongBan"),
                        rs.getString("TenPhongBan"),
                        rs.getString("Diachi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email")
                );
                list.add(pb);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm phòng ban: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

    // khong cho trùng email 
    public static boolean checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM PHONGBAN WHERE Email = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có ít nhất 1 bản ghi trùng email
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Không tìm thấy email
    }

}
