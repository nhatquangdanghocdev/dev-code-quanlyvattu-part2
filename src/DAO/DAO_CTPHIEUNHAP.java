package dao;

import static dao.DAO_CTPHIEUNHAP.getConnection;
import Model.ChiTietPhieuNhapKho;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author asus
 */
public class DAO_CTPHIEUNHAP {

    private static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;databaseName=QLVT;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "1";
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
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static boolean insertCTPhieuNhap(ChiTietPhieuNhapKho ctpnk) {
        String sql = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaVatTu, SoLuong) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ctpnk.getMaPhieuNhap());
            pstmt.setString(2, ctpnk.getMaVatTu());
            pstmt.setInt(3, ctpnk.getSoLuong());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateChiTietPhieuNhap(ChiTietPhieuNhapKho ctpnk) throws SQLException {
        String sql = "UPDATE CHITIETPHIEUNHAP SET MaPhieuNhap = ?, MaVatTu = ?, SoLuong = ? WHERE MaPhieuNhap = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ctpnk.getMaPhieuNhap());
                ps.setString(2, ctpnk.getMaVatTu());
                ps.setInt(3, ctpnk.getSoLuong());

                ps.setString(4, ctpnk.getMaPhieuNhap());

                rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    conn.commit();
                    return rowsUpdated > 0;
                }
            } catch (SQLException innerEx) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Lỗi: " + innerEx.getMessage());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    public static boolean isCTPhieuNhapExists(String maPhieuNhap, String maVatTu) throws SQLException {
    String sql = "SELECT COUNT(*) FROM CHITIETPHIEUNHAP WHERE MaPhieuNhap = ? AND MaVatTu = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, maPhieuNhap);
        stmt.setString(2, maVatTu);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu đã tồn tại
            }
        }
    }
    return false; // Không tồn tại
}

    public static boolean deleteCTPhieuNhap(String maPhieuNhap, String maVatTu) {
        String sql = "DELETE FROM CHITIETPHIEUNHAP WHERE MaPhieuNhap = ? AND MaVatTu = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhieuNhap);
            pstmt.setString(2, maVatTu);
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

    public static List<ChiTietPhieuNhapKho> getAllCtphieunhap() {
        List<ChiTietPhieuNhapKho> cTPhieuNhapList = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETPHIEUNHAP";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ChiTietPhieuNhapKho ctpnk = new ChiTietPhieuNhapKho(
                        rs.getString("MaPhieuNhap"),
                        rs.getString("MaVatTu"),
                        rs.getInt("SoLuong")
                );
                cTPhieuNhapList.add(ctpnk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách vật tư: " + e.getMessage());
            e.printStackTrace();
        }
        return cTPhieuNhapList;
    }

    public static List<ChiTietPhieuNhapKho> searchWithMaPhieuNhap_CTPN(String keyword) {
        List<ChiTietPhieuNhapKho> list_ChiTietPhieuNhapKho = new ArrayList<>();
        String sql = "SELECT MaPhieuNhap, MaVatTu, SoLuong "
                + "FROM CHITIETPHIEUNHAP "
                + "WHERE MaPhieuNhap LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuNhapKho ctpnk = new ChiTietPhieuNhapKho(
                        rs.getString("MaPhieuNhap"),
                        rs.getString("MaVatTu"),
                        rs.getInt("SoLuong")
                );
                list_ChiTietPhieuNhapKho.add(ctpnk);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm chi tiết phiếu xuất: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list_ChiTietPhieuNhapKho;
    }
}
