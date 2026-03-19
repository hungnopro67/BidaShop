/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bida.shop.ui.manager;

import bida.shop.dao.BanDAO;
import bida.shop.entity.Ban;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class QuanLyBanJDialog extends javax.swing.JInternalFrame{

    private static final long serialVersionUID = 1L;
    private List<Ban> danhSachBan = new ArrayList<>();
    /**
     * Creates new form QuanLyBanJDialog
     */
    public QuanLyBanJDialog() {
        initComponents();      
    }
    
    public void open(){
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadDataToTable();
    }
    
    void loadDataToTable() {
        DefaultTableModel model = (DefaultTableModel) tblBan.getModel();
        model.setRowCount(0); // xóa dữ liệu cũ

        model.setColumnIdentifiers(new String[]{"Mã bàn", "Tên bàn", "Giá giờ chơi", "Trạng thái"});
        BanDAO dao = new BanDAO();
        List<Ban> list = dao.selectAll();
        danhSachBan = list; // GÁN DỮ LIỆU VÀO DANH SÁCH GỐC DÙNG ĐỂ LỌC

        for (Ban ban : list) {
            Object[] row = {
                ban.getMaBan(),
                ban.getTenBan(),
                ban.getGiaGioChoi(),
                ban.getTrangThai()
            };
            model.addRow(row);
        }danhSachBan = list;
    }
    

    public void sua(){
        int row = tblBan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một bàn để sửa.");
            return;
        }

        // Lấy dữ liệu từ bảng
        String maBan = tblBan.getValueAt(row, 0).toString();
        String tenBan = tblBan.getValueAt(row, 1).toString();
        String giaStr = tblBan.getValueAt(row, 2).toString().replace(",", "").trim();
        double giaGio = Double.parseDouble(giaStr);
        String trangThai = tblBan.getValueAt(row, 3).toString();

        // Hiển thị hộp thoại nhập mới
        String newTen = JOptionPane.showInputDialog(null, "Sửa tên bàn:", tenBan);
        if (newTen == null || newTen.trim().isEmpty()) return;

        String newGiaStr = JOptionPane.showInputDialog(null, "Sửa giá giờ:", giaGio);
        if (newGiaStr == null || newGiaStr.trim().isEmpty()) return;
        double newGia = Double.parseDouble(newGiaStr);

        String[] options = {"Hoạt động","Trống", "Bảo trì"};
        String newTrangThai = (String) JOptionPane.showInputDialog(null, "Chọn trạng thái:",
                "Trạng thái", JOptionPane.QUESTION_MESSAGE, null, options, trangThai);
        if (newTrangThai == null) return;

        // Cập nhật vào DB
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE Ban SET TenBan = ?, GiaBan = ?, TrangThaiBan = ? WHERE MaBan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newTen);
            ps.setDouble(2, newGia);
            ps.setString(3, newTrangThai);
            ps.setString(4, maBan);
            ps.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(null, "Cập nhật bàn thành công!");
            loadDataToTable();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }
   
    public void xoa(){
        int row = tblBan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một bàn để xóa.");
            return;
        }

        String maBan = tblBan.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(null,
                "Bạn có chắc chắn muốn xóa bàn " + maBan + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = JDBCUtil.getConnection();
                String sql = "DELETE FROM Ban WHERE MaBan = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, maBan);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(null, "Xóa bàn thành công!");
                loadDataToTable();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }
    
   public void loadDataByStatus(String status) {
        DefaultTableModel model = (DefaultTableModel) tblBan.getModel();
        model.setRowCount(0); // Xoá dữ liệu cũ

        for (Ban ban : danhSachBan) {
            String banStatus = ban.getTrangThai(); // Trạng thái từ DB (có dấu)

            if (status.equals("Tất cả") || banStatus.equalsIgnoreCase(status)) {
                model.addRow(new Object[]{
                    ban.getMaBan(),
                    ban.getTenBan(),
                    ban.getGiaGioChoi(),
                    ban.getTrangThai()
                });
            }
        }
    }
    
    public static String removeDiacritics(String str) {
        str = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return str;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        cboTrangThai = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBan = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setTitle("Quản lý bàn");
        setPreferredSize(new java.awt.Dimension(868, 570));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(868, 570));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Hoạt động", "Bảo trì", "Trống" }));
        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });
        jPanel3.add(cboTrangThai, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 23, -1, -1));

        tblBan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã bàn", "Tên bàn", "Giá giờ chơi", "Trạng thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblBan);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 80, 846, 366));

        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });
        jPanel3.add(btnThem, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 507, -1, -1));

        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });
        jPanel3.add(btnSua, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 507, -1, -1));

        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });
        jPanel3.add(btnXoa, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 507, -1, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 860, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        ThemBan dialog = new ThemBan(null, true, QuanLyBanJDialog.this);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
        sua();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        xoa();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        // TODO add your handling code here:
        String selected = (String) cboTrangThai.getSelectedItem();
        loadDataByStatus(selected);
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QuanLyBanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLyBanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLyBanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLyBanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuanLyBanJDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblBan;
    // End of variables declaration//GEN-END:variables
}
