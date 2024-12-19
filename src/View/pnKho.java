package view;

import dao.DAO_KHO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import Model.Kho;   

public class pnKho extends javax.swing.JPanel {

    private DefaultTableModel tableModel;
    
    public pnKho() {
        initComponents();
        tableModel = (DefaultTableModel) tbl_Kho.getModel();
        fillToTable();
    }
    
    private void fillToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<Kho> khoList = DAO_KHO.getAllKho();
        for (Kho kh : khoList) {
            tableModel.addRow(new Object[]{kh.getMaKho(), kh.getTenKho(), kh.getDiachi()});

        }
    }

    public void clickHereKho() {
        int row = tbl_Kho.getSelectedRow(); // Lấy chỉ số dòng được chọn

        if (row != -1) {
            try {
                // Lấy dữ liệu từ bảng và chuyển đổi kiểu dữ liệu tương ứng
                //   String Makho = tbl_Kho.getValueAt(row, 0).toString(); // Lấy mã vật tư (int)
                String Tenkho = tbl_Kho.getValueAt(row, 1).toString(); // Lấy tên vật tư (String)
                String Diachi = tbl_Kho.getValueAt(row, 2).toString(); // Lấy đơn vị tính (Str
                //   txt_makho.setText(String.valueOf(Makho)); // Hiển thị mã vật tư dưới dạng chuỗi
                txt_tenkho.setText(String.valueOf(Tenkho));
                txt_diachi.setText(String.valueOf(Diachi));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng!");
        }
    }

    private void addKho() {
        if (txt_tenkho.getText().isEmpty() || txt_diachi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            Kho kho = new Kho();
            kho.setTenKho(txt_tenkho.getText());
            kho.setDiachi(txt_diachi.getText());

            boolean check = DAO_KHO.addKho(kho);

            if (check) {
                fillToTable(); // Cập nhật bảng
                JOptionPane.showMessageDialog(this, "Thêm kho thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm kho thất bại, vui lòng kiểm tra lại thông tin nhập vào!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    // Phương thức cập nhật vật tư
    private void updateKho() {
        // Lấy dòng được chọn trong bảng
        int index = tbl_Kho.getSelectedRow();

//    // Kiểm tra nếu chưa chọn dòng nào
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kho để cập nhật!");
            return;
        }

        // Kiểm tra thông tin đầu vào
        if (txt_tenkho.getText().isEmpty() || txt_diachi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // Lấy mã kho từ dòng được chọn
            String maKho = (String) tbl_Kho.getValueAt(index, 0); // Cột 0 chứa mã kho

            // Tìm kho trong cơ sở dữ liệu theo mã
            Kho kho = DAO_KHO.searchKhoByMa(maKho);
            if (kho == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kho có mã: " + maKho);
                return;
            }

            // Cập nhật thông tin kho từ các trường nhập liệu
            kho.setTenKho(txt_tenkho.getText().trim());
            kho.setDiachi(txt_diachi.getText().trim());

            // Hiển thị hộp thoại xác nhận cập nhật
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật thông tin kho này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Thực hiện cập nhật trong cơ sở dữ liệu
                boolean check = DAO_KHO.updateKho(kho);
                if (check) {
                    JOptionPane.showMessageDialog(this, "Cập nhật kho thành công!");
                    fillToTable(); // Làm mới bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật kho thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hủy cập nhật kho.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteKho() {
        // Lấy dòng được chọn trong bảng
        int index = tbl_Kho.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn 1 dòng để xóa!");
            return;
        }

        try {
            // Lấy mã kho từ cột 0 của dòng được chọn
            String maKho = (String) tbl_Kho.getValueAt(index, 0);

            // Hiển thị hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa kho: " + maKho + " không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi phương thức DAO để xóa kho
                boolean success = DAO_KHO.deleteKho(maKho);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa kho thành công!");
                    fillToTable(); // Cập nhật lại bảng
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa kho. Kho đang được tham chiếu hoặc có lỗi xảy ra.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void timkho() {
        String keyword = txt_TimKiem.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        List<Kho> results = DAO_KHO.searchWithKeyword(keyword);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm kho với từ khóa: " + keyword);
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
        } else {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
            for (Kho kho : results) {
                tableModel.addRow(new Object[]{kho.getMaKho(), kho.getTenKho(), kho.getDiachi()});
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        txt_tenkho = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_Kho = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txt_diachi = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_them = new javax.swing.JButton();
        btn_xoaKho = new javax.swing.JButton();
        btn_sua = new javax.swing.JButton();
        btn_lammoi = new javax.swing.JButton();
        btn_timkho = new javax.swing.JButton();
        txt_TimKiem = new javax.swing.JTextField();

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Tên Kho :");

        tbl_Kho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Kho", "Tên Kho", "Địa Chỉ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_Kho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_KhoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_Kho);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Địa Chỉ :");

        jLabel1.setBackground(new java.awt.Color(191, 239, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kho");

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

        btn_them.setText("Thêm");
        btn_them.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themActionPerformed(evt);
            }
        });

        btn_xoaKho.setText("Xóa");
        btn_xoaKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoaKhoActionPerformed(evt);
            }
        });

        btn_sua.setText("Sửa");
        btn_sua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_suaActionPerformed(evt);
            }
        });

        btn_lammoi.setText("Làm mới");
        btn_lammoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoiActionPerformed(evt);
            }
        });

        btn_timkho.setText("Tìm Kiếm");
        btn_timkho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_timkhoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_xoaKho, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_sua, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_timkho, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_TimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_TimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_xoaKho, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_sua, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_lammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_timkho, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_diachi, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_tenkho, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addContainerGap())
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
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_tenkho, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_diachi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tbl_KhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_KhoMouseClicked
        clickHereKho();
    }//GEN-LAST:event_tbl_KhoMouseClicked

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        addKho();
    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_xoaKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoaKhoActionPerformed
        deleteKho();
    }//GEN-LAST:event_btn_xoaKhoActionPerformed

    private void btn_suaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_suaActionPerformed
        updateKho();
    }//GEN-LAST:event_btn_suaActionPerformed

    private void btn_lammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoiActionPerformed
        fillToTable();
        txt_tenkho.setText("");
        txt_diachi.setText("");
        txt_TimKiem.setText("");
    }//GEN-LAST:event_btn_lammoiActionPerformed

    private void btn_timkhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_timkhoActionPerformed
        timkho();
    }//GEN-LAST:event_btn_timkhoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_lammoi;
    private javax.swing.JButton btn_sua;
    private javax.swing.JButton btn_them;
    private javax.swing.JButton btn_timkho;
    private javax.swing.JButton btn_xoaKho;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl_Kho;
    private javax.swing.JTextField txt_TimKiem;
    private javax.swing.JTextField txt_diachi;
    private javax.swing.JTextField txt_tenkho;
    // End of variables declaration//GEN-END:variables
}
