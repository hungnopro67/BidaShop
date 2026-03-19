/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bida.shop.ui.manager;

import database.Auth;
import database.JDBCUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class ThemCa extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    public ThemCa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadBanButtons();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void loadBanButtons() {
        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "SELECT MaBan, TenBan, TrangThaiBan FROM Ban";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String maBan = rs.getString("MaBan");
                String tenBan = rs.getString("TenBan");
                String trangThai = rs.getString("TrangThaiBan");

                JButton btn = new JButton("<html><center>" + tenBan + "<br>(" + maBan + ")</center></html>");
                btn.setPreferredSize(new Dimension(80, 80));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setFocusPainted(false);

                if ("Trống".equalsIgnoreCase(trangThai)) {
                    btn.setBackground(Color.BLUE);
                    btn.setForeground(Color.WHITE);
                    btn.addActionListener(e -> chonBan(maBan));
                } else if ("Hoạt động".equalsIgnoreCase(trangThai)) {
                    btn.setBackground(Color.GREEN);
                    btn.setForeground(Color.BLACK);
                    btn.setEnabled(false);
                } else {
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setEnabled(false);
                }

                pnlGrid.add(btn);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách bàn: " + e.getMessage());
        }
    }

//    private void chonBan(String maBan) {
//        try (Connection conn = JDBCUtil.getConnection()) {
//            // Insert ca choi moi
//            String sql = "INSERT INTO CaChoi (MaCaChoi, MaBan, ThoiGianBatDau, TrangThaiCaThuc) VALUES (?, ?, GETDATE(), N'Đang chơi')";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            String maCaChoi = "CC" + System.currentTimeMillis() % 100000;
//            ps.setString(1, maCaChoi);
//            ps.setString(2, maBan);
//            ps.executeUpdate();
//
//            // Cap nhat trang thai ban thanh "Hoạt động"
//            String updateSql = "UPDATE Ban SET TrangThaiBan = N'Hoạt động' WHERE MaBan = ?";
//            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
//            psUpdate.setString(1, maBan);
//            psUpdate.executeUpdate();
//
//            JOptionPane.showMessageDialog(this, "Thêm ca cho bàn " + maBan + " thành công!");
//            dispose();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi khi thêm ca: " + ex.getMessage());
//        }
//    }
    private void chonBan(String maBan) {
        try (Connection conn = JDBCUtil.getConnection()) {
            // Lấy mã nhân viên từ người dùng đăng nhập
            String maNV = database.Auth.user != null ? database.Auth.user.getMaNhanVien() : null;
            System.out.println(">> Nhân viên đang đăng nhập: " + (Auth.user != null ? Auth.user.getMaNhanVien(): "null"));

            // Nếu chưa đăng nhập, không cho thêm
            if (maNV == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không xác định được nhân viên đang đăng nhập!");
                return;
            }

            // Insert ca chơi mới
            String sql = "INSERT INTO CaChoi (MaCaChoi, MaBan, MaNVBatDau, ThoiGianBatDau, TrangThaiCaThuc) "
                       + "VALUES (?, ?, ?, GETDATE(), N'Đang chơi')";
            PreparedStatement ps = conn.prepareStatement(sql);
            String maCaChoi = "CC" + System.currentTimeMillis() % 100000;
            ps.setString(1, maCaChoi);
            ps.setString(2, maBan);
            ps.setString(3, maNV);;
            ps.executeUpdate();

            // Cập nhật trạng thái bàn
            String updateSql = "UPDATE Ban SET TrangThaiBan = N'Hoạt động' WHERE MaBan = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, maBan);
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm ca cho bàn " + maBan + " thành công!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm ca: " + ex.getMessage());
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGrid = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thêm ca");

        pnlGrid.setLayout(new java.awt.GridLayout(10, 5, 6, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(ThemCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThemCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThemCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThemCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ThemCa(null, true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlGrid;
    // End of variables declaration//GEN-END:variables
}

