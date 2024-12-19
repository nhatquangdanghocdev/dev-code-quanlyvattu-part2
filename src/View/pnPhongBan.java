package view;

import dao.DAO_PHONGBAN;
import Model.PhongBan;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class pnPhongBan extends javax.swing.JPanel {

    private DefaultTableModel tableModel;

    public pnPhongBan() {
        initComponents();
        tableModel = (DefaultTableModel) tbl_Phongban.getModel();
        fillToTablePB();
    }

    private void fillToTablePB() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<PhongBan> PhongList = DAO_PHONGBAN.getAllPhongban();
        for (PhongBan pb : PhongList) {
            tableModel.addRow(new Object[]{pb.getMaPB(), pb.getTenPB(), pb.getDiachi(), pb.getSoDienThoai(), pb.getEmail()});
        }
    }

    // Phương thức thêm Phòng Ban
    private void addPhongBan() {
        // Kiểm tra nếu các trường thông tin bị bỏ trống
        if (txt_Tenpb.getText().isEmpty()
                || txt_diachi.getText().isEmpty()
                || txt_sdt.getText().isEmpty()
                || txt_email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String email = txt_email.getText().trim();
        String sdt = txt_sdt.getText().trim();

        // Kiểm tra định dạng số điện thoại (giả sử số điện thoại phải là 10 chữ số)
        if (!sdt.matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không đúng định dạng! Vui lòng nhập lại.");
            return;
        }

        // Kiểm tra email có trùng không
        if (DAO_PHONGBAN.checkEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email đã tồn tại trong hệ thống!");
            return;
        }

        try {
            // Tạo đối tượng PhongBan mới
            PhongBan pb = new PhongBan();
            pb.setTenPB(txt_Tenpb.getText().trim());
            pb.setDiachi(txt_diachi.getText().trim());
            pb.setSoDienThoai(txt_sdt.getText().trim());
            pb.setEmail(txt_email.getText().trim());

            // Thêm phòng ban vào cơ sở dữ liệu
            boolean check = DAO_PHONGBAN.insertIntoPHONGBAN(pb);

            if (check) {
                fillToTablePB(); // Cập nhật bảng hiển thị
                JOptionPane.showMessageDialog(this, "Thêm phòng ban thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phòng ban thất bại, vui lòng kiểm tra lại thông tin nhập vào!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    // Phương thức cập nhật vật tư
// Phương thức cập nhật phòng ban
    private void updatePhongBan() {
        // Lấy dòng được chọn trong bảng
        int index = tbl_Phongban.getSelectedRow();

        // Kiểm tra nếu chưa chọn dòng nào
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn 1 dòng để cập nhật!");
            return;
        }

        // Kiểm tra thông tin đầu vào
        if (txt_Tenpb.getText().isEmpty() || txt_diachi.getText().isEmpty()
                || txt_sdt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // Lấy mã phòng ban từ dòng được chọn
            String maPhongBan = (String) tbl_Phongban.getValueAt(index, 0); // Cột 0 chứa mã phòng ban

            // Tìm phòng ban trong cơ sở dữ liệu theo mã
            PhongBan pb = DAO_PHONGBAN.searchPhongBanByMa(maPhongBan);
            if (pb == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng ban có mã: " + maPhongBan);
                return;
            }

            // Cập nhật thông tin phòng ban từ các trường nhập liệu
            pb.setTenPB(txt_Tenpb.getText().trim());
            pb.setDiachi(txt_diachi.getText().trim());
            pb.setSoDienThoai(txt_sdt.getText().trim());

            // Kiểm tra định dạng số điện thoại (phải là 10 chữ số)
            if (!pb.getSoDienThoai().matches("^\\d{10}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không đúng định dạng! Vui lòng nhập lại.");
                return;
            }

            // Hiển thị hộp thoại xác nhận cập nhật
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật thông tin phòng ban này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Thực hiện cập nhật trong cơ sở dữ liệu
                boolean check = DAO_PHONGBAN.updatePhg(pb);
                if (check) {
                    JOptionPane.showMessageDialog(this, "Cập nhật phòng ban thành công!");
                    fillToTablePB(); // Làm mới bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật phòng ban thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hủy cập nhật phòng ban.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deletePhongBan() {
        // Lấy dòng được chọn trong bảng
        int index = tbl_Phongban.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng ban để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Lấy mã phòng ban từ cột đầu tiên
            String maPhongBan = (String) tbl_Phongban.getValueAt(index, 0);

            if (maPhongBan == null || maPhongBan.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phòng ban không hợp lệ. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Hiển thị hộp thoại xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa phòng ban có mã: " + maPhongBan + "?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi DAO để thực hiện xóa
                boolean success = DAO_PHONGBAN.deletePhog(maPhongBan);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa phòng ban thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    fillToTablePB(); // Cập nhật lại bảng sau khi xóa
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa phòng ban vì nó đang được tham chiếu trong Phiếu Xuất.\n"
                            + "Vui lòng xóa các phiếu xuất liên quan trước!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi khi xóa phòng ban: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Hiển thị chi tiết lỗi trong console
        }
    }

    public void clickHerePB() {
        int row = tbl_Phongban.getSelectedRow(); // Lấy chỉ số dòng được chọn

        // Kiểm tra xem có dòng nào được chọn không
        if (row != -1) {
            try {
                // Lấy dữ liệu từ bảng và chuyển đổi kiểu dữ liệu tương ứng
                //String mapb = tbl_Phongban.getValueAt(row, 0).toString();
                String tenpb = tbl_Phongban.getValueAt(row, 1).toString();
                String diachi = tbl_Phongban.getValueAt(row, 2).toString();
                String sdt = tbl_Phongban.getValueAt(row, 3).toString();
                String email = tbl_Phongban.getValueAt(row, 4).toString();

                // Cập nhật các trường nhập liệu
                // txt_maPb.setText(String.valueOf(mapb)); // Hiển thị mã vật tư dưới dạng chuỗi
                txt_Tenpb.setText(tenpb);
                txt_diachi.setText(diachi);
                txt_sdt.setText(sdt);
                txt_email.setText(email);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void timPhongBan() {
        String keyword = txt_Hientimkiem.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        // Gọi DAO để tìm kiếm dữ liệu
        List<PhongBan> results = DAO_PHONGBAN.searchPhongBanByKeyword(keyword);

        // Kiểm tra kết quả và cập nhật vào bảng
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng ban với từ khóa: " + keyword);
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
        } else {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
            for (PhongBan pb : results) {
                tableModel.addRow(new Object[]{
                    pb.getMaPB(),
                    pb.getTenPB(),
                    pb.getDiachi(),
                    pb.getSoDienThoai(),
                    pb.getEmail()
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
        txt_Hientimkiem = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_Tenpb = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_Phongban = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txt_diachi = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_sdt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();

        jLabel1.setBackground(new java.awt.Color(191, 239, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Phòng Ban");

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
                .addComponent(txt_Hientimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Hientimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_Them, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_XOA, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_Sua, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_TIm, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Tên Phòng Ban :");

        tbl_Phongban.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Phòng Ban", "Tên Phòng Ban", "Địa Chỉ", "Số Điện Thoại", "E-mail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_Phongban.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_PhongbanMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_Phongban);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Địa Chỉ :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Số Điện Thoại :");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("E-mail :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_Tenpb, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_diachi, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_sdt, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(txt_Tenpb, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_diachi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_sdt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ThemActionPerformed
        addPhongBan();
    }//GEN-LAST:event_btn_ThemActionPerformed

    private void btn_XOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_XOAActionPerformed
        deletePhongBan();
    }//GEN-LAST:event_btn_XOAActionPerformed

    private void btn_SuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SuaActionPerformed
        updatePhongBan();
    }//GEN-LAST:event_btn_SuaActionPerformed

    private void btn_lammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoiActionPerformed
        fillToTablePB();
        txt_Tenpb.setText("");
        txt_diachi.setText("");
        txt_email.setText("");
        txt_Hientimkiem.setText("");
        txt_sdt.setText("");
    }//GEN-LAST:event_btn_lammoiActionPerformed

    private void btn_TImActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TImActionPerformed
        timPhongBan();
    }//GEN-LAST:event_btn_TImActionPerformed

    private void tbl_PhongbanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_PhongbanMouseClicked
        clickHerePB();
    }//GEN-LAST:event_tbl_PhongbanMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Sua;
    private javax.swing.JButton btn_TIm;
    private javax.swing.JButton btn_Them;
    private javax.swing.JButton btn_XOA;
    private javax.swing.JButton btn_lammoi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl_Phongban;
    private javax.swing.JTextField txt_Hientimkiem;
    private javax.swing.JTextField txt_Tenpb;
    private javax.swing.JTextField txt_diachi;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_sdt;
    // End of variables declaration//GEN-END:variables
}
