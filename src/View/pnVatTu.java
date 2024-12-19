package view;

import dao.DAO_VATTU;
import Model.VatTu;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class pnVatTu extends javax.swing.JPanel {

    private DefaultTableModel tableModel;

    public pnVatTu() {
        initComponents();
        tableModel = (DefaultTableModel) tbl_VatTu.getModel();
        fillToTableVT();
    }

    public void clickHereVT() {
        int row = tbl_VatTu.getSelectedRow(); // Lấy chỉ số dòng được chọn

        // Kiểm tra xem có dòng nào được chọn không
        if (row != -1) {
            try {
                // Lấy dữ liệu từ bảng và chuyển đổi kiểu dữ liệu tương ứng
                String maVatTu = tbl_VatTu.getValueAt(row, 0).toString(); // Lấy mã vật tư 
                String tenVatTu = tbl_VatTu.getValueAt(row, 1).toString(); // Lấy tên vật tư (String)
                String donViTinh = tbl_VatTu.getValueAt(row, 2).toString();
                Integer Soluong = Integer.parseInt(tbl_VatTu.getValueAt(row, 3).toString());
                String Makho = tbl_VatTu.getValueAt(row, 4).toString();

                // Cập nhật các trường nhập liệu
                txt_TenVT.setText(tenVatTu);
                txt_Donvitinh.setText(donViTinh);
                txt_SoLuongVT.setText(String.valueOf(Soluong));
                txt_MaKhoVT.setText(Makho);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void fillToTableVT() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<VatTu> vatTuList = DAO_VATTU.getAllVatTu();
        for (VatTu vt : vatTuList) {
            tableModel.addRow(new Object[]{vt.getMavattu(), vt.getTenVattu(), vt.getDonvitinh(), vt.getSoluong(), vt.getMaKho()});
        }
    }

    // Phương thức thêm vật tư
    private void addVatTu() {
        if (txt_TenVT.getText().isEmpty() || txt_Donvitinh.getText().isEmpty()
                || txt_SoLuongVT.getText().isEmpty() || txt_MaKhoVT.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String tenVT = txt_TenVT.getText().trim();
        String donViTinh = txt_Donvitinh.getText().trim();
        String soLuongStr = txt_SoLuongVT.getText().trim();
        String maKho = txt_MaKhoVT.getText().trim();

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không thể là số âm!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ!");
            return;
        }

        // Tạo đối tượng VatTu
        VatTu vt = new VatTu(null, tenVT, donViTinh, soLuong, maKho);

        // Gọi phương thức thêm từ DAO
        try {
            DAO_VATTU.insertVatTu(vt);
            JOptionPane.showMessageDialog(this, "Thêm vật tư thành công!");
            fillToTableVT(); // Làm mới bảng
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteVatTu() {
        int index = tbl_VatTu.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn một dòng để xóa!");
            return;
        }

        // Lấy mã vật tư từ bảng
        String maVatTu = (String) tbl_VatTu.getValueAt(index, 0);

        // Hiển thị hộp thoại xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa vật tư có mã: " + maVatTu + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = DAO_VATTU.deleteVatTu(maVatTu);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa vật tư thành công!");
                    fillToTableVT(); // Làm mới bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa vật tư: Đang được tham chiếu hoặc không tồn tại.");
                }
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void updateVatTu() {
        int index = tbl_VatTu.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn một dòng để sửa!");
            return;
        }

        // Lấy mã vật tư từ dòng được chọn
        String maVT = (String) tbl_VatTu.getValueAt(index, 0);

        // Kiểm tra các trường thông tin
        String tenVT = txt_TenVT.getText().trim();
        String donViTinh = txt_Donvitinh.getText().trim();
        String soLuongStr = txt_SoLuongVT.getText().trim();
        String maKho = txt_MaKhoVT.getText().trim();

        if (tenVT.isEmpty() || donViTinh.isEmpty() || soLuongStr.isEmpty() || maKho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không thể là số âm!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ!");
            return;
        }

        // Tạo đối tượng VatTu với mã không thay đổi
        VatTu vt = new VatTu(maVT, tenVT, donViTinh, soLuong, maKho);

        try {
            boolean success = DAO_VATTU.updateVatTu(vt);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật vật tư thành công!");
                fillToTableVT(); // Làm mới bảng
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy vật tư để cập nhật!");
            }
        } catch (RuntimeException e) {
            // Hiển thị thông báo lỗi từ DAO
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void timVatTu() {
        String keyword = txt_HienTimKiem.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        // Gọi DAO để tìm kiếm dữ liệu
        List<VatTu> results = DAO_VATTU.searchVatTuByKeyword(keyword);

        // Kiểm tra kết quả và cập nhật vào bảng
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vật tư với từ khóa: " + keyword);
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
        } else {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
            for (VatTu vt : results) {
                tableModel.addRow(new Object[]{
                    vt.getMavattu(),
                    vt.getTenVattu(),
                    vt.getDonvitinh(),
                    vt.getSoluong(),
                    vt.getMaKho()
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_Them = new javax.swing.JButton();
        btn_XOA = new javax.swing.JButton();
        btn_Sua = new javax.swing.JButton();
        btn_lammoi = new javax.swing.JButton();
        btn_TIm = new javax.swing.JButton();
        txt_HienTimKiem = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_VatTu = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txt_MaKhoVT = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_SoLuongVT = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_Donvitinh = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_TenVT = new javax.swing.JTextField();

        jLabel1.setBackground(new java.awt.Color(191, 239, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Vật Tư");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );

        btn_Them.setText("Thêm");
        btn_Them.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ThemActionPerformed(evt);
            }
        });

        btn_XOA.setText("Xóa");
        btn_XOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_XOAActionPerformed(evt);
            }
        });

        btn_Sua.setText("Sửa");
        btn_Sua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SuaActionPerformed(evt);
            }
        });

        btn_lammoi.setText("Làm mới");
        btn_lammoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoiActionPerformed(evt);
            }
        });

        btn_TIm.setText("Tìm Kiếm");
        btn_TIm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TImActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btn_Them, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_XOA, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_Sua, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_TIm, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_HienTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_HienTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_Them, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_XOA, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_Sua, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_TIm, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        tbl_VatTu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Vật Tư", "Tên Vật Tư", "Đơn Vị Tính", "Số Lượng", "Mã Kho"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_VatTu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_VatTuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_VatTu);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Mã Kho :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Số Lượng :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Đơn Vị Tính :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Tên Vật Tư :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_TenVT, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txt_MaKhoVT))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txt_SoLuongVT))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txt_Donvitinh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_TenVT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_Donvitinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_SoLuongVT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_MaKhoVT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 230, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemActionPerformed
        addVatTu();
    }//GEN-LAST:event_btn_ThemActionPerformed

    private void btn_XOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XOAActionPerformed
        deleteVatTu();
    }//GEN-LAST:event_btn_XOAActionPerformed

    private void btn_SuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaActionPerformed
        updateVatTu();
    }//GEN-LAST:event_btn_SuaActionPerformed

    private void btn_lammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoiActionPerformed
        fillToTableVT();
        txt_TenVT.setText("");
        txt_MaKhoVT.setText("");
        txt_Donvitinh.setText("");
        txt_HienTimKiem.setText("");
        txt_SoLuongVT.setText("");
    }//GEN-LAST:event_btn_lammoiActionPerformed

    private void btn_TImActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TImActionPerformed
        timVatTu();
    }//GEN-LAST:event_btn_TImActionPerformed

    private void tbl_VatTuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_VatTuMouseClicked
        clickHereVT();
    }//GEN-LAST:event_tbl_VatTuMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Sua;
    private javax.swing.JButton btn_TIm;
    private javax.swing.JButton btn_Them;
    private javax.swing.JButton btn_XOA;
    private javax.swing.JButton btn_lammoi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl_VatTu;
    private javax.swing.JTextField txt_Donvitinh;
    private javax.swing.JTextField txt_HienTimKiem;
    private javax.swing.JTextField txt_MaKhoVT;
    private javax.swing.JTextField txt_SoLuongVT;
    private javax.swing.JTextField txt_TenVT;
    // End of variables declaration//GEN-END:variables
}
