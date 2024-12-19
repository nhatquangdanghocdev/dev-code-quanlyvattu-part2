package view;

import DAO.DAO_CHITIETPHIEUXUAT;
import Model.PhieuXuat;
import DAO.DAO_PHIEUXUAT;
import Model.ChiTietPhieuXuat;
import View.jfXemChiTietPhieuXuat;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class pnPhieuXuat extends javax.swing.JPanel {

    private DefaultTableModel tbl_ModelPX;
    private DefaultTableModel tbl_ModelCTPX;
    private DefaultTableModel tbl_ModelPXAndCTPX;

    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public pnPhieuXuat() {
        initComponents();
        codesize();
        tbl_ModelPX = (DefaultTableModel) tbl_PhieuXuat.getModel();
        tbl_ModelCTPX = (DefaultTableModel) tbl_ChiTietPhieuXuat.getModel();
//        tbl_ModelPXAndCTPX = (DefaultTableModel) tbl_PXAndCTPX.getModel();
        fillToTablePhieuXuat();
        fillToTableChiTietPhieuXuat();
    }

    public void clickHerePhieuXuat() {
        int row = tbl_PhieuXuat.getSelectedRow(); // Lấy chỉ số dòng được chọn

        // Kiểm tra xem có dòng nào được chọn không
        if (row > -1) {
            try {
                // Lấy dữ liệu từ bảng và chuyển đổi kiểu dữ liệu tương ứng
                String maPhieuXuat_PX = String.valueOf(tbl_PhieuXuat.getValueAt(row, 0)); // Lấy mã phiếu xuất (String)
                String ngayXuat = String.valueOf(tbl_PhieuXuat.getValueAt(row, 1)); // Lấy ngày xuất (String)
                String maKho = String.valueOf(tbl_PhieuXuat.getValueAt(row, 2)); // Lấy mã kho (String)  
                String maPhongBan = String.valueOf(tbl_PhieuXuat.getValueAt(row, 3)); // Lấy mã phòng ban (String)

                date = sdf.parse(ngayXuat);
                txt_NgayXuat.setDate(date);
                // Hiển thị ngày xuất dưới dạng chuỗi

                txt_MaKho.setText(maKho); // Hiển thị mã kho dưới dạng chuỗi
                txt_MaPhongBan.setText(maPhongBan);// Hiển thị ngày xuất dưới dạng chuỗi

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void fillToTablePhieuXuat() {
        tbl_ModelPX.setRowCount(0); // Xóa dữ liệu cũ
        List<PhieuXuat> list_PhieuXuat = DAO_PHIEUXUAT.getAllPhieuXuat();
        for (PhieuXuat px : list_PhieuXuat) {
            tbl_ModelPX.addRow(new Object[]{px.getMaPhieuXuat_PX(), px.getNgayXuat(), px.getMaKho(), px.getMaPhongBan()});
        }
    }

    private void addPhieuXuat() {

        if (txt_NgayXuat == null
                || txt_MaKho.getText().isEmpty()
                || txt_MaPhongBan.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;

        }

        date = txt_NgayXuat.getDate();

        try {

            PhieuXuat px = new PhieuXuat();
            px.setNgayXuat(String.valueOf(sdf.format(date)));
            px.setMaKho(String.valueOf(txt_MaKho.getText()));
            px.setMaPhongBan(String.valueOf(txt_MaPhongBan.getText()));

            boolean check = DAO_PHIEUXUAT.insertIntoPHIEUXUAT(px);

            if (check) {
                fillToTablePhieuXuat(); // Cập nhật bảng
                JOptionPane.showMessageDialog(this, "Thêm phiếu xuất thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phiếu xuất thất bại, vui lòng kiểm tra lại thông tin nhập vào!");
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void updatePhieuXuat() {
        int index = tbl_PhieuXuat.getSelectedRow();
        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để sửa !");
            return;
        }

        if (txt_NgayXuat == null
                || txt_MaKho.getText().isEmpty()
                || txt_MaPhongBan.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;

        }

        date = txt_NgayXuat.getDate();
        String dateToString = String.valueOf(sdf.format(date));

        try {

            PhieuXuat px = new PhieuXuat();
            px.setMaPhieuXuat_PX(String.valueOf(tbl_PhieuXuat.getValueAt(index, 0)));
            px.setNgayXuat(String.valueOf(dateToString));
            px.setMaKho(String.valueOf(txt_MaKho.getText().trim()));
            px.setMaPhongBan(String.valueOf(txt_MaPhongBan.getText().trim()));

            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn sửa phiếu xuất" + px.getMaPhieuXuat_PX() + "này không?", "Xác nhận sửa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = DAO.DAO_PHIEUXUAT.updatePhieuXuat(px);
                if (success) {
                    fillToTablePhieuXuat(); // Cập nhật bảng
                    JOptionPane.showMessageDialog(this, "Cập nhật phiếu xuất " + px.getMaPhieuXuat_PX() + " thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật phiếu xuất " + px.getMaPhieuXuat_PX() + " thất bại!");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Đã hủy sửa phiếu xuất.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }

    }

    private void deletePhieuXuat() {

        int index = tbl_PhieuXuat.getSelectedRow();

        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để xóa !");
            return;
        }

        try {

            String maPhieuXuat_PX = (String) tbl_PhieuXuat.getValueAt(index, 0);

            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phiếu xuất này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = DAO_PHIEUXUAT.deletePhieuXuat(maPhieuXuat_PX);
                if (success == true) {
                    JOptionPane.showMessageDialog(null, "Xóa phiếu xuất thành công!");
                    fillToTablePhieuXuat(); // Cập nhật bảng sau khi xóa
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa phiếu xuất thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Đã hủy xóa phiếu xuất.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    private void searchPhieuXuat() throws ParseException {

        String timMaPhieuXuat_PX = String.valueOf(txt_TimKiemPX.getText().trim());
        if (timMaPhieuXuat_PX.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu xuất để tìm!");
            return;
        }
        List<PhieuXuat> result = DAO_PHIEUXUAT.searchWithMaPhieuXuat_PX(timMaPhieuXuat_PX);
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu xuất với mã: " + timMaPhieuXuat_PX);
            // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
            tbl_ModelPX.setRowCount(0); // Xóa sạch dữ liệu trong bảng
        } else {
            tbl_ModelPX.setRowCount(0);// Xóa dữ liệu cũ trong bảng
            // Hiển thị thông tin lên các trường nhập liệu
            for (PhieuXuat px : result) {
                // Cập nhật bảng chỉ với kết quả tìm kiếm

                tbl_ModelPX.addRow(new Object[]{
                    px.getMaPhieuXuat_PX(),
                    px.getNgayXuat(),
                    px.getMaKho(),
                    px.getMaPhongBan()
                });
            }
        }

    }

    private void viewChiTietPhieuXuatInPX() {
        int index = tbl_PhieuXuat.getSelectedRow();

        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để xem chi tiết !");
            return;
        }

        String xemChiTietPhieuXuat_PX = (String) tbl_PhieuXuat.getValueAt(index, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xem chi tiết phiếu xuất này không?", "Xác nhận xem chi tiết", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Object[]> result = DAO_PHIEUXUAT.viewWithMaPhieuXuat_PX_InPX(xemChiTietPhieuXuat_PX);

            if (result.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu xuất với mã: " + xemChiTietPhieuXuat_PX);
                // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
                tbl_ModelPXAndCTPX.setRowCount(0); // Xóa sạch dữ liệu trong bảng
            } else {

                jfXemChiTietPhieuXuat viewFrame = new jfXemChiTietPhieuXuat();
                viewFrame.filToTablePXAndCTPX(result);
                viewFrame.setVisible(true);
                viewFrame.setLocationRelativeTo(null);

            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void fillToTableChiTietPhieuXuat() {
        tbl_ModelCTPX.setRowCount(0); // Xóa dữ liệu cũ
        List<ChiTietPhieuXuat> list_ChiTietPhieuXuat = DAO_CHITIETPHIEUXUAT.getAllChiTietPhieuXuat();
        for (ChiTietPhieuXuat ctpx : list_ChiTietPhieuXuat) {
            tbl_ModelCTPX.addRow(new Object[]{ctpx.getMaPhieuXuat_CTPX(), ctpx.getMaVatTu(), ctpx.getSoLuong()
            });
        }
    }

    public void clickHereChiTietPhieuXuat() {
        int row = tbl_ChiTietPhieuXuat.getSelectedRow(); // Lấy chỉ số dòng được chọn

        // Kiểm tra xem có dòng nào được chọn không
        if (row > -1) {
            try {
                // Lấy dữ liệu từ bảng và chuyển đổi kiểu dữ liệu tương ứng
                String maPhieuXuat_CTPX = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(row, 0)); // Lấy mã phiếu xuất (String)
                String maVatTu = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(row, 1)); // Lấy mã vật tư (String)
                String soLuong = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(row, 2)); // Lấy số lượng (String)

                txt_MaPhieuXuatCTPX.setText(maPhieuXuat_CTPX);
                // Hiển thị mã phiếu xuất dưới dạng chuỗi

                txt_MaVatTu.setText(maVatTu); // Hiển thị mã vật tư dưới dạng chuỗi.
                txt_SoLuong.setText(soLuong);// Hiển thị số lượng dưới dạng chuỗi.

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void addChiTietPhieuXuat() {

        if (txt_MaPhieuXuatCTPX.getText().isEmpty()
                || txt_MaVatTu.getText().isEmpty()
                || txt_SoLuong.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;

        }

        try {

            ChiTietPhieuXuat ctpx = new ChiTietPhieuXuat();
            ctpx.setMaPhieuXuat_CTPX(String.valueOf(txt_MaPhieuXuatCTPX.getText()));
            ctpx.setMaVatTu(String.valueOf(txt_MaVatTu.getText()));
            ctpx.setSoLuong(Integer.valueOf(txt_SoLuong.getText()));

            boolean check = DAO_CHITIETPHIEUXUAT.insertIntoCHITIETPHIEUXUAT(ctpx);

            if (check) {
                fillToTableChiTietPhieuXuat(); // Cập nhật bảng
                JOptionPane.showMessageDialog(this, "Thêm chi tiết phiếu xuất thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm chi tiết phiếu xuất thất bại, vui lòng kiểm tra lại thông tin nhập vào!");
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void updateChiTietPhieuXuat() {

        int index = tbl_ChiTietPhieuXuat.getSelectedRow();

        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để sửa chi tiết phiếu xuất!");
            return;
        }

        if (txt_MaPhieuXuatCTPX.getText().isEmpty()
                || txt_MaVatTu.getText().isEmpty()
                || txt_SoLuong.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;

        }

        try {

            ChiTietPhieuXuat ctpx = new ChiTietPhieuXuat();
            ctpx.setMaPhieuXuat_CTPX(String.valueOf(txt_MaPhieuXuatCTPX.getText().trim()));
            ctpx.setMaVatTu(String.valueOf(txt_MaVatTu.getText().trim()));
            ctpx.setSoLuong(Integer.valueOf(txt_SoLuong.getText().trim()));

            String maPhieuXuatDK = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(index, 0));
            String maVatTuDK = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(index, 1));

            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn sửa chi tiết phiếu xuất" + ctpx.getMaPhieuXuat_CTPX() + "này không?", "Xác nhận sửa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = DAO.DAO_CHITIETPHIEUXUAT.updateChiTietPhieuXuat(ctpx, maPhieuXuatDK, maVatTuDK);
                if (success) {
                    fillToTableChiTietPhieuXuat(); // Cập nhật bảng
                    JOptionPane.showMessageDialog(this, "Cập nhật chi tiết phiếu xuất " + ctpx.getMaPhieuXuat_CTPX() + " thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật chi tiết phiếu xuất " + ctpx.getMaPhieuXuat_CTPX() + " thất bại!");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Đã hủy sửa phiếu xuất.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }

    }

    private void deleteChiTietPhieuXuat() {

        int index = tbl_ChiTietPhieuXuat.getSelectedRow();

        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để xóa !");
            return;
        }

        try {

            String maPhieuXuat_CTPX = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(index, 0));
            String maVatTu = String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(index, 1));
            int soLuong = Integer.valueOf(String.valueOf(tbl_ChiTietPhieuXuat.getValueAt(index, 2)));

            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa chi tiết phiếu xuất này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = DAO_CHITIETPHIEUXUAT.deleteChiTietPhieuXuat(maPhieuXuat_CTPX, maVatTu, soLuong);
                if (success == true) {
                    JOptionPane.showMessageDialog(null, "Xóa chi tiết phiếu xuất thành công!");
                    fillToTableChiTietPhieuXuat(); // Cập nhật bảng sau khi xóa
                    txt_MaPhieuXuatCTPX.setText("");
                    txt_MaVatTu.setText("");
                    txt_SoLuong.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa chi tiết phiếu xuất thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Đã hủy xóa chi tiết phiếu xuất.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    private void searchChiTietPhieuXuat() throws ParseException {

        String timMaPhieuXuat_CTPX = String.valueOf(txt_TimKiemCTPX.getText().trim());
        if (timMaPhieuXuat_CTPX.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu xuất để tìm!");
            return;
        }
        List<ChiTietPhieuXuat> result = DAO_CHITIETPHIEUXUAT.searchWithMaPhieuXuat_CTPX(timMaPhieuXuat_CTPX);
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu xuất với mã: " + timMaPhieuXuat_CTPX);
            // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
            tbl_ModelCTPX.setRowCount(0); // Xóa sạch dữ liệu trong bảng
        } else {
            tbl_ModelCTPX.setRowCount(0);// Xóa dữ liệu cũ trong bảng
            // Hiển thị thông tin lên các trường nhập liệu
            for (ChiTietPhieuXuat ctpx : result) {
                // Cập nhật bảng chỉ với kết quả tìm kiếm

                tbl_ModelCTPX.addRow(new Object[]{
                    ctpx.getMaPhieuXuat_CTPX(),
                    ctpx.getMaVatTu(),
                    ctpx.getSoLuong()
                });
            }
        }
    }

    public void codesize() { //code nút btn tròn 
        btn_XuatExcel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:999;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0");
        btn_XemCT.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:999;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0");
        btn_Lammoi.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:999;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPhieuXuat = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_MaKho = new javax.swing.JTextField();
        txt_MaPhongBan = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_PhieuXuat = new javax.swing.JTable();
        txt_TimKiemPX = new javax.swing.JTextField();
        btn_TimPX = new javax.swing.JButton();
        btn_ThemPX = new javax.swing.JButton();
        btn_XoaPX = new javax.swing.JButton();
        btn_SuaPX = new javax.swing.JButton();
        txt_NgayXuat = new com.toedter.calendar.JDateChooser();
        pnPhieuXuatCT = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btn_TimCTPX = new javax.swing.JButton();
        txt_MaPhieuXuatCTPX = new javax.swing.JTextField();
        txt_MaVatTu = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_ChiTietPhieuXuat = new javax.swing.JTable();
        txt_TimKiemCTPX = new javax.swing.JTextField();
        btn_ThemCTPX = new javax.swing.JButton();
        btn_XoaCTPX = new javax.swing.JButton();
        btn_SuaCTPX = new javax.swing.JButton();
        btn_Lammoi = new javax.swing.JButton();
        btn_XemCT = new javax.swing.JButton();
        btn_XuatExcel = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_SoLuong = new javax.swing.JTextField();

        pnPhieuXuat.setPreferredSize(new java.awt.Dimension(0, 300));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel2.setText("Phiếu Xuất ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Ngày Xuất :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Mã Kho :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Mã Phòng Ban :");

        tbl_PhieuXuat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Phiếu Xuất", "Ngày Xuất", "Mã Kho", "Mã Phòng Ban"
            }
        ));
        tbl_PhieuXuat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_PhieuXuatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_PhieuXuat);

        btn_TimPX.setText("Tìm Kiếm");
        btn_TimPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TimPXActionPerformed(evt);
            }
        });

        btn_ThemPX.setText("Thêm");
        btn_ThemPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ThemPXActionPerformed(evt);
            }
        });

        btn_XoaPX.setText("Xóa");
        btn_XoaPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_XoaPXActionPerformed(evt);
            }
        });

        btn_SuaPX.setText("Sửa");
        btn_SuaPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SuaPXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnPhieuXuatLayout = new javax.swing.GroupLayout(pnPhieuXuat);
        pnPhieuXuat.setLayout(pnPhieuXuatLayout);
        pnPhieuXuatLayout.setHorizontalGroup(
            pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txt_NgayXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txt_MaKho, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                            .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txt_MaPhongBan))))
                    .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(btn_ThemPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_XoaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_SuaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(btn_TimPX, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_TimKiemPX, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(135, 135, 135))))
        );
        pnPhieuXuatLayout.setVerticalGroup(
            pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_TimKiemPX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_TimPX))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnPhieuXuatLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(txt_NgayXuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_MaKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txt_MaPhongBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(pnPhieuXuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_ThemPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XoaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_SuaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel6.setText("Phiếu Xuất Chi Tiết");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Mã Phiếu Xuất :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Mã Vật Tư :");

        btn_TimCTPX.setText("Tìm Kiếm");
        btn_TimCTPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TimCTPXActionPerformed(evt);
            }
        });

        tbl_ChiTietPhieuXuat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Phiếu Xuất", "Mã Vật Tư", "Số Lượng"
            }
        ));
        tbl_ChiTietPhieuXuat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ChiTietPhieuXuatMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_ChiTietPhieuXuat);

        btn_ThemCTPX.setText("Thêm");
        btn_ThemCTPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ThemCTPXActionPerformed(evt);
            }
        });

        btn_XoaCTPX.setText("Xóa");
        btn_XoaCTPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_XoaCTPXActionPerformed(evt);
            }
        });

        btn_SuaCTPX.setText("Sửa");
        btn_SuaCTPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SuaCTPXActionPerformed(evt);
            }
        });

        btn_Lammoi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btn_Lammoi.setText("Làm Mới");
        btn_Lammoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LammoiActionPerformed(evt);
            }
        });

        btn_XemCT.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btn_XemCT.setText("Xem Chi Tiết");
        btn_XemCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_XemCTActionPerformed(evt);
            }
        });

        btn_XuatExcel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btn_XuatExcel.setText("Xuất Excel");
        btn_XuatExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_XuatExcelActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Số Lượng :");

        javax.swing.GroupLayout pnPhieuXuatCTLayout = new javax.swing.GroupLayout(pnPhieuXuatCT);
        pnPhieuXuatCT.setLayout(pnPhieuXuatCTLayout);
        pnPhieuXuatCTLayout.setHorizontalGroup(
            pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatCTLayout.createSequentialGroup()
                        .addComponent(btn_ThemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_XoaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_SuaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_MaPhieuXuatCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_MaVatTu))
                                .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_SoLuong))))
                        .addGap(18, 18, 18)))
                .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatCTLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_TimCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_TimKiemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatCTLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_Lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_XemCT, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_XuatExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        pnPhieuXuatCTLayout.setVerticalGroup(
            pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuXuatCTLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txt_MaPhieuXuatCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txt_MaVatTu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txt_SoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ThemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_XoaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_SuaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuXuatCTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_TimKiemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_TimCTPX))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(pnPhieuXuatCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XemCT, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XuatExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPhieuXuat, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
            .addComponent(pnPhieuXuatCT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnPhieuXuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnPhieuXuatCT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ThemPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemPXActionPerformed
        addPhieuXuat();
    }//GEN-LAST:event_btn_ThemPXActionPerformed

    private void btn_XoaPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XoaPXActionPerformed
        deletePhieuXuat();
    }//GEN-LAST:event_btn_XoaPXActionPerformed

    private void btn_SuaPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaPXActionPerformed
        updatePhieuXuat();
    }//GEN-LAST:event_btn_SuaPXActionPerformed

    private void btn_TimPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TimPXActionPerformed
        try {
            searchPhieuXuat();
        } catch (ParseException ex) {
            Logger.getLogger(pnPhieuXuat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_TimPXActionPerformed

    private void btn_XemCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XemCTActionPerformed
        viewChiTietPhieuXuatInPX();
    }//GEN-LAST:event_btn_XemCTActionPerformed

    private void btn_ThemCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemCTPXActionPerformed
        addChiTietPhieuXuat();
    }//GEN-LAST:event_btn_ThemCTPXActionPerformed

    private void btn_XoaCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XoaCTPXActionPerformed
        deleteChiTietPhieuXuat();
    }//GEN-LAST:event_btn_XoaCTPXActionPerformed

    private void btn_SuaCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaCTPXActionPerformed
        updateChiTietPhieuXuat();
    }//GEN-LAST:event_btn_SuaCTPXActionPerformed

    private void btn_TimCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TimCTPXActionPerformed
        try {
            searchChiTietPhieuXuat();
        } catch (ParseException ex) {
            Logger.getLogger(pnPhieuXuat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_TimCTPXActionPerformed

    private void tbl_PhieuXuatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_PhieuXuatMouseClicked
        clickHerePhieuXuat();
    }//GEN-LAST:event_tbl_PhieuXuatMouseClicked

    private void tbl_ChiTietPhieuXuatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ChiTietPhieuXuatMouseClicked
        clickHereChiTietPhieuXuat();
    }//GEN-LAST:event_tbl_ChiTietPhieuXuatMouseClicked

    private void btn_LammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LammoiActionPerformed
       fillToTableChiTietPhieuXuat();
       fillToTablePhieuXuat();
        txt_TimKiemCTPX.setText("");
       txt_TimKiemPX.setText("");
       txt_NgayXuat.setDate(null);
       txt_MaKho.setText("");
       txt_MaPhongBan.setText("");
       txt_MaPhieuXuatCTPX.setText("");
       txt_MaVatTu.setText("");
       txt_SoLuong.setText("");
    }//GEN-LAST:event_btn_LammoiActionPerformed

    private void btn_XuatExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XuatExcelActionPerformed
        exportToExcel();
    }//GEN-LAST:event_btn_XuatExcelActionPerformed

    
    private void exportToExcel() {
        try {
            // Tạo Workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("ChiTietPhieuXuat");

            // Tạo tiêu đề cột
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tbl_ChiTietPhieuXuat.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tbl_ChiTietPhieuXuat.getColumnName(i));
            }

            // Ghi dữ liệu từ bảng vào file Excel
            for (int i = 0; i < tbl_ChiTietPhieuXuat.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tbl_ChiTietPhieuXuat.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = tbl_ChiTietPhieuXuat.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Lưu file
            FileOutputStream fileOut = new FileOutputStream("ChiTietPhieuNhap.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Lammoi;
    private javax.swing.JButton btn_SuaCTPX;
    private javax.swing.JButton btn_SuaPX;
    private javax.swing.JButton btn_ThemCTPX;
    private javax.swing.JButton btn_ThemPX;
    private javax.swing.JButton btn_TimCTPX;
    private javax.swing.JButton btn_TimPX;
    private javax.swing.JButton btn_XemCT;
    private javax.swing.JButton btn_XoaCTPX;
    private javax.swing.JButton btn_XoaPX;
    private javax.swing.JButton btn_XuatExcel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnPhieuXuat;
    private javax.swing.JPanel pnPhieuXuatCT;
    private javax.swing.JTable tbl_ChiTietPhieuXuat;
    private javax.swing.JTable tbl_PhieuXuat;
    private javax.swing.JTextField txt_MaKho;
    private javax.swing.JTextField txt_MaPhieuXuatCTPX;
    private javax.swing.JTextField txt_MaPhongBan;
    private javax.swing.JTextField txt_MaVatTu;
    private com.toedter.calendar.JDateChooser txt_NgayXuat;
    private javax.swing.JTextField txt_SoLuong;
    private javax.swing.JTextField txt_TimKiemCTPX;
    private javax.swing.JTextField txt_TimKiemPX;
    // End of variables declaration//GEN-END:variables
}
