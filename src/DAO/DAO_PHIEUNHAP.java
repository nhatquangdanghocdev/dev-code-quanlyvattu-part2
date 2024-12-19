
package dao;

import Model.PhieuNhap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_PHIEUNHAP {
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
     public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
    
     public static boolean insertPhieuNhap(PhieuNhap pn) {
        String sqlGetMaxMaPhieuNhap = "SELECT MAX(MaPhieuNhap) FROM PHIEUNHAP";  // Truy vấn để lấy mã phiếu xuất lớn nhất.
        String sqlInsert = "INSERT INTO PHIEUNHAP (MaPhieuNhap, NgayNhap, MaKho, MaNhaCungCap) VALUES (?, ?, ?, ?)";

        // Kiểm tra dữ liệu đầu vào
        if (pn.getNgayNhap().isEmpty() || pn.getMaKho().isEmpty()
            || pn.getMaNhaCungCap().isEmpty() ){
            
            System.out.println("Vui lòng nhập đầy đủ thông tin !");
            return false;  
        }

        try (Connection conn = getConnection(); PreparedStatement psGetMaxMaPhieuNhap = conn.prepareStatement(sqlGetMaxMaPhieuNhap);
                                             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            // Lấy mã sinh viên lớn nhất trong bảng
            ResultSet rs = psGetMaxMaPhieuNhap.executeQuery();
            String newMaPhieuNhap = "PN01";  // Mặc định nếu không có sinh viên nào trong bảng
            if (rs.next()) {
                String maxMaPhieuNhap = rs.getString(1);
                if (maxMaPhieuNhap != null) {
                    int nextMaPhieuNhap = Integer.parseInt(maxMaPhieuNhap.substring(2)) + 1;  // Lấy số sau "PX"
                    newMaPhieuNhap = String.format("PN%02d", nextMaPhieuNhap);  // Tạo mã sinh viên mới, ví dụ "PX00002"
                }
            }

            // Cập nhật thông tin sinh viên mới
            psInsert.setString(1, newMaPhieuNhap);  // Sử dụng mã phiếu xuất mới
            psInsert.setString(2, pn.getNgayNhap());
            psInsert.setString(3, pn.getMaKho()); 
            psInsert.setString(4, pn.getMaNhaCungCap());

            // Thực thi câu lệnh INSERT và kiểm tra kết quả
            return psInsert.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();  // In ra thông báo lỗi chi tiết
        }
        return false;
    }
    public static void updatePhieuNhap(PhieuNhap pn) {
        String sql = "UPDATE PhieuNhap SET NgayNhap = ?, MaKho = ?, MaNhaCungCap = ? WHERE MaPhieuNhap = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, pn.getNgayNhap());
        pstmt.setString(2, pn.getMaKho());
        pstmt.setString(3, pn.getMaNhaCungCap());
        pstmt.setString(4, pn.getMaPhieuNhap());

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Cập nhật phiếu nhập thành công!");
        } else {
            System.out.println("Không tìm thấy phiếu nhập với mã: " + pn.getMaPhieuNhap());
        }
    } catch (SQLException e) {
        if (e.getMessage().contains("FOREIGN KEY constraint")) {
            if (e.getMessage().contains("MaKho")) {
                System.err.println("Lỗi: Mã kho không tồn tại hoặc đã bị xóa!");
            } else if (e.getMessage().contains("MaNhaCungCap")) {
                System.err.println("Lỗi: Mã nhà cung cấp không tồn tại hoặc đã bị xóa!");
            }
        } else {
            System.err.println("Lỗi khi cập nhật phiếu nhập: " + e.getMessage());
        }
    }
}
    
      public static boolean deletePhieuNhap(String maPhieuNhap) {
//          if (isPhieuNhapReferenced(maPhieuNhap)) {
//        System.err.println("Phiếu nhập đang được tham chiếu, không thể xóa!");
//        return false; // Không thực hiện xóa
//    }

    String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap = ?";
    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, maPhieuNhap);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Xóa phiếu nhập thành công!");
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi xóa phiếu nhập: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
    }
      public static List<PhieuNhap> getAllPhieuNhap() {
        List<PhieuNhap> phieuNhapList = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                    rs.getString("MaPhieuNhap"),
                    rs.getString("NgayNhap"),
                    rs.getString("MaKho"),
                    rs.getString("MaNhaCungCap")
                );
                phieuNhapList.add(pn);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return phieuNhapList;
    }
    public static List<PhieuNhap> searchPhieuNhap(String keyword) {
    List<PhieuNhap> resultList = new ArrayList<>();
    String sql = "SELECT * FROM PhieuNhap WHERE MaPhieuNhap LIKE ? OR MaKho LIKE ? OR MaNhaCungCap LIKE ?";

    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            PhieuNhap pn = new PhieuNhap(
                rs.getString("MaPhieuNhap"),
                rs.getString("NgayNhap"),
                rs.getString("MaKho"),
                rs.getString("MaNhaCungCap")
            );
            resultList.add(pn);
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi tìm kiếm phiếu nhập: " + e.getMessage());
        e.printStackTrace();
    }
    return resultList;
}
    public static List<Object[]> viewWithMaPhieuNhap_PN_InPN(String keyword){
        List<Object[]> result = new ArrayList<>();
        
        String sql = "SELECT CTPN.MaPhieuNhap, CTPN.MaVatTu, CTPN.SoLuong, PN.NgayNhap, PN.MaKho, PN.MaNhaCungCap "
               + "FROM PHIEUNHAP PN INNER JOIN CHITIETPHIEUNHAP CTPN ON PN.MaPhieuNhap = CTPN.MaPhieuNhap "
               + "WHERE CTPN.MaPhieuNhap LIKE ? ";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                    row[0] = rs.getString("MaPhieuNhap");
                    row[1] = rs.getString("MaVatTu");
                    row[2] = rs.getString("SoLuong");
                    row[3] = rs.getString("NgayNhap");
                    row[4] = rs.getString("MaKho");
                    row[5] = rs.getString("MaNhaCungCap");
            
                result.add(row);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm phiếu xuất: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
        }
    public static List<PhieuNhap> searchWithMaPhieuNhap_PN(String keyword){
        List<PhieuNhap> list_ChiTietPhieuNhap = new ArrayList<>();
        
        String sql = "SELECT MaPhieuNhap, NgayNhap, MaKho, MaNhaCungCap " + 
                     "FROM PHIEUNHAP " + 
                     "WHERE MaPhieuNhap LIKE ? ";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                    rs.getString("MaPhieuNhap"),
                    rs.getString("NgayNhap"),
                    rs.getString("MaKho"),
                    rs.getString("MaNhaCungCap")
                );
                list_ChiTietPhieuNhap.add(pn);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm phiếu nhập: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list_ChiTietPhieuNhap;
        }
}

