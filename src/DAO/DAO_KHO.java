package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.Kho;
public class DAO_KHO {

    private static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;databaseName=QLVT;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "1";
    private static int rowsAffected;

  static {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // Đăng ký driver SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không thể tải driver SQL Server", e);
        }catch (SQLException ex) {
            
        } 
        
    }
    // Phương thức tạo kết nối
    public static Connection getConnection() throws SQLException {
//        System.out.println("Connect thanh cong");
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }    

 // Phương thức thêm kho
public static boolean addKho(Kho kho) {
    String sqlGetMaxId = "SELECT MAX(maKho) FROM Kho";  // Truy vấn để lấy mã kho lớn nhất
    String sqlInsert = "INSERT INTO Kho (maKho, tenKho, diaChi) VALUES (?, ?, ?)";  // Thêm kho với thông tin cần thiết

    // Kiểm tra dữ liệu đầu vào
    if (kho.getTenKho().isEmpty()
            || kho.getDiachi().isEmpty()) {
        System.out.println("Thông tin kho không hợp lệ!");
        return false;
    }

    try (Connection conn = getConnection();
         PreparedStatement psGetMax = conn.prepareStatement(sqlGetMaxId);
         PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

        // Lấy mã kho lớn nhất trong bảng
        ResultSet rs = psGetMax.executeQuery();
        String newMaKho = "K01";  // Mặc định nếu không có kho nào trong bảng
        if (rs.next()) {
            String maxMaKho = rs.getString(1);
            if (maxMaKho != null) {
                int nextId = Integer.parseInt(maxMaKho.substring(1)) + 1;  // Lấy số sau "KHO"
                newMaKho = String.format("K%02d", nextId);  // Tạo mã kho mới, ví dụ "KHO00002"
            }
        }

        // Cập nhật thông tin kho mới
        psInsert.setString(1, newMaKho);  // Sử dụng mã kho mới
        psInsert.setString(2, kho.getTenKho());
        psInsert.setString(3, kho.getDiachi());

        // Thực thi câu lệnh INSERT
        int rowsInserted = psInsert.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Thêm kho thành công với mã: " + newMaKho);
            return true;
        } else {
            System.out.println("Thêm kho thất bại!");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Lỗi khi thêm kho: " + e.getMessage());
        return false;
    }
}

// Phương thức thêm kho từ form giao diện
 
      public static boolean check(String maKho) throws SQLException{
    String sql = "SELECT COUNT(*) FROM Kho WHERE MaKho = ?";
    boolean exists = false;

    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, maKho);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            exists = true; // Nếu COUNT > 0 thì Mã Vật Tư đã tồn tại
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi kiểm tra trùng lặp Mã Kho: " + e.getMessage());
        e.printStackTrace();
    }
    return exists;
}
    // Cập nhật vật tư (khong dc update ma nen de ma o cuoi )
 public static boolean updateKho(Kho kho) {
    String sql = "UPDATE Kho SET  tenKho = ?, diaChi = ? WHERE MaKho = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, kho.getTenKho());
        ps.setString(2, kho.getDiachi());
        ps.setString(3, kho.getMaKho());
        int rowsUpdated = ps.executeUpdate();
        return rowsUpdated > 0;  // Thành công nếu có ít nhất một dòng được cập nhật
    } catch (SQLException e) {
        System.out.println("Lỗi khi cập nhật kho: " + e.getMessage());
        return false;
    }
}


    // Xóa vật tư
    public static boolean deleteKho(String maKho) {
        String sql = "DELETE FROM Kho WHERE MaKho = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKho);
            rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Xóa kho thành công!");
                return true;
            } else {
                System.out.println("Khong tim thay kho");
                return false;
            }
        } catch (SQLException e) {
//            System.err.println("Lỗi khi xóa kho: " + e.getMessage());
//            e.printStackTrace();
        }
        return false;
    }

    public static boolean isKhoReferenced(String maKho) {
        String sql = "SELECT COUNT(*) FROM PhieuXuat WHERE MaKho = ?";
        try (Connection conn = DAO_PHIEUNHAP.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maKho);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu COUNT > 0, nghĩa là đang được tham chiếu
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách vật tư
    public static List<Kho> getAllKho() {
        List<Kho> list = new ArrayList<>();
        String sql = "SELECT MaKho, TenKho, DiaChi FROM Kho";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kho kh = new Kho();
                kh.setMaKho(rs.getString("MaKho")); // Lấy mã kho
                kh.setTenKho(rs.getString("TenKho")); // Lấy mã vật tư
                kh.setDiachi(rs.getString("DiaChi")); // Lấy số lượng nhập
                list.add(kh); // Thêm đối tượng vào danh sách
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách vật tư trong kho: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Phương thức tìm kho theo mã
 public static Kho searchKhoByMa(String maKho) {
    String sql = "SELECT * FROM Kho WHERE MaKho = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, maKho);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Kho kho = new Kho();
            kho.setMaKho(rs.getString("maKho"));
            kho.setTenKho(rs.getString("tenKho"));
            kho.setDiachi(rs.getString("diaChi"));
            return kho;
        }
    } catch (SQLException e) {
        System.out.println("Lỗi khi tìm kiếm kho: " + e.getMessage());
    }
    return null;
}

public static List<Kho> searchWithKeyword(String keyword) {
    List<Kho> list = new ArrayList<>();
    String sql = "SELECT MaKho, TenKho, DiaChi FROM KHO WHERE MaKho LIKE ? OR TenKho LIKE ?";

    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Kho kh = new Kho(
                    rs.getString("MaKho"),
                    rs.getString("TenKho"),
                    rs.getString("DiaChi")
            );
            list.add(kh);
        }
    } catch (SQLException ex) {
        System.err.println("Lỗi khi tìm kiếm kho: " + ex.getMessage());
        ex.printStackTrace();
    }
    return list;
}
}
