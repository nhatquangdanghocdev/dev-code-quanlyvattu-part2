
package DAO;

import static DAO.DAO_CHITIETPHIEUXUAT.getConnection;
import Model.ChiTietPhieuXuat;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class DAO_CHITIETPHIEUXUAT {
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
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    public static List<ChiTietPhieuXuat> getAllChiTietPhieuXuat() {
        List<ChiTietPhieuXuat> list_ChiTietPhieuXuat = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETPHIEUXUAT";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ChiTietPhieuXuat ctpx = new ChiTietPhieuXuat(
                    rs.getString("MaPhieuXuat"),
                    rs.getString("MaVatTu"),
                    rs.getInt("SoLuong")
                );
                list_ChiTietPhieuXuat.add(ctpx);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list_ChiTietPhieuXuat;
    }

    public static boolean insertIntoCHITIETPHIEUXUAT(ChiTietPhieuXuat ctpx) {
    String sqlInsert = "INSERT INTO CHITIETPHIEUXUAT (MaPhieuXuat, MaVatTu, SoLuong) VALUES (?, ?, ?)";


    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
            psInsert.setString(1, ctpx.getMaPhieuXuat_CTPX());
            psInsert.setString(2, ctpx.getMaVatTu());
            psInsert.setInt(3, ctpx.getSoLuong());

            rowsInserted = psInsert.executeUpdate();
            if (rowsInserted > 0) {
                conn.commit();
                return true;
            }
            return false;
        }catch(SQLException innerEx){
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Lỗi: " + innerEx.getMessage());
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    // Cập nhật phiếu xuất (khong dc update ma nen de ma o cuoi )
    public static boolean updateChiTietPhieuXuat(ChiTietPhieuXuat ctpx, String maPhieuXuatDK, String maVatTuDK) throws SQLException {
        String sql = "UPDATE CHITIETPHIEUXUAT SET MaPhieuXuat = ?, MaVatTu = ?, SoLuong = ? WHERE MaPhieuXuat = ? AND MaVatTu = ?";
        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, ctpx.getMaPhieuXuat_CTPX());
            ps.setString(2, ctpx.getMaVatTu());
            ps.setInt(3, ctpx.getSoLuong());
            
            ps.setString(4, maPhieuXuatDK);
            ps.setString(5, maVatTuDK);

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
    public static boolean deleteChiTietPhieuXuat(String maPhieuXuat_CTPX, String maVatTu, int soLuong) {
        String sql = "DELETE FROM CHITIETPHIEUXUAT WHERE MaPhieuXuat = ? AND MaVatTu = ? AND SoLuong = ?";

        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, maPhieuXuat_CTPX);
            pstmt.setString(2, maVatTu);
            pstmt.setInt(3, soLuong);
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
    
    
    public static List<ChiTietPhieuXuat> searchWithMaPhieuXuat_CTPX(String keyword){
        List<ChiTietPhieuXuat> list_ChiTietPhieuXuat = new ArrayList<>();
        String sql = "SELECT MaPhieuXuat, MaVatTu, SoLuong " + 
                     "FROM CHITIETPHIEUXUAT " +     
                     "WHERE MaPhieuXuat LIKE ?" ;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuXuat ctpx = new ChiTietPhieuXuat(
                    rs.getString("MaPhieuXuat"),
                    rs.getString("MaVatTu"),
                    rs.getInt("SoLuong")
                );
                list_ChiTietPhieuXuat.add(ctpx);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm chi tiết phiếu xuất: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list_ChiTietPhieuXuat;
        }
    
}
