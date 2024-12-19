package DAO;

import Model.NhaCungCap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhaCungCap {

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

    public static void insertNhaCungCap(NhaCungCap ncc) {
        String sqlGetMaxMaNCC = "SELECT MaNhaCungCap FROM NhaCungCap";
        String sqlInsert = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, Email, DiaChi) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement psGetMax = conn.prepareStatement(sqlGetMaxMaNCC); PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            // Lấy tất cả mã nhà cung cấp và tìm mã lớn nhất
            ResultSet rs = psGetMax.executeQuery();
            int maxNumber = 0;
            while (rs.next()) {
                String currentMaNCC = rs.getString("MaNhaCungCap");
                if (currentMaNCC != null && currentMaNCC.startsWith("NCC")) {
                    try {
                        int number = Integer.parseInt(currentMaNCC.substring(3));
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi định dạng mã nhà cung cấp: " + currentMaNCC);
                    }
                }
            }

            // Tạo mã nhà cung cấp mới
            String newMaNCC = "NCC" + (maxNumber + 1);

            // Thực hiện thêm nhà cung cấp
            psInsert.setString(1, newMaNCC);
            psInsert.setString(2, ncc.getTennhacungcap());
            psInsert.setString(3, ncc.getSodienThoai());
            psInsert.setString(4, ncc.getEmail());
            psInsert.setString(5, ncc.getDiaChi());

            int rowsInserted = psInsert.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm nhà cung cấp thành công với mã: " + newMaNCC);
            } else {
                System.out.println("Không thể thêm nhà cung cấp.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

// Phương thức cập nhật nhà cung cấp (không thay đổi)
    public static void updateNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNhaCungCap = ?, SoDienThoai = ?, Email = ?, DiaChi = ? WHERE MaNhaCungCap = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Đặt giá trị cho các tham số trong câu lệnh UPDATE
            pstmt.setString(1, ncc.getTennhacungcap());
            pstmt.setString(2, ncc.getSodienThoai());
            pstmt.setString(3, ncc.getEmail());
            pstmt.setString(4, ncc.getDiaChi());
            pstmt.setString(5, ncc.getManhacungcap());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật nhà cung cấp thành công!");
            } else {
                System.out.println("Không tìm thấy nhà cung cấp để cập nhật.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean deleteNhaCungCap(String maNhaCungCap) {
        // Sửa lại tên cột nếu cần để khớp với tên trong cơ sở dữ liệu
        String sqlCheckReference = "SELECT COUNT(*) FROM PHIEUNHAP WHERE MaNhaCungCap = ?";
        String sqlDelete = "DELETE FROM NHACUNGCAP WHERE MaNhaCungCap = ?";

        try (Connection conn = getConnection(); PreparedStatement psCheckRef = conn.prepareStatement(sqlCheckReference); PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {

            // Kiểm tra ràng buộc tham chiếu
            psCheckRef.setString(1, maNhaCungCap);
            try (ResultSet rs = psCheckRef.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Không thể xóa nhà cung cấp do đang được tham chiếu trong Phiếu Nhập.");
                    return false;
                }
            }

            // Xóa nhà cung cấp
            psDelete.setString(1, maNhaCungCap);
            int rowsDeleted = psDelete.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xóa nhà cung cấp thành công!");
                return true;
            } else {
                System.out.println("Không tìm thấy nhà cung cấp để xóa.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> nhaCungCapList = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("MaNhaCungCap"),
                        rs.getString("TenNhaCungCap"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email"),
                        rs.getString("DiaChi")
                );
                nhaCungCapList.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
        return nhaCungCapList;
    }

    public static String generateMaNhaCungCap() {
        String sql = "SELECT MAX(MaNhaCungCap) FROM NhaCungCap";
        String newMaNCC = "NCC01"; // Giá trị mặc định nếu bảng trống

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String maxMaNCC = rs.getString(1);
                if (maxMaNCC != null) {
                    int nextMaNCC = Integer.parseInt(maxMaNCC.substring(3)) + 1;  // Lấy số sau "NCC"
                    newMaNCC = String.format("NCC%02d", nextMaNCC);  // Tạo mã mới như NCC02, NCC03,...
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy mã nhà cung cấp mới: " + e.getMessage());
            e.printStackTrace();
        }

        return newMaNCC;
    }

    // Kiểm tra mã nhà cung cấp đã tồn tại
    public static boolean checkMaNhaCungCapTonTai(String maNCC) {
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE MaNhaCungCap = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Mã nhà cung cấp đã tồn tại
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

// Kiểm tra email đã tồn tại
    public static boolean checkEmailNhaCungCapTonTai(String email) {
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE Email = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Email đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<NhaCungCap> searchNhaCungCapByKeyword(String keyword) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap LIKE ? OR TenNhaCungCap LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ? OR DiaChi LIKE ?";
        List<NhaCungCap> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            pstmt.setString(3, query);
            pstmt.setString(4, query);
            pstmt.setString(5, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap(
                            rs.getString("MaNhaCungCap"),
                            rs.getString("TenNhaCungCap"),
                            rs.getString("SoDienThoai"),
                            rs.getString("Email"),
                            rs.getString("DiaChi")
                    );
                    list.add(ncc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isDuplicateEmail(String email) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static boolean isNhaCungCapReferenced(String maNhaCungCap) {
        String sql = "SELECT COUNT(*) FROM PhieuXuat WHERE MaNhaCungCap = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNhaCungCap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Nhà cung cấp này đang được tham chiếu trong Phiếu Xuất
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra tham chiếu nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

// Kiểm tra mã nhà cung cấp có tồn tại hay không
    public static boolean existsNhaCungCap(String maNhaCungCap) {
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE MaNhaCungCap = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNhaCungCap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

// Kiểm tra email có bị trùng không (trừ chính nhà cung cấp đang cập nhật)
    public static boolean isDuplicateEmail(String email, String maNhaCungCap) {
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE Email = ? AND MaNhaCungCap != ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, maNhaCungCap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email trùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
