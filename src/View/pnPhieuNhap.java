package view;

import dao.DAO_PHIEUNHAP;
import Model.PhieuNhap;
import View.jfXemChiTietPhieuNhap;
import com.formdev.flatlaf.FlatClientProperties;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import dao.DAO_CTPHIEUNHAP;
import Model.ChiTietPhieuNhapKho;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;

public class pnPhieuNhap extends javax.swing.JPanel {

    private DefaultTableModel tableModel;
    private DefaultTableModel tableModel1;
    private DefaultTableModel tbl_ModelPNAndCTPN;

    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public pnPhieuNhap() {
        initComponents();
        tableModel = (DefaultTableModel) tbl_phieunhap.getModel();
        fillToTablePN();
        tableModel1 = (DefaultTableModel) tbl_chitietpn.getModel();
        fillToTableCTPN();
        codesize();
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

    public void clickHerePN() {

        int row = tbl_phieunhap.getSelectedRow(); // Lấy chỉ số dòng được chọn
        if (row != -1) {
            try {
                String ngayNhapStr = tbl_phieunhap.getValueAt(row, 1).toString();
                txt_kho.setText(tbl_phieunhap.getValueAt(row, 2).toString());
                txt_Mancc1.setText(tbl_phieunhap.getValueAt(row, 3).toString());
                // Chuyển đổi ngày từ chuỗi sang đối tượng Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date ngayNhap = sdf.parse(ngayNhapStr);
                jDateChooser1.setDate(ngayNhap);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    public void clickHerePNCT() {
        int row = tbl_chitietpn.getSelectedRow();
        if (row != -1) {
            try {
                txt_maphieunhap2.setText(tbl_chitietpn.getValueAt(row, 0).toString());
                txt_mavt.setText(tbl_chitietpn.getValueAt(row, 1).toString());
                int soLuong = Integer.parseInt(tbl_chitietpn.getValueAt(row, 2).toString());
                txt_soluong.setText(String.valueOf(soLuong));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void fillToTablePN() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<PhieuNhap> phieuNhapList = DAO_PHIEUNHAP.getAllPhieuNhap();
        for (PhieuNhap pn : phieuNhapList) {
            tableModel.addRow(new Object[]{pn.getMaPhieuNhap(), pn.getNgayNhap(), pn.getMaKho(), pn.getMaNhaCungCap()});
        }
    }

    private void fillToTableCTPN() {
        tableModel1.setRowCount(0); // Xóa dữ liệu cũ
        List<ChiTietPhieuNhapKho> ctphieuNhapList = DAO_CTPHIEUNHAP.getAllCtphieunhap();
        for (ChiTietPhieuNhapKho ctpnk : ctphieuNhapList) {
            tableModel1.addRow(new Object[]{ctpnk.getMaPhieuNhap(), ctpnk.getMaVatTu(), ctpnk.getSoLuong()});
        }
    }

    private void addPhieuNhap() {
        date = jDateChooser1.getDate();
        if (sdf.format(date).isEmpty()
                || txt_kho.getText().isEmpty()
                || txt_Mancc1.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            PhieuNhap pn = new PhieuNhap();
            pn.setNgayNhap(String.valueOf(sdf.format(date)));
            pn.setMaKho(String.valueOf(txt_kho.getText()));
            pn.setMaNhaCungCap(String.valueOf(txt_Mancc1.getText()));

            boolean check = DAO_PHIEUNHAP.insertPhieuNhap(pn);

            if (check) {
                fillToTablePN(); // Cập nhật bảng
                JOptionPane.showMessageDialog(this, "Thêm phiếu nhập thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phiếu nhập thất bại, vui lòng kiểm tra lại thông tin nhập vào!");
                return;
            }

        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void addPhieuNhapct() {
        // Kiểm tra dữ liệu đầu vào
        if (txt_maphieunhap2.getText().isEmpty()
                || txt_mavt.getText().isEmpty() || txt_soluong.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // Lấy dữ liệu từ giao diện
            String maPhieuNhap = txt_maphieunhap2.getText().trim();
            String maVatTu = txt_mavt.getText().trim();
            int soLuong = Integer.parseInt(txt_soluong.getText().trim());

            // Kiểm tra trùng lặp trước khi thêm
            if (DAO_CTPHIEUNHAP.isCTPhieuNhapExists(maPhieuNhap, maVatTu)) {
                JOptionPane.showMessageDialog(this,
                        "Chi tiết phiếu nhập với mã phiếu nhập '" + maPhieuNhap
                        + "' và mã vật tư '" + maVatTu + "' đã tồn tại. Vui lòng kiểm tra lại!",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo đối tượng chi tiết phiếu nhập
            ChiTietPhieuNhapKho ctpnk = new ChiTietPhieuNhapKho(maPhieuNhap, maVatTu, soLuong);

            // Gọi DAO để thêm
            boolean success = DAO_CTPHIEUNHAP.insertCTPhieuNhap(ctpnk);

            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm chi tiết phiếu nhập thành công!");
                fillToTableCTPN(); // Làm mới bảng chi tiết phiếu nhập
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại. Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: Số lượng phải là số nguyên hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteCTPhieuNhap() {
        int selectedRow = tbl_chitietpn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chi tiết phiếu nhập để xóa!");
            return;
        }

        // Lấy mã phiếu nhập và mã vật tư từ dòng được chọn
        String maPhieuNhap = tbl_chitietpn.getValueAt(selectedRow, 0).toString();
        String maVatTu = tbl_chitietpn.getValueAt(selectedRow, 1).toString();

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa chi tiết phiếu nhập này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Gọi DAO để xóa chi tiết phiếu nhập
                boolean success = DAO_CTPHIEUNHAP.deleteCTPhieuNhap(maPhieuNhap, maVatTu);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa chi tiết phiếu nhập thành công!");
                    fillToTableCTPN(); // Cập nhật lại bảng chi tiết phiếu nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa chi tiết phiếu nhập. Vui lòng thử lại!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chi tiết phiếu nhập: " + e.getMessage());
            }
        }
    }

    private void updateVatTu() {
        int selectedRow = tbl_phieunhap.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để cập nhật!");
            return;
        }

        // Lấy dữ liệu từ các trường nhập liệu
        String maPhieuNhap = tbl_phieunhap.getValueAt(selectedRow, 0).toString(); // Lấy mã phiếu nhập chính xác
        String ngayNhap = sdf.format(jDateChooser1.getDate());
        String maKho = txt_kho.getText().trim();
        String maNhaCungCap = txt_Mancc1.getText().trim();

        if (ngayNhap.isEmpty() || maKho.isEmpty() || maNhaCungCap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin trước khi cập nhật!");
            return;
        }

        try {
            // Tạo đối tượng phiếu nhập
            PhieuNhap pn = new PhieuNhap();
            //     pn.setMaPhieuNhap(maPhieuNhap); // Đảm bảo mã phiếu nhập được truyền vào đúng
            pn.setNgayNhap(ngayNhap);
            pn.setMaKho(maKho);
            pn.setMaNhaCungCap(maNhaCungCap);
            pn.setMaPhieuNhap(maPhieuNhap);
            // Cập nhật dữ liệu
            DAO_PHIEUNHAP.updatePhieuNhap(pn);
            fillToTablePN(); // Làm mới bảng
            JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phiếu nhập: " + e.getMessage());
        }
    }
//

    private void deletePhieuNhap() {
        int selectedRow = tbl_phieunhap.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để xóa!");
            return;
        }

        // Lấy mã phiếu nhập từ dòng được chọn
        String maPhieuNhap = tbl_phieunhap.getValueAt(selectedRow, 0).toString();

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa phiếu nhập với mã \"" + maPhieuNhap + "\" không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // Nếu người dùng xác nhận
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Gọi DAO để xóa phiếu nhập
                boolean success = DAO_PHIEUNHAP.deletePhieuNhap(maPhieuNhap);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thành công!");
                    fillToTablePN(); // Cập nhật lại bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Phiếu nhập đang được tham chiếu, không thể xóa!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiếu nhập: " + e.getMessage());
            }
        }
    }

    private void updateCTPhieuNhap() {
        if (txt_maphieunhap2.getText().isEmpty()
                || txt_mavt.getText().isEmpty()
                || txt_soluong.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;

        }

        try {

            ChiTietPhieuNhapKho ctpnk = new ChiTietPhieuNhapKho();
            ctpnk.setMaPhieuNhap(String.valueOf(txt_maphieunhap2.getText().trim()));
            ctpnk.setMaVatTu(String.valueOf(txt_mavt.getText().trim()));
            ctpnk.setSoLuong(Integer.valueOf(txt_soluong.getText().trim()));

            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn sửa chi tiết phiếu nhập " + ctpnk.getMaPhieuNhap() + " này không?", "Xác nhận sửa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dao.DAO_CTPHIEUNHAP.updateChiTietPhieuNhap(ctpnk);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Cập nhật chi tiết phiếu xuất " + ctpnk.getMaPhieuNhap() + " thành công!");
                    fillToTableCTPN(); // Cập nhật bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật chi tiết phiếu xuất " + ctpnk.getMaPhieuNhap() + " thất bại!");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Đã hủy sửa phiếu xuất.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void searchPhieuNhap() throws ParseException {

        String timMaPhieuNhap_PN = String.valueOf(txt_timpn.getText().trim());
        if (timMaPhieuNhap_PN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu nhập để tìm!");
            return;
        }
        List<PhieuNhap> result = DAO_PHIEUNHAP.searchWithMaPhieuNhap_PN(timMaPhieuNhap_PN);
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập với mã: " + timMaPhieuNhap_PN);
            // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
            tableModel.setRowCount(0); // Xóa sạch dữ liệu trong bảng
        } else {
            tableModel.setRowCount(0);// Xóa dữ liệu cũ trong bảng
            // Hiển thị thông tin lên các trường nhập liệu
            for (PhieuNhap pn : result) {
                // Cập nhật bảng chỉ với kết quả tìm kiếm

                tableModel.addRow(new Object[]{
                    pn.getMaPhieuNhap(),
                    pn.getNgayNhap(),
                    pn.getMaKho(),
                    pn.getMaNhaCungCap()
                });
            }
        }

    }

    private void viewChiTietPhieuNhapInPN() {
        int index = tbl_phieunhap.getSelectedRow();

        if (index < 0 || index == -1) {
            JOptionPane.showMessageDialog(null, "Bạn phải chọn 1 dòng dữ liệu để xem chi tiết !");
            return;
        }

        String xemChiTietPhieuNhap_PN = (String) tbl_phieunhap.getValueAt(index, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xem chi tiết phiếu nhập này không?", "Xác nhận xem chi tiết", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Object[]> result = DAO_PHIEUNHAP.viewWithMaPhieuNhap_PN_InPN(xemChiTietPhieuNhap_PN);

            if (result.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu xuất với mã: " + xemChiTietPhieuNhap_PN);
                // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
                tbl_ModelPNAndCTPN.setRowCount(0); // Xóa sạch dữ liệu trong bảng
            } else {

                jfXemChiTietPhieuNhap viewFrame = new jfXemChiTietPhieuNhap();
                viewFrame.filToTablePNAndCTPN(result);
                viewFrame.setVisible(true);
                viewFrame.setLocationRelativeTo(null);

            }
        }
    }

    private void searchChiTietPhieuNhap() throws ParseException {

        String timMaPhieuNhap_CTPN = String.valueOf(txt_timpnct.getText().trim());
        if (timMaPhieuNhap_CTPN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu xuất để tìm!");
            return;
        }
        List<ChiTietPhieuNhapKho> result = DAO_CTPHIEUNHAP.searchWithMaPhieuNhap_CTPN(timMaPhieuNhap_CTPN);
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu xuất với mã: " + timMaPhieuNhap_CTPN);
            // Xóa sạch các trường nhập liệu và bảng nếu không tìm thấy
            tableModel1.setRowCount(0); // Xóa sạch dữ liệu trong bảng
        } else {
            tableModel1.setRowCount(0);// Xóa dữ liệu cũ trong bảng
            // Hiển thị thông tin lên các trường nhập liệu
            for (ChiTietPhieuNhapKho ctpnk : result) {
                // Cập nhật bảng chỉ với kết quả tìm kiếm

                tableModel1.addRow(new Object[]{
                    ctpnk.getMaPhieuNhap(),
                    ctpnk.getMaVatTu(),
                    ctpnk.getSoLuong()
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPhieuNhap = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_kho = new javax.swing.JTextField();
        txt_Mancc1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_phieunhap = new javax.swing.JTable();
        txt_timpn = new javax.swing.JTextField();
        btn_TimPN = new javax.swing.JButton();
        btn_ThemPX = new javax.swing.JButton();
        btn_XoaPX = new javax.swing.JButton();
        btn_SuaPX = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        pnPhieuNhapCT = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btn_TimCTPN = new javax.swing.JButton();
        txt_maphieunhap2 = new javax.swing.JTextField();
        txt_mavt = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_chitietpn = new javax.swing.JTable();
        txt_timpnct = new javax.swing.JTextField();
        btn_ThemCTPX = new javax.swing.JButton();
        btn_XoaCTPX = new javax.swing.JButton();
        btn_SuaCTPX = new javax.swing.JButton();
        btn_Lammoi = new javax.swing.JButton();
        btn_XemCT = new javax.swing.JButton();
        btn_XuatExcel = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_soluong = new javax.swing.JTextField();

        pnPhieuNhap.setPreferredSize(new java.awt.Dimension(0, 300));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel2.setText("Phiếu Nhập");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Ngày Nhập :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Mã Kho :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Mã Nhà Cung Cấp :");

        tbl_phieunhap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Phiếu Nhập", "Ngày Nhập", "Mã Kho", "Mã Nhà Cung Cấp"
            }
        ));
        tbl_phieunhap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_phieunhapMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_phieunhap);

        btn_TimPN.setText("Tìm Kiếm");
        btn_TimPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TimPNActionPerformed(evt);
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

        javax.swing.GroupLayout pnPhieuNhapLayout = new javax.swing.GroupLayout(pnPhieuNhap);
        pnPhieuNhap.setLayout(pnPhieuNhapLayout);
        pnPhieuNhapLayout.setHorizontalGroup(
            pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_kho, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addComponent(txt_Mancc1)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(btn_ThemPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_XoaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_SuaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(btn_TimPN, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_timpn, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(135, 135, 135))))
        );
        pnPhieuNhapLayout.setVerticalGroup(
            pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_timpn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_TimPN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnPhieuNhapLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_kho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txt_Mancc1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(pnPhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_ThemPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XoaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_SuaPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel6.setText("Phiếu Nhập Chi Tiết");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Mã Phiếu Nhập :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Mã Vật Tư :");

        btn_TimCTPN.setText("Tìm Kiếm");
        btn_TimCTPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TimCTPNActionPerformed(evt);
            }
        });

        tbl_chitietpn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Phiếu Nhập", "Mã Vật Tư", "Số Lượng"
            }
        ));
        tbl_chitietpn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_chitietpnMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_chitietpn);

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

        javax.swing.GroupLayout pnPhieuNhapCTLayout = new javax.swing.GroupLayout(pnPhieuNhapCT);
        pnPhieuNhapCT.setLayout(pnPhieuNhapCTLayout);
        pnPhieuNhapCTLayout.setHorizontalGroup(
            pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapCTLayout.createSequentialGroup()
                        .addComponent(btn_ThemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_XoaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_SuaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_maphieunhap2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_mavt))
                                .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_soluong))))
                        .addGap(18, 18, 18)))
                .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapCTLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_TimCTPN, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_timpnct, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapCTLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_Lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_XemCT, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_XuatExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        pnPhieuNhapCTLayout.setVerticalGroup(
            pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPhieuNhapCTLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txt_maphieunhap2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txt_mavt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txt_soluong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ThemCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_XoaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_SuaCTPX, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPhieuNhapCTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_timpnct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_TimCTPN))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnPhieuNhapCTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XemCT, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_XuatExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPhieuNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
            .addComponent(pnPhieuNhapCT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnPhieuNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnPhieuNhapCT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tbl_phieunhapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_phieunhapMouseClicked
        clickHerePN();
    }//GEN-LAST:event_tbl_phieunhapMouseClicked

    private void btn_TimPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TimPNActionPerformed
        try {
            searchPhieuNhap();
        } catch (ParseException ex) {
            Logger.getLogger(pnPhieuNhap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_TimPNActionPerformed

    private void btn_ThemPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemPXActionPerformed
        addPhieuNhap();
    }//GEN-LAST:event_btn_ThemPXActionPerformed

    private void btn_XoaPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XoaPXActionPerformed
        deletePhieuNhap();
    }//GEN-LAST:event_btn_XoaPXActionPerformed

    private void btn_SuaPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaPXActionPerformed
        updateVatTu();
    }//GEN-LAST:event_btn_SuaPXActionPerformed

    private void btn_TimCTPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TimCTPNActionPerformed
        try {
            // TODO add your handling code here:
            searchChiTietPhieuNhap();
        } catch (ParseException ex) {
            Logger.getLogger(pnPhieuNhap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_TimCTPNActionPerformed

    private void tbl_chitietpnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_chitietpnMouseClicked
        clickHerePNCT();
    }//GEN-LAST:event_tbl_chitietpnMouseClicked

    private void btn_ThemCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemCTPXActionPerformed
        addPhieuNhapct();
    }//GEN-LAST:event_btn_ThemCTPXActionPerformed

    private void btn_XoaCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XoaCTPXActionPerformed
        deleteCTPhieuNhap();
    }//GEN-LAST:event_btn_XoaCTPXActionPerformed

    private void btn_SuaCTPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaCTPXActionPerformed
        updateCTPhieuNhap();
    }//GEN-LAST:event_btn_SuaCTPXActionPerformed

    private void btn_XemCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XemCTActionPerformed
        viewChiTietPhieuNhapInPN();
    }//GEN-LAST:event_btn_XemCTActionPerformed

    private void btn_LammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LammoiActionPerformed
        fillToTableCTPN();
        fillToTablePN();
        jDateChooser1.setDate(null);
        txt_kho.setText(null);
        txt_Mancc1.setText(null);
        txt_maphieunhap2.setText(null);
        txt_mavt.setText(null);
        txt_soluong.setText(null);
        txt_timpnct.setText(null);
        txt_timpn.setText(null);
    }//GEN-LAST:event_btn_LammoiActionPerformed

    private void btn_XuatExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XuatExcelActionPerformed
        exportToExcel();
    }//GEN-LAST:event_btn_XuatExcelActionPerformed

    private void exportToExcel() {
        try {
            // Tạo Workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("ChiTietPhieuNhap");

            // Tạo tiêu đề cột
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tbl_chitietpn.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tbl_chitietpn.getColumnName(i));
            }

            // Ghi dữ liệu từ bảng vào file Excel
            for (int i = 0; i < tbl_chitietpn.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tbl_chitietpn.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = tbl_chitietpn.getValueAt(i, j);
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
    private javax.swing.JButton btn_TimCTPN;
    private javax.swing.JButton btn_TimPN;
    private javax.swing.JButton btn_XemCT;
    private javax.swing.JButton btn_XoaCTPX;
    private javax.swing.JButton btn_XoaPX;
    private javax.swing.JButton btn_XuatExcel;
    private com.toedter.calendar.JDateChooser jDateChooser1;
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
    private javax.swing.JPanel pnPhieuNhap;
    private javax.swing.JPanel pnPhieuNhapCT;
    private javax.swing.JTable tbl_chitietpn;
    private javax.swing.JTable tbl_phieunhap;
    private javax.swing.JTextField txt_Mancc1;
    private javax.swing.JTextField txt_kho;
    private javax.swing.JTextField txt_maphieunhap2;
    private javax.swing.JTextField txt_mavt;
    private javax.swing.JTextField txt_soluong;
    private javax.swing.JTextField txt_timpn;
    private javax.swing.JTextField txt_timpnct;
    // End of variables declaration//GEN-END:variables
}
