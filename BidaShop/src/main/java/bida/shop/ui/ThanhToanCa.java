/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bida.shop.ui;

import bida.shop.ui.manager.QuanLyDoanhThuJDialog;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class ThanhToanCa extends javax.swing.JDialog  {

    private static final long serialVersionUID = 1L;

    /**
     * Creates new form ThanhToanCa
     */
    
    private String maBan;
    private String maCaChoi;
    private boolean isXemLai;

    public ThanhToanCa(java.awt.Frame parent, boolean modal, String maBan, String maCaChoi, boolean isXemLai) {
        super(parent, modal);   
        this.maBan = maBan;
        this.maCaChoi = maCaChoi;
        this.isXemLai = isXemLai;
        initComponents();
        loadThongTinThanhToan();
        if (isXemLai) {
            jButton1.setEnabled(false);
        }   
        setLocationRelativeTo(null);
    }

    private void loadThongTinThanhToan() {
        try (Connection conn = JDBCUtil.getConnection()) {
            // 1. Lấy thông tin ca chơi theo maCaChoi
            String sqlCa = "SELECT MaBan, ThoiGianBatDau, ThoiGianKetThuc FROM CaChoi WHERE MaCaChoi = ?";
            PreparedStatement psCa = conn.prepareStatement(sqlCa);
            psCa.setString(1, maCaChoi);
            ResultSet rsCa = psCa.executeQuery();

            Timestamp batDau = null, ketThuc = null;
            if (rsCa.next()) {
                maBan = rsCa.getString("MaBan");
                batDau = rsCa.getTimestamp("ThoiGianBatDau");
                ketThuc = rsCa.getTimestamp("ThoiGianKetThuc");
                if (ketThuc == null) {
                    ketThuc = new Timestamp(System.currentTimeMillis());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ca chơi!");
                return;
            }

            lblTenBan.setText("Bàn: " + maBan);

            // 2. Tính thời gian chơi
            long millis = ketThuc.getTime() - batDau.getTime();
            double soGio = millis / (1000.0 * 60 * 60);
            lblThoiGianChoi.setText(String.format("Thời gian chơi: %.2f giờ", soGio));

            // 2.1. Lấy giá giờ theo bàn
            double giaGio = 50000; // default
            String sqlGiaGio = "SELECT GiaBan FROM Ban WHERE MaBan = ?";
            PreparedStatement psGia = conn.prepareStatement(sqlGiaGio);
            psGia.setString(1, maBan);
            ResultSet rsGia = psGia.executeQuery();
            if (rsGia.next()) {
                giaGio = rsGia.getDouble("GiaBan");
            }
            rsGia.close();
            psGia.close();

            // 3. Tính tiền giờ theo giá bàn
            double tienGio = soGio * giaGio;

            // 4. Lấy danh sách đồ uống
            String sqlDoUong = "SELECT pd.MaDoUong, d.TenDoUong, pd.SoLuong, d.GiaBan, pd.ThanhTien " +
                               "FROM PhieuDoUong pd JOIN DoUong d ON pd.MaDoUong = d.MaDoUong " +
                               "WHERE pd.MaCaChoi = ?";
            PreparedStatement psDU = conn.prepareStatement(sqlDoUong);
            psDU.setString(1, maCaChoi);
            ResultSet rsDU = psDU.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblDoUong.getModel();
            model.setRowCount(0);
            double tongTienDoUong = 0;

            while (rsDU.next()) {
                Object[] row = {
                    rsDU.getString("MaDoUong"),
                    rsDU.getString("TenDoUong"),
                    rsDU.getInt("SoLuong"),
                    rsDU.getDouble("GiaBan"),
                    rsDU.getDouble("ThanhTien")
                };
                tongTienDoUong += rsDU.getDouble("ThanhTien");
                model.addRow(row);
            }

            double tongTienCa = tienGio + tongTienDoUong;
            DecimalFormat df = new DecimalFormat("#,###");

            lblTienGio.setText("Tiền giờ: " + df.format(tienGio) + " VND");
            lblTienDoUong.setText("Tiền đồ uống: " + df.format(tongTienDoUong) + " VND");
            lblTongTien.setText("Tổng tiền: " + df.format(tongTienCa) + " VND");

            // Cập nhật tổng tiền nếu chưa kết thúc ca
            if (!isXemLai) {
                String sqlUpdate = "UPDATE CaChoi SET TongTienCa = ? WHERE MaCaChoi = ?";
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, tongTienCa);
                psUpdate.setString(2, maCaChoi);
                psUpdate.executeUpdate();
                psUpdate.close();
            }

            rsCa.close(); rsDU.close();
            psCa.close(); psDU.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải thông tin thanh toán: " + e.getMessage());
        }
    }

    private void thanhToan() {
        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "UPDATE CaChoi SET ThoiGianKetThuc = GETDATE(), TrangThaiCaThuc = N'Kết thúc' "
                       + "WHERE MaBan = ? AND TrangThaiCaThuc = N'Đang chơi'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.executeUpdate();

            String sqlUpdateBan = "UPDATE Ban SET TrangThaiBan = N'Trống' WHERE MaBan = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlUpdateBan);
            ps2.setString(1, maBan);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        lblTenBan = new javax.swing.JLabel();
        lblThoiGianChoi = new javax.swing.JLabel();
        lblTienGio = new javax.swing.JLabel();
        lblTienDoUong = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDoUong = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thanh toán");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTenBan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTenBan.setForeground(new java.awt.Color(255, 51, 51));
        lblTenBan.setText("Tên bàn");

        lblThoiGianChoi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblThoiGianChoi.setText("Thời gian chơi");

        lblTienGio.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTienGio.setText("Tiền giờ");

        lblTienDoUong.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTienDoUong.setText("Tiền đồ uống");

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongTien.setForeground(new java.awt.Color(255, 0, 0));
        lblTongTien.setText("Tổng tiền");

        tblDoUong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã đồ uống", "Tên", "Số lượng", "Đơn giá", "Tổng"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblDoUong);

        jButton1.setBackground(new java.awt.Color(0, 204, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doanhthu.png"))); // NOI18N
        jButton1.setText("Thanh Toán");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblThoiGianChoi)
                            .addComponent(lblTenBan)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblTienGio)
                                .addGap(102, 102, 102)
                                .addComponent(lblTienDoUong)))
                        .addGap(0, 492, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(lblTongTien)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(146, 146, 146))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTenBan)
                .addGap(18, 18, 18)
                .addComponent(lblThoiGianChoi)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTienGio)
                    .addComponent(lblTienDoUong))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(lblTongTien))
                .addGap(47, 47, 47))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        thanhToan();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(ThanhToanCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThanhToanCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThanhToanCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThanhToanCa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 //new ThanhToanCa(null, true, "B001").setVisible(true);
                  new ThanhToanCa(null, true, "B001", "CC001", false).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTenBan;
    private javax.swing.JLabel lblThoiGianChoi;
    private javax.swing.JLabel lblTienDoUong;
    private javax.swing.JLabel lblTienGio;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JTable tblDoUong;
    // End of variables declaration//GEN-END:variables
}
