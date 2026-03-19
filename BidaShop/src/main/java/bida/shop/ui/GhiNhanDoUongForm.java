/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bida.shop.ui;

import bida.shop.entity.Drink;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class GhiNhanDoUongForm extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel modelDoUong, modelBill;
    private List<Drink> danhSachDoUong = new ArrayList<>();
    private double tongTien = 0;
    private int currentIndex = -1;
    private String maCaChoiHienTai = null; // Sẽ được set khi chọn bàn

    /**
     * Creates new form GhiNhanDoUongForm
     */
    public GhiNhanDoUongForm() {
        initComponents();
        modelDoUong = (DefaultTableModel) tblDoUong.getModel();
        modelBill = (DefaultTableModel) tblBill.getModel();
        loadDoUong();
        loadBanDangHoatDong();
    }

    public void open() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
    }

    void loadDoUong() {
        modelDoUong.setRowCount(0); // Xóa dữ liệu cũ

        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "SELECT MaDoUong, TenDoUong, GiaBan, SoLuong FROM DoUong"; // Thêm cột SoLuong
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ma = rs.getString("MaDoUong");
                String ten = rs.getString("TenDoUong");
                double gia = rs.getDouble("GiaBan");
                int soLuong = rs.getInt("SoLuong");

                // Hiển thị dữ liệu
                System.out.println(ma + " | " + ten + " | " + gia + " | " + soLuong);
                modelDoUong.addRow(new Object[]{ma, ten, gia, soLuong});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi load đồ uống: " + e.getMessage());
        }
    }

    private void loadBanDangHoatDong() {
        cboBan.removeAllItems(); // Xóa toàn bộ item cũ

        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "SELECT MaBan FROM CaChoi WHERE TrangThaiCaThuc = N'Đang chơi'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            boolean hasBan = false;
            while (rs.next()) {
                String maBan = rs.getString("MaBan");
                cboBan.addItem(maBan);
                hasBan = true;
            }

            if (!hasBan) {
                cboBan.addItem("Không có bàn hoạt động");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load bàn hoạt động: " + e.getMessage());
        }
    }

    void updateTongTien() {
        tongTien = 0;
        for (int i = 0; i < modelBill.getRowCount(); i++) {
            try {
                Object giaObj = modelBill.getValueAt(i, 3);
                double tien = (giaObj instanceof Double)
                        ? (Double) giaObj
                        : Double.parseDouble(giaObj.toString());
                tongTien += tien;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        lblTongTien.setText(String.format("%.0f đ", tongTien));
    }

    public void luuHoaDon() {
    if (modelBill.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Không có món nào để lưu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Lấy mã bàn từ combo box (giả sử item là "Item 1", "Bàn 1", hoặc "B001")
    String selectedItem = cboBan.getSelectedItem().toString().trim();
    String maBan = extractMaBan(selectedItem);
    if (maBan == null || maBan.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không thể xác định mã bàn. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection conn = null;
    try {
        conn = JDBCUtil.getConnection();
        conn.setAutoCommit(false);

        // Lấy MaCaChoi đang chơi
        String maCaChoi = getMaCaChoiDangChoi(conn, maBan);
        if (maCaChoi == null) {
            JOptionPane.showMessageDialog(this, "Bàn này không có ca chơi đang hoạt động!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xóa hết phiếu cũ của ca này để đồng bộ
        String sqlDelete = "DELETE FROM PhieuDoUong WHERE MaCaChoi = ?";
        try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
            psDel.setString(1, maCaChoi);
            psDel.executeUpdate();
        }

        // Thêm lại toàn bộ món hiện tại
        String sqlInsert = "INSERT INTO PhieuDoUong (MaCaChoi, MaDoUong, SoLuong, ThanhTien) VALUES (?, ?, ?, ?)";
        try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
            for (int i = 0; i < modelBill.getRowCount(); i++) {
                String maDoUong = modelBill.getValueAt(i, 0).toString();
                int soLuong = Integer.parseInt(modelBill.getValueAt(i, 2).toString());
                double thanhTien = Double.parseDouble(modelBill.getValueAt(i, 3).toString().replace(",", ""));

                psIns.setString(1, maCaChoi);
                psIns.setString(2, maDoUong);
                psIns.setInt(3, soLuong);
                psIns.setDouble(4, thanhTien);
                psIns.addBatch();
            }
            psIns.executeBatch();
        }

        conn.commit();
        JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công!\nDữ liệu đã được lưu an toàn vào CSDL.", 
                                      "Thành công", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        e.printStackTrace();
        if (conn != null) {
            try { conn.rollback(); } catch (Exception ex) {}
        }
        JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

// Method hỗ trợ lấy MaCaChoi
private String getMaCaChoiDangChoi(Connection conn, String maBan) throws Exception {
    String sql = "SELECT MaCaChoi FROM CaChoi WHERE MaBan = ? AND TrangThaiCaThuc = N'Đang chơi'";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, maBan);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("MaCaChoi");
        }
    }
    return null;
}

// Method trích xuất mã bàn từ text (hỗ trợ nhiều định dạng)
private String extractMaBan(String text) {
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b(B\\d{3,4})\\b").matcher(text);
    if (m.find()) return m.group(1);
    return text.trim(); // Nếu đã là mã bàn rồi thì trả về luôn
}

    void hienThiHoaDonTheoBan(String maBan) {
        modelBill.setRowCount(0); // Xóa dữ liệu cũ
        tongTien = 0;

        try (Connection conn = JDBCUtil.getConnection()) {
            // Bước 1: Lấy mã ca chơi đang hoạt động
            String sqlCaChoi = "SELECT MaCaChoi FROM CaChoi WHERE MaBan = ? AND TrangThaiCaThuc = N'Đang chơi'";
            PreparedStatement ps1 = conn.prepareStatement(sqlCaChoi);
            ps1.setString(1, maBan);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                String maCaChoi = rs1.getString("MaCaChoi");

                // Bước 2: Truy vấn danh sách món đã gọi từ bảng PhieuDoUong
                String sqlPhieu = "SELECT pd.MaDoUong, d.TenDoUong, pd.SoLuong, pd.ThanhTien "
                        + "FROM PhieuDoUong pd JOIN DoUong d ON pd.MaDoUong = d.MaDoUong "
                        + "WHERE pd.MaCaChoi = ?";
                PreparedStatement ps2 = conn.prepareStatement(sqlPhieu);
                ps2.setString(1, maCaChoi);
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    String maDU = rs2.getString("MaDoUong");
                    String tenDU = rs2.getString("TenDoUong");
                    int soLuong = rs2.getInt("SoLuong");
                    double tien = rs2.getDouble("ThanhTien");

                    tongTien += tien;
                    modelBill.addRow(new Object[]{maDU, tenDU, soLuong, tien});
                }

                lblTongTien.setText(String.format("%.0f đ", tongTien));

                rs2.close();
                ps2.close();
            }

            rs1.close();
            ps1.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị hóa đơn bàn: " + e.getMessage());
        }
    }

    void clearBillTable() {
        modelBill.setRowCount(0);
        tongTien = 0;
        lblTongTien.setText("0 đ");
    }
    
    private void capNhatTongTien() {
    double tong = 0;
    for (int i = 0; i < modelBill.getRowCount(); i++) {
        try {
            Object value = modelBill.getValueAt(i, 3); // Cột "Giá" = Thành tiền
            if (value != null) {
                String str = value.toString().replace(",", "").trim();
                tong += Double.parseDouble(str);
            }
        } catch (Exception e) {
            System.err.println("Lỗi parse dòng " + i + ": " + e.getMessage());
        }
    }
    lblTongTien.setText(String.format("%,.0f đ", tong));
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
        jPanel7 = new javax.swing.JPanel();
        cboBan = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDoUong = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBill = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        btnSuaMon = new javax.swing.JButton();
        btnXoaMon = new javax.swing.JButton();
        btnLuuHoaDon = new javax.swing.JButton();
        btnThemVaoBan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ghi nhận đồ uống");
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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chọn bàn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        cboBan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboBan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboBan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tblDoUong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã đồ uống", "Tên đồ uống", "Giá ", "Số lượng"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblDoUong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDoUongMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDoUong);

        tblBill.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã đồ uống", "Tên đồ uống", "Số lượng", "Giá"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBill);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tùy chỉnh", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        btnSuaMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        btnSuaMon.setText("Sửa món");
        btnSuaMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaMonActionPerformed(evt);
            }
        });

        btnXoaMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnXoaMon.setText("Xóa món");
        btnXoaMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaMonActionPerformed(evt);
            }
        });

        btnLuuHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/lichsu.png"))); // NOI18N
        btnLuuHoaDon.setText("Lưu hóa đơn");
        btnLuuHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuHoaDonActionPerformed(evt);
            }
        });

        btnThemVaoBan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        btnThemVaoBan.setText("Thêm vào bàn");
        btnThemVaoBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemVaoBanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSuaMon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXoaMon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLuuHoaDon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnThemVaoBan))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSuaMon)
                    .addComponent(btnXoaMon)
                    .addComponent(btnLuuHoaDon)
                    .addComponent(btnThemVaoBan))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Tổng tiền:");

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongTien.setForeground(new java.awt.Color(255, 0, 51));
        lblTongTien.setText("0 đ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTongTien)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblTongTien))
                .addContainerGap(91, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 580));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSuaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaMonActionPerformed
        // TODO add your handling code here:
        int row = tblBill.getSelectedRow();
        if (row != -1) {
            String ten = modelBill.getValueAt(row, 1).toString(); // Tên đồ uống (cột 1)
            int currentSoLuong = Integer.parseInt(modelBill.getValueAt(row, 2).toString()); // Cột số lượng (2)
            String input = JOptionPane.showInputDialog(null, "Sửa số lượng cho " + ten, currentSoLuong);

            if (input != null && !input.isBlank()) {
                try {
                    int newSoLuong = Integer.parseInt(input.trim());
                    if (newSoLuong <= 0) {
                        JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0");
                        return;
                    }

                    double donGia = Double.parseDouble(modelBill.getValueAt(row, 3).toString()) / currentSoLuong;
                    modelBill.setValueAt(newSoLuong, row, 2); // Cập nhật số lượng (cột 2)
                    modelBill.setValueAt(donGia * newSoLuong, row, 3); // Cập nhật giá mới (cột 3)
                    updateTongTien();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một món trong hóa đơn để sửa!");
        }
    }//GEN-LAST:event_btnSuaMonActionPerformed

    private void tblDoUongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDoUongMouseClicked
        // TODO add your handling code here:

        if (evt.getClickCount() == 2) {
            int row = tblDoUong.getSelectedRow();
            if (row != -1) {
                String maDoUong = tblDoUong.getValueAt(row, 0).toString();
                String tenDoUong = tblDoUong.getValueAt(row, 1).toString();
                Object giaObj = tblDoUong.getValueAt(row, 2);
                double donGia = 0;

                // Xử lý lỗi nếu kiểu dữ liệu bị sai
                try {
                    if (giaObj instanceof Double) {
                        donGia = (Double) giaObj;
                    } else if (giaObj instanceof String) {
                        donGia = Double.parseDouble(giaObj.toString());
                    } else {
                        System.err.println("Giá không đúng định dạng: " + giaObj);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                // Kiểm tra nếu đã tồn tại thì cập nhật số lượng
                boolean daCo = false;
                for (int i = 0; i < modelBill.getRowCount(); i++) {
                    String maTrongBill = modelBill.getValueAt(i, 0).toString();
                    if (maDoUong.equals(maTrongBill)) {
                        int soLuong = (int) modelBill.getValueAt(i, 2) + 1;
                        modelBill.setValueAt(soLuong, i, 2);
                        modelBill.setValueAt(soLuong * donGia, i, 3);
                        daCo = true;
                        break;
                    }
                }

                // Nếu chưa có thì thêm mới
                if (!daCo) {
                    modelBill.addRow(new Object[]{maDoUong, tenDoUong, 1, donGia, false});
                }

                updateTongTien();
            }
        }
    }//GEN-LAST:event_tblDoUongMouseClicked

    private void btnXoaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaMonActionPerformed
        // TODO add your handling code here:
        int row = tblBill.getSelectedRow();
        if (row != -1) {
            modelBill.removeRow(row);
            updateTongTien();
        }
    }//GEN-LAST:event_btnXoaMonActionPerformed

    private void btnLuuHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuHoaDonActionPerformed
        // TODO add your handling code here:
        luuHoaDon();
    }//GEN-LAST:event_btnLuuHoaDonActionPerformed

    private void btnThemVaoBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemVaoBanActionPerformed
        int row = tblDoUong.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một đồ uống từ danh sách bên trái!", 
                                      "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maDoUong = tblDoUong.getValueAt(row, 0).toString();
    String tenDoUong = tblDoUong.getValueAt(row, 1).toString();
    double giaBan = Double.parseDouble(tblDoUong.getValueAt(row, 2).toString().replace(",", ""));
    int tonKho = Integer.parseInt(tblDoUong.getValueAt(row, 3).toString());

    // Nhập số lượng
    String input = JOptionPane.showInputDialog(this, 
        "Nhập số lượng cho " + tenDoUong + " (Tồn kho: " + tonKho + "):", 
        "Nhập số lượng", JOptionPane.QUESTION_MESSAGE);

    if (input == null || input.trim().isEmpty()) {
        return; // Người dùng bấm Cancel
    }

    int soLuongOrder;
    try {
        soLuongOrder = Integer.parseInt(input.trim());
        if (soLuongOrder <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (soLuongOrder > tonKho) {
            JOptionPane.showMessageDialog(this, "Không đủ hàng! Chỉ còn " + tonKho + " sản phẩm.", 
                                          "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double thanhTien = soLuongOrder * giaBan;

    // Kiểm tra món đã có trong bill chưa → nếu có thì cộng dồn
    boolean daTonTai = false;
    for (int i = 0; i < modelBill.getRowCount(); i++) {
        String maTrongBill = modelBill.getValueAt(i, 0).toString();
        if (maTrongBill.equals(maDoUong)) {
            int slCu = Integer.parseInt(modelBill.getValueAt(i, 2).toString());
            double ttCu = Double.parseDouble(modelBill.getValueAt(i, 3).toString().replace(",", ""));

            modelBill.setValueAt(slCu + soLuongOrder, i, 2);     // Cập nhật số lượng
            modelBill.setValueAt(ttCu + thanhTien, i, 3);        // Cập nhật thành tiền
            daTonTai = true;
            break;
        }
    }

    // Nếu chưa có → thêm mới
    if (!daTonTai) {
        modelBill.addRow(new Object[]{
            maDoUong,
            tenDoUong,
            soLuongOrder,
            thanhTien  // Thành tiền vào cột "Giá"
        });
    }

    capNhatTongTien();
    JOptionPane.showMessageDialog(this, "Đã thêm " + soLuongOrder + " " + tenDoUong + " vào hóa đơn!", 
                                  "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnThemVaoBanActionPerformed

    private void cboBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBanActionPerformed
        // TODO add your handling code here:
        String maBan = (String) cboBan.getSelectedItem();
        if (maBan != null && !maBan.equals("Không có bàn hoạt động")) {
            hienThiHoaDonTheoBan(maBan);
        } else {
            clearBillTable();
        }
    }//GEN-LAST:event_cboBanActionPerformed

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
            java.util.logging.Logger.getLogger(GhiNhanDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GhiNhanDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GhiNhanDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GhiNhanDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GhiNhanDoUongForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLuuHoaDon;
    private javax.swing.JButton btnSuaMon;
    private javax.swing.JButton btnThemVaoBan;
    private javax.swing.JButton btnXoaMon;
    private javax.swing.JComboBox<String> cboBan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JTable tblBill;
    private javax.swing.JTable tblDoUong;
    // End of variables declaration//GEN-END:variables
}
