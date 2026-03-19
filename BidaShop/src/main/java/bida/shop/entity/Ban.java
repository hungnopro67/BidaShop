package bida.shop.entity;

public class Ban {

    private String maBan;
    private String tenBan;
    private float giaGioChoi;
    private String trangThai;

    public Ban() {
    }

    public Ban(String maBan, String tenBan,
               float giaGioChoi, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.giaGioChoi = giaGioChoi;
        this.trangThai = trangThai;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public float getGiaGioChoi() {
        return giaGioChoi;
    }

    public void setGiaGioChoi(float giaGioChoi) {
        this.giaGioChoi = giaGioChoi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}