/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bida.shop.ui.manager;

import bida.shop.dao.BanDAO;
import bida.shop.dao.NhanVienDAO;
import bida.shop.entity.Ban;
import bida.shop.entity.User;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class QuanLyNhanVienForm extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;
    private int currentIndex = -1;
    private List<User> userList = new ArrayList<>();
    private DefaultTableModel tableModel;

    /**
     * Creates new form QuanLyNhanVienForm
     */
    
    
    public QuanLyNhanVienForm() {
        initComponents();
        tableModel = (DefaultTableModel) tblUserManager.getModel();
        ButtonGroup groupRole = new ButtonGroup();
        groupRole.add(rdoManager);
        groupRole.add(rdoEmployee);

        ButtonGroup groupStatus = new ButtonGroup();
        groupStatus.add(rdoActive);
        groupStatus.add(rdoSuspended);
    }

    public void open(){
    BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
    ui.setNorthPane(null);
    loadDataToTable();
    // Đảm bảo radio có giá trị mặc định khi mở lần đầu
    if (!rdoManager.isSelected() && !rdoEmployee.isSelected()) {
        rdoManager.setSelected(true);
    }
    if (!rdoActive.isSelected() && !rdoSuspended.isSelected()) {
        rdoActive.setSelected(true);
    }
}

    
    private void loadDataToTable() {
    DefaultTableModel model = (DefaultTableModel) tblUserManager.getModel();
    model.setRowCount(0);
    model.setColumnIdentifiers(new String[]{"Tên đăng nhập", "Mật khẩu", "Họ và tên","SDT","Vai trò", "Trạng thái",""});
    NhanVienDAO dao = new NhanVienDAO();
    List<User> list = dao.selectAll();
    System.out.println("Số nhân viên: " + list.size());
    for (User nhanVien : list) {
        Object[] row = {
            nhanVien.getTenDangNhap(),
            nhanVien.getMatKhau(),
            nhanVien.getTenNv(),
            nhanVien.getSoDienThoai(),
            nhanVien.getVaiTro(),
            nhanVien.getTrangThai(),
        };
        model.addRow(row);
    }
}
    
    public void taoMoi() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();
    String confirm = new String(txtConfirmPassword.getPassword()).trim();
    String fullname = txtFullName.getText().trim();
    String sdt = txtSdt.getText().trim();
    String isManager = rdoManager.isSelected() ? "Quản lý" : "Nhân viên";
    String isActive = rdoActive.isSelected() ? "Đang làm" : "Nghỉ";
    String maNv = txtmaNV.getText().trim();

    // Kiểm tra dữ liệu trống
    if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() ||
        fullname.isEmpty() || sdt.isEmpty() || maNv.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    // Kiểm tra xác nhận mật khẩu
    if (!password.equals(confirm)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
        return;
    }

    NhanVienDAO dao = new NhanVienDAO();

    // Kiểm tra trùng Username - HIỆN THÔNG BÁO NHƯ CŨ NHƯNG KHÔNG DỪNG LẠI
    if (dao.existsByUsername(username)) {
        JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
        return;
        // Không có return → tiếp tục thực hiện thêm nhân viên sau khi bấm OK
    }

    // Kiểm tra trùng Mã nhân viên
    if (dao.existsByMaNV(maNv)) {
        JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại!");
        return;
    }

    // Tạo đối tượng User
    User user = new User(
        maNv,
        fullname,
        username,
        password,
        isManager,
        sdt,
        isActive
    );

    // Thêm vào DB
    dao.insert(user);
    loadDataToTable();
    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
}



    
    public void capNhat() {
    String password = new String(txtPassword.getPassword()).trim();
    String confirm = new String(txtConfirmPassword.getPassword()).trim();
    String fullname = txtFullName.getText().trim();
    String sdt = txtSdt.getText().trim();
    String isManager = rdoManager.isSelected() ? "Quản lý" : "Nhân viên";
    String isActive = rdoActive.isSelected() ? "Đang làm" : "Nghỉ";

    if (password.isEmpty() || confirm.isEmpty() || fullname.isEmpty() || sdt.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
        return;
    }
    if (!password.equals(confirm)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
        return;
    }

    User user = new User(
        txtmaNV.getText().trim(),      // giữ nguyên không cho sửa
        fullname,
        txtUsername.getText().trim(),  // giữ nguyên không cho sửa
        password,
        isManager,
        sdt,
        isActive
    );

    new NhanVienDAO().update(user);
    loadDataToTable();
    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
}

    
    public void xoa() {
    String username = txtUsername.getText().trim();

    if (username.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn chắc chắn muốn xoá?", "Xác nhận", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    NhanVienDAO dao = new NhanVienDAO();
    dao.delete(username);
    loadDataToTable();

    // Xóa xong thì tự động làm trắng form
    nhapMoi();

    JOptionPane.showMessageDialog(this, "Xóa thành công!");
}

    
    public void nhapMoi(){
        txtUsername.setText("");
        txtFullName.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtSdt.setText("");
        txtmaNV.setText("");
        rdoManager.setSelected(true);
        rdoActive.setSelected(true);
        txtUsername.setEditable(true);
txtmaNV.setEditable(true);
rdoManager.setEnabled(true);
rdoEmployee.setEnabled(true);


    }
   
    private void setForm(User u) {
        txtUsername.setText(u.getTenDangNhap());
        txtPassword.setText(u.getMatKhau());
        txtConfirmPassword.setText(u.getMatKhau());
        txtFullName.setText(u.getTenNv());

        if ("Quản lý".equalsIgnoreCase(u.getVaiTro())) {
            rdoManager.setSelected(true);
            rdoEmployee.setSelected(false);
        } else {
            rdoEmployee.setSelected(true);
            rdoManager.setSelected(false);
        }

        if ("Đang làm".equalsIgnoreCase(u.getTrangThai())) {
            rdoActive.setSelected(true);
            rdoSuspended.setSelected(false);
        } else {
            rdoSuspended.setSelected(true);
            rdoActive.setSelected(false);
        }
    }
    
    private void showDetail() {
    int selectedRow = tblUserManager.getSelectedRow();
    if (selectedRow >= 0) {

        String username = tblUserManager.getValueAt(selectedRow, 0).toString();

        // Lấy đầy đủ thông tin từ Database theo username
        User u = new NhanVienDAO().findByUsername(username);

        if (u != null) {
            txtUsername.setText(u.getTenDangNhap());
            txtPassword.setText(u.getMatKhau());
            txtConfirmPassword.setText(u.getMatKhau());
            txtFullName.setText(u.getTenNv());
            txtSdt.setText(u.getSoDienThoai());
            txtmaNV.setText(u.getMaNhanVien());

            // Vai trò
            if ("Quản lý".equalsIgnoreCase(u.getVaiTro())) {
                rdoManager.setSelected(true);
            } else {
                rdoEmployee.setSelected(true);
            }

            // Trạng thái
            if ("Đang làm".equalsIgnoreCase(u.getTrangThai())) {
                rdoActive.setSelected(true);
            } else {
                rdoSuspended.setSelected(true);
            }
        }
    }
    txtUsername.setEditable(false);
txtmaNV.setEditable(false);
rdoManager.setEnabled(false);
rdoEmployee.setEnabled(false);


}


    
    private void first() {
        if (tableModel.getRowCount() > 0) {
            currentIndex = 0;
            tblUserManager.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }
    
    private void previous() {
        if (currentIndex > 0) {
            currentIndex--;
            tblUserManager.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }
    
    private void next() {
        if (currentIndex < tableModel.getRowCount() - 1) {
            currentIndex++;
            tblUserManager.setRowSelectionInterval(currentIndex, currentIndex);
            showDetail();
        }
    }
    
    private void last() {
        if (tableModel.getRowCount() > 0) {
            currentIndex = tableModel.getRowCount() - 1;
            tblUserManager.setRowSelectionInterval(currentIndex, currentIndex);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUserManager = new javax.swing.JTable();
        btnSelectAll = new javax.swing.JButton();
        btnDeleteSelected = new javax.swing.JButton();
        btnDeselectAll = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        rdoEmployee = new javax.swing.JRadioButton();
        rdoManager = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        rdoActive = new javax.swing.JRadioButton();
        rdoSuspended = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreateNew = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtSdt = new javax.swing.JTextField();
        txtmaNV = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý nhân viên");
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

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(868, 570));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tblUserManager.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tên đăng nhập", "Mật khẩu ", "Họ và tên", "SDT", "Vai trò", "Trạng thái ", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblUserManager);

        btnSelectAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/check.png"))); // NOI18N
        btnSelectAll.setText("Chọn tất cả");
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
            }
        });

        btnDeleteSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/deleteall.png"))); // NOI18N
        btnDeleteSelected.setText("Xóa các mục chọn");
        btnDeleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSelectedActionPerformed(evt);
            }
        });

        btnDeselectAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/uncheck.png"))); // NOI18N
        btnDeselectAll.setText("Bỏ chọn tất cả");
        btnDeselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeselectAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(392, Short.MAX_VALUE)
                .addComponent(btnSelectAll)
                .addGap(18, 18, 18)
                .addComponent(btnDeselectAll)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteSelected)
                .addGap(31, 31, 31))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 848, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(128, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectAll)
                    .addComponent(btnDeleteSelected)
                    .addComponent(btnDeselectAll))
                .addGap(55, 55, 55))
        );

        jTabbedPane1.addTab("Danh sách", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 36, -1, -1));

        jLabel2.setText("Tên đăng nhập");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 36, -1, -1));

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });
        jPanel2.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 58, 239, -1));

        jLabel3.setText("Họ và tên");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 36, -1, -1));

        txtFullName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFullNameActionPerformed(evt);
            }
        });
        jPanel2.add(txtFullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 58, 250, -1));

        jLabel4.setText("Mật khẩu");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 138, -1, -1));
        jPanel2.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 160, 239, -1));

        jLabel5.setText("Xác nhận mật khẩu");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 138, -1, -1));
        jPanel2.add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 160, 250, -1));

        jLabel6.setText("Vai trò");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 267, 37, -1));

        rdoEmployee.setText("Nhân viên");
        jPanel2.add(rdoEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(307, 301, -1, -1));

        rdoManager.setText("Quản lý");
        jPanel2.add(rdoManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 301, -1, -1));

        jLabel7.setText("Trạng thái");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 267, -1, -1));

        rdoActive.setText("Đang làm");
        jPanel2.add(rdoActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 301, -1, -1));

        rdoSuspended.setText("Nghỉ");
        rdoSuspended.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoSuspendedActionPerformed(evt);
            }
        });
        jPanel2.add(rdoSuspended, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 301, -1, -1));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 414, 856, 11));

        btnCreateNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        btnCreateNew.setText("Tạo mới");
        btnCreateNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnCreateNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 474, -1, -1));

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        btnUpdate.setText("Cập nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel2.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 474, -1, -1));

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(251, 474, -1, -1));

        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        btnReset.setText("Nhập mới");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        jPanel2.add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(338, 474, -1, -1));

        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        jPanel2.add(btnFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(627, 476, 42, -1));

        btnPrevious.setText("<<");
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrevious, new org.netbeans.lib.awtextra.AbsoluteConstraints(675, 476, 52, -1));

        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jPanel2.add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(733, 476, 51, -1));

        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        jPanel2.add(btnLast, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 476, 42, -1));

        jLabel8.setText("Số điện thoại");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 200, -1, -1));

        txtSdt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSdtActionPerformed(evt);
            }
        });
        jPanel2.add(txtSdt, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 228, 239, -1));

        txtmaNV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmaNVActionPerformed(evt);
            }
        });
        jPanel2.add(txtmaNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 222, 250, -1));

        jLabel9.setText("Mã nhân viên");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 200, -1, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nhanvien.jpg"))); // NOI18N
        jLabel10.setText("jLabel10");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 201, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 200, 280));

        jTabbedPane1.addTab("Biểu mẫu", jPanel2);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblUserManager.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(true, i, 6); // Cột checkbox ở vị trí 6
        }
    }//GEN-LAST:event_btnSelectAllActionPerformed

    private void btnDeleteSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSelectedActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblUserManager.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Lấy MaNV từ dòng được chọn
        String tenDangNhap = tblUserManager.getValueAt(selectedRow, 0).toString();

        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "DELETE FROM NhanVien WHERE TenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ((DefaultTableModel) tblUserManager.getModel()).removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên để xóa.");
            }

            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }//GEN-LAST:event_btnDeleteSelectedActionPerformed

    private void btnDeselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeselectAllActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblUserManager.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 6); // Cột checkbox ở vị trí 6
        }
    }//GEN-LAST:event_btnDeselectAllActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtmaNVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmaNVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmaNVActionPerformed

    private void txtSdtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSdtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSdtActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        last();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        next();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        // TODO add your handling code here:
        previous();
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        first();

    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        nhapMoi();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:

        xoa();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        capNhat();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCreateNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateNewActionPerformed
        // TODO add your handling code here:
        taoMoi();
    }//GEN-LAST:event_btnCreateNewActionPerformed

    private void rdoSuspendedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoSuspendedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoSuspendedActionPerformed

    private void txtFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFullNameActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

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
            java.util.logging.Logger.getLogger(QuanLyNhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLyNhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLyNhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLyNhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuanLyNhanVienForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateNew;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteSelected;
    private javax.swing.JButton btnDeselectAll;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton rdoActive;
    private javax.swing.JRadioButton rdoEmployee;
    private javax.swing.JRadioButton rdoManager;
    private javax.swing.JRadioButton rdoSuspended;
    private javax.swing.JTable tblUserManager;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtSdt;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtmaNV;
    // End of variables declaration//GEN-END:variables
}
