package bida.shop.ui.manager;

import bida.shop.dao.LichSuGiaGioChoiDAO;
import bida.shop.entity.HistoryPriceTime;
import bida.shop.ui.LoginJDialog;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Admin
 */
public class ThayDoiGiaGioJDialog extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;

    private LichSuGiaGioChoiDAO dao;
    private DefaultTableModel tableModel;
    private int currentIndex = -1;

    /**
     * Creates new form ThayDoiGiaGioJDialog
     */
    public ThayDoiGiaGioJDialog() {
        initComponents();

        init();

        // Đảm bảo cột checkbox hiển thị đúng dạng JCheckBox
        TableColumn column = tblBang.getColumnModel().getColumn(6);
        column.setCellRenderer(tblBang.getDefaultRenderer(Boolean.class));
        column.setCellEditor(tblBang.getDefaultEditor(Boolean.class));

        // Hiện ngày giờ hiện tại
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        txtNgayCapNhat.setText(LocalDateTime.now().format(dtf));

        // Hiện mã nhân viên đang đăng nhập
        if (LoginJDialog.userLogin != null) {
            txtMaNvThayDoi.setText(LoginJDialog.userLogin.getMaNhanVien());
        } else {
            txtMaNvThayDoi.setText("Không rõ"); // hoặc để trống, hoặc gán tạm thời
            System.err.println("⚠️ Chưa đăng nhập nhưng đã mở ThayDoiGiaGio!");
        }

        txtMaNvThayDoi.setEditable(false); // Không cho người dùng sửa tay
        loadDataToTable();
    }

    public void open() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        SwingUtilities.invokeLater(() -> loadDataToTable()); // Đảm bảo cập nhật UI đúng luồng
    }

    private void init() {
        dao = new LichSuGiaGioChoiDAO();
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Mã lịch sử giá", "Mã bàn", "Giá cũ", "Giá mới", "Ngày cập nhật", "Mã NV thay đổi", ""});
        tblBang.setModel(tableModel);
        tblBang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetail();
            }
        });
        fillTable();
    }

    public void loadDataToTable() {
        DefaultTableModel model = (DefaultTableModel) tblBang.getModel();

        if (model.getColumnCount() == 0) {
            model.setColumnIdentifiers(new String[]{"Mã LS", "Mã bàn", "Giá cũ", "Giá mới", "Ngày cập nhật", "Mã NV"});
        }

        model.setRowCount(0); // Xoá dữ liệu cũ

        try {
            List<HistoryPriceTime> list = dao.selectAll();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (HistoryPriceTime h : list) {
                Object[] row = {
                    h.getMaLichSuGia(),
                    h.getMaBan(),
                    h.getGiaCu(),
                    h.getGiaMoi(),
                    h.getNgayCapNhat() != null ? sdf.format(h.getNgayCapNhat()) : "",
                    h.getMaNvThayDoi()
                };
                model.addRow(row);
            }

            System.out.println("Đã load " + list.size() + " dòng vào bảng.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu lịch sử giá giờ chơi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<HistoryPriceTime> list = dao.selectAll();
        if (list != null && !list.isEmpty()) {
            for (HistoryPriceTime history : list) {
                tableModel.addRow(new Object[]{
                    history.getMaLichSuGia(),
                    history.getMaBan(),
                    history.getGiaCu(),
                    history.getGiaMoi(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(history.getNgayCapNhat()),
                    history.getMaNvThayDoi(),
                    false
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu lịch sử giá giờ chơi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        clearForm();
    }

    private void showDetail() {
        int selectedRow = tblBang.getSelectedRow();
        if (selectedRow >= 0) {
            currentIndex = selectedRow;
            HistoryPriceTime history = dao.selectAll().get(selectedRow);
            txtMaLichSuGia.setText(String.valueOf(history.getMaLichSuGia()));
            txtMaban.setText(String.valueOf(history.getMaBan()));
            txtGiaCu.setText(String.valueOf(history.getGiaCu()));
            txtGiaMoi.setText(String.valueOf(history.getGiaMoi()));
            txtNgayCapNhat.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(history.getNgayCapNhat()));
            txtMaNvThayDoi.setText(String.valueOf(history.getMaNvThayDoi()));
        }
    }

    private HistoryPriceTime getFormData() {
        String maLichSuGiaStr = txtMaLichSuGia.getText().trim();
        String maBanStr = txtMaban.getText().trim();
        String giaCuStr = txtGiaCu.getText().trim();
        String giaMoiStr = txtGiaMoi.getText().trim();
        String ngayCapNhatStr = txtNgayCapNhat.getText().trim();
        String maNvThayDoiStr = txtMaNvThayDoi.getText().trim();

        if (maLichSuGiaStr.isEmpty() || maBanStr.isEmpty() || giaCuStr.isEmpty() || giaMoiStr.isEmpty() || ngayCapNhatStr.isEmpty() || maNvThayDoiStr.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin!");
        }

        double giaCu, giaMoi;
        try {
            giaCu = Double.parseDouble(giaCuStr);
            giaMoi = Double.parseDouble(giaMoiStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá cũ và giá mới phải là số hợp lệ!");
        }

        // Xử lý ngày
        Date ngayCapNhat;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ngayCapNhat = sdf.parse(ngayCapNhatStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Định dạng ngày phải là yyyy-MM-dd HH:mm:ss!");
        }

        // Tạo đối tượng và gán giá trị
        HistoryPriceTime history = new HistoryPriceTime();
        history.setMaLichSuGia(maLichSuGiaStr); // ✅ String
        history.setMaBan(maBanStr);             // ✅ String
        history.setGiaCu(giaCu);                // ✅ Double
        history.setGiaMoi(giaMoi);              // ✅ Double
        history.setNgayCapNhat(ngayCapNhat);    // ✅ Date
        history.setMaNvThayDoi(maNvThayDoiStr); // ✅ String

        return history;
    }

    public void create() {
        String sql = "INSERT INTO LichSuGiaGioChoi (MaLichSuGia, MaBan, GiaCu, GiaMoi, NgayCapNhat, MaNVThayDoi) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String maLichSuGia = txtMaLichSuGia.getText().trim();
            String maBan = txtMaban.getText().trim();
            double giaCu = Double.parseDouble(txtGiaCu.getText().trim());
            double giaMoi = Double.parseDouble(txtGiaMoi.getText().trim());
            String ngayCapNhat = txtNgayCapNhat.getText().trim(); // dạng yyyy-MM-dd HH:mm:ss
            String maNV = txtMaNvThayDoi.getText().trim();

            stmt.setString(1, maLichSuGia);
            stmt.setString(2, maBan);
            stmt.setDouble(3, giaCu);
            stmt.setDouble(4, giaMoi);
            stmt.setString(5, ngayCapNhat); // bạn có thể convert sang java.sql.Timestamp nếu cần
            stmt.setString(6, maNV);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Thêm thành công.");
                loadDataToTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + e.getMessage());
        }
    }

    public void update() {
        String sql = "UPDATE LichSuGiaGioChoi SET MaBan = ?, GiaCu = ?, GiaMoi = ?, NgayCapNhat = ?, MaNVThayDoi = ? WHERE MaLichSuGia = ?";
        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String maBan = txtMaban.getText().trim();
            double giaCu = Double.parseDouble(txtGiaCu.getText().trim());
            double giaMoi = Double.parseDouble(txtGiaMoi.getText().trim());
            String ngayCapNhat = txtNgayCapNhat.getText().trim();
            String maNV = txtMaNvThayDoi.getText().trim();
            String maLichSuGia = txtMaLichSuGia.getText().trim();

            stmt.setString(1, maBan);
            stmt.setDouble(2, giaCu);
            stmt.setDouble(3, giaMoi);
            stmt.setString(4, ngayCapNhat);
            stmt.setString(5, maNV);
            stmt.setString(6, maLichSuGia);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
                loadDataToTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
        }
    }

    public void delete() {
        String sql = "DELETE FROM LichSuGiaGioChoi WHERE MaLichSuGia = ?";
        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String maLichSuGia = txtMaLichSuGia.getText().trim();
            stmt.setString(1, maLichSuGia);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Xoá thành công.");
                loadDataToTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xoá: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtMaLichSuGia.setText("");
        txtMaban.setText("");
        txtGiaCu.setText("");
        txtGiaMoi.setText("");
        txtNgayCapNhat.setText("");
        txtMaNvThayDoi.setText("");
        currentIndex = -1;
    }

    private void selectAll() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(true, i, 6);
        }
    }

    private void deselectAll() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(false, i, 6);
        }
    }

    private void deleteSelected() {
        DefaultTableModel model = (DefaultTableModel) tblBang.getModel();
        List<Integer> idsToDelete = new ArrayList<>();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            boolean isSelected = (boolean) model.getValueAt(i, 6);
            if (isSelected) {
                idsToDelete.add((int) model.getValueAt(i, 0));
                model.removeRow(i);
            }
        }
        for (int id : idsToDelete) {
            dao.delete(id); // Giả định delete đã được thêm vào LichSuGiaGioChoiDAO
        }
        clearForm();
    }

    private void first() {
        if (tableModel.getRowCount() > 0) {
            currentIndex = 0;
            tblBang.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }

    private void previous() {
        if (currentIndex > 0) {
            currentIndex--;
            tblBang.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }

    private void next() {
        if (currentIndex < tableModel.getRowCount() - 1) {
            currentIndex++;
            tblBang.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }

    private void last() {
        if (tableModel.getRowCount() > 0) {
            currentIndex = tableModel.getRowCount() - 1;
            tblBang.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
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

        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBang = new javax.swing.JTable();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMaLichSuGia = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtMaban = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtGiaCu = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtGiaMoi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtNgayCapNhat = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMaNvThayDoi = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setTitle("Quản lý giá giờ");
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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        btnCheckAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/check.png"))); // NOI18N
        btnCheckAll.setText("Chọn tất cả");
        btnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAllActionPerformed(evt);
            }
        });

        btnUncheckAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/uncheck.png"))); // NOI18N
        btnUncheckAll.setText("Bỏ chọn tất cả");
        btnUncheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUncheckAllActionPerformed(evt);
            }
        });

        tblBang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã lịch sử giá", "Mã bàn", "Giá cũ", "Giá mới", "Ngày cập nhật", "Mã NV thay đổi", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBang);

        btnDeleteCheckedItems.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/deleteall.png"))); // NOI18N
        btnDeleteCheckedItems.setText("Xóa các mục chọn");
        btnDeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedItemsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(368, Short.MAX_VALUE)
                .addComponent(btnCheckAll)
                .addGap(18, 18, 18)
                .addComponent(btnUncheckAll)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteCheckedItems)
                .addGap(48, 48, 48))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckAll)
                    .addComponent(btnUncheckAll)
                    .addComponent(btnDeleteCheckedItems))
                .addGap(55, 55, 55))
        );

        tabs.addTab("Danh sách", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Mã lịch sử giá");

        btnCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        btnCreate.setText("Tạo mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        btnClear.setText("Nhập mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        btnUpdate.setText("Cập nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        btnMovePrevious.setText("<<");
        btnMovePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovePreviousActionPerformed(evt);
            }
        });

        btnMoveNext.setText(">>");
        btnMoveNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveNextActionPerformed(evt);
            }
        });

        btnMoveLast.setText(">|");
        btnMoveLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveLastActionPerformed(evt);
            }
        });

        jLabel3.setText("Mã bàn");

        jLabel4.setText("Giá cũ");

        jLabel5.setText("Giá mới");

        jLabel6.setText("Ngày cập nhật");

        jLabel7.setText("Mã NV thay đổi");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtMaLichSuGia, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGiaCu, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtMaban, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGiaMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(116, 116, 116))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(97, 97, 97))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtNgayCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(225, 225, 225)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaNvThayDoi, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 794, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(28, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaLichSuGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaban, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGiaCu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGiaMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNgayCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMaNvThayDoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(54, 54, 54)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnClear)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate)
                    .addComponent(btnMoveFirst)
                    .addComponent(btnMovePrevious)
                    .addComponent(btnMoveNext)
                    .addComponent(btnMoveLast))
                .addGap(66, 66, 66))
        );

        tabs.addTab("Biểu mẫu", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 861, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tblBang.getRowCount(); i++) {
            tblBang.setValueAt(true, i, tblBang.getColumnCount() - 1); // cột cuối là checkbox
        }

    }//GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUncheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUncheckAllActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tblBang.getRowCount(); i++) {
            tblBang.setValueAt(false, i, tblBang.getColumnCount() - 1);
        }

    }//GEN-LAST:event_btnUncheckAllActionPerformed

    private void btnDeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCheckedItemsActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblBang.getModel();

        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Boolean checked = (Boolean) model.getValueAt(i, tblBang.getColumnCount() - 1);
            if (checked != null && checked) {
                model.removeRow(i);
            }
        }
    }//GEN-LAST:event_btnDeleteCheckedItemsActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        this.create();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
        first();
    }//GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovePreviousActionPerformed
        // TODO add your handling code here:
        previous();
    }//GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveNextActionPerformed
        // TODO add your handling code here:
        next();
    }//GEN-LAST:event_btnMoveNextActionPerformed

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveLastActionPerformed
        // TODO add your handling code here:
        last();
    }//GEN-LAST:event_btnMoveLastActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        open();
    }//GEN-LAST:event_formInternalFrameOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //* Set the Nimbus look and feel */
        //<editor-fold desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ThayDoiGiaGioJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThayDoiGiaGioJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThayDoiGiaGioJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThayDoiGiaGioJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ThayDoiGiaGioJDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCheckedItems;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUncheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblBang;
    private javax.swing.JTextField txtGiaCu;
    private javax.swing.JTextField txtGiaMoi;
    private javax.swing.JTextField txtMaLichSuGia;
    private javax.swing.JTextField txtMaNvThayDoi;
    private javax.swing.JTextField txtMaban;
    private javax.swing.JTextField txtNgayCapNhat;
    // End of variables declaration//GEN-END:variables
}
