package DAO;

import Model.ChiTietPhieuXuat;
import java.sql.*;
import Model.PhieuXuat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DAO_PHIEUXUAT {

    private static final String url = "jdbc:sqlserver://localhost:1433;databaseName=QLVT;trustServerCertificate=true";
    private static final String username = "sa";
    private static final String password = "1";
    private static int rowsInserted;
    private static int rowsUpdated;
    private static int rowsDeleted;

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
        return DriverManager.getConnection(url, username, password);
    }

    // Phương thức thêm phiếu xuất
    public static boolean insertIntoPHIEUXUAT(PhieuXuat px) {
        String sqlGetMaxMaPhieuXuat = "SELECT MAX(MaPhieuXuat) FROM PHIEUXUAT";  // Truy vấn để lấy mã phiếu xuất lớn nhất.
        String sqlInsert = "INSERT INTO PHIEUXUAT (MaPhieuXuat, NgayXuat, MaKho, MaPhongBan) VALUES (?, ?, ?, ?)";
        // Kiểm tra dữ liệu đầu vào
        if (px.getNgayXuat().isEmpty()
                || px.getMaKho().isEmpty()
                || px.getMaPhongBan().isEmpty() ){
            
            System.out.println("Vui lòng nhập đầy đủ thông tin !");
            return false;
            
        }
        // Khối try-with-resources bên ngoài để quản lý rollback và commit
        try(Connection conn = getConnection()){
            conn.setAutoCommit(false);
        // Khối try-with-resources bên trong để quản lý PreparedStatement
        try (PreparedStatement psGetMaxMaPhieuXuat = conn.prepareStatement(sqlGetMaxMaPhieuXuat);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
            
            
            // Lấy mã sinh viên lớn nhất trong bảng
            ResultSet rs = psGetMaxMaPhieuXuat.executeQuery();
            String newMaPhieuXuat = "PX01";  // Mặc định nếu không có mã phiếu xuất nào trong bảng
            if (rs.next()){
                String maxMaPhieuXuat = rs.getString(1);
                if (maxMaPhieuXuat != null) {
                    int nextMaPhieuXuat = Integer.parseInt(maxMaPhieuXuat.substring(2)) + 1;  // Lấy số sau "PX"
                    newMaPhieuXuat = String.format("PX%02d", nextMaPhieuXuat);  // Tạo mã phiếu xuất mới, ví dụ "PX00002"
                }
            }

            // Cập nhật thông tin sinh viên mới
            psInsert.setString(1, newMaPhieuXuat);  // Sử dụng mã phiếu xuất mới
            psInsert.setString(2, px.getNgayXuat()); 
            psInsert.setString(3, px.getMaKho()); 
            psInsert.setString(4, px.getMaPhongBan()); 

            // Thực thi câu lệnh INSERT và kiểm tra kết quả
            rowsInserted = psInsert.executeUpdate();
            if(rowsInserted>0){
            conn.commit();
            return rowsInserted > 0;
            }

        } catch (SQLException innerEx) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Lỗi: " + innerEx.getMessage());  // In ra thông báo lỗi chi tiết
                return false;
        }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Cập nhật phiếu xuất (khong dc update ma nen de ma o cuoi )
    public static boolean updatePhieuXuat(PhieuXuat px) throws SQLException {
        String sql = "UPDATE PHIEUXUAT SET NgayXuat = ?, MaKho = ?, MaPhongBan = ? WHERE MaPhieuXuat = ?";
        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, px.getNgayXuat());
            ps.setString(2, px.getMaKho());
            ps.setString(3, px.getMaPhongBan());
            
            ps.setString(4, px.getMaPhieuXuat_PX());

            rowsUpdated = ps.executeUpdate();
            if(rowsUpdated > 0){
                conn.commit();
                return rowsUpdated > 0;
            }
        }catch(SQLException innerEx){
            conn.rollback();
            JOptionPane.showMessageDialog(null,"Lỗi: " + innerEx.getMessage());
            return false;
        }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Xóa phiếu xuất
    public static boolean deletePhieuXuat(String maPhieuXuat_PX) {
        String sql = "DELETE FROM PHIEUXUAT WHERE MaPhieuXuat = ?";

        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, maPhieuXuat_PX);
            rowsDeleted = pstmt.executeUpdate();
            if(rowsDeleted > 0){
            conn.commit();
            return rowsDeleted > 0;
            }
                
        }catch (SQLException innerEx) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Lỗi: " + innerEx.getMessage());
                return false;
        }
            
        }catch(SQLException e){
                e.getStackTrace();
                return false;
        }
        return false;
    }
    

    // Lấy danh sách phiếu xuất:
    public static List<PhieuXuat> getAllPhieuXuat() {
        List<PhieuXuat> list_PhieuXuat = new ArrayList<>();
        String sql = "SELECT MaPhieuXuat, NgayXuat, MaKho, MaPhongBan FROM PHIEUXUAT";

        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                PhieuXuat px = new PhieuXuat();
                px.setMaPhieuXuat_PX(rs.getString("MaPhieuXuat")); // Lấy mã phiếu xuất
                px.setNgayXuat(rs.getString("NgayXuat")); // Lấy mã phòng ban
                px.setMaKho(rs.getString("MaKho")); // Lấy mã kho
                px.setMaPhongBan(rs.getString("MaPhongBan")); // Lấy ngày xuất

                list_PhieuXuat.add(px); // Thêm đối tượng vào danh sách
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách : " + e.getMessage());
            e.printStackTrace();
        }
        return list_PhieuXuat;
    }

    
    
    public static List<PhieuXuat> searchWithMaPhieuXuat_PX(String keyword){
        List<PhieuXuat> list_ChiTietPhieuXuat = new ArrayList<>();
        
        String sql = "SELECT MaPhieuXuat, NgayXuat, MaKho, MaPhongBan " + 
                     "FROM PHIEUXUAT " + 
                     "WHERE MaPhieuXuat LIKE ? ";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuXuat px = new PhieuXuat(
                    rs.getString("MaPhieuXuat"),
                    rs.getString("NgayXuat"),
                    rs.getString("MaKho"),
                    rs.getString("MaPhongBan")
                );
                list_ChiTietPhieuXuat.add(px);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm phiếu xuất: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list_ChiTietPhieuXuat;
        }
    
    public static List<Object[]> viewWithMaPhieuXuat_PX_InPX(String keyword){
        List<Object[]> result = new ArrayList<>();
        
        String sql = "SELECT CTPX.MaPhieuXuat, CTPX.MaVatTu, CTPX.SoLuong, PX.NgayXuat, PX.MaKho, PX.MaPhongBan "
               + "FROM PHIEUXUAT PX INNER JOIN CHITIETPHIEUXUAT CTPX ON PX.MaPhieuXuat = CTPX.MaPhieuXuat "
               + "WHERE CTPX.MaPhieuXuat LIKE ? ";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                    row[0] = rs.getString("MaPhieuXuat");
                    row[1] = rs.getString("MaVatTu");
                    row[2] = rs.getString("SoLuong");
                    row[3] = rs.getString("NgayXuat");
                    row[4] = rs.getString("MaKho");
                    row[5] = rs.getString("MaPhongBan");
            
                result.add(row);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm phiếu xuất: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
        }
    
}


