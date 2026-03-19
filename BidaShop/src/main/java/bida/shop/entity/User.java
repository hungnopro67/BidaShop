package bida.shop.entity;

public class User {

    private String maNhanVien;
    private String tenNv;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private String soDienThoai;
    private String trangThai;

    // Constructor không tham số
    public User() {
    }

    // Constructor đầy đủ tham số
    public User(String maNhanVien,
                String tenNv,
                String tenDangNhap,
                String matKhau,
                String vaiTro,
                String soDienThoai,
                String trangThai) {
        this.maNhanVien = maNhanVien;
        this.tenNv = tenNv;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.soDienThoai = soDienThoai;
        this.trangThai = trangThai;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNv() {
        return tenNv;
    }

    public void setTenNv(String tenNv) {
        this.tenNv = tenNv;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}