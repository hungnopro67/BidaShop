package bida.shop.entity;

public class CaChoi {

    private String maCaChoi;
    private String maBan;
    private String maNv;
    private String timeBegin;
    private String timeEnd;
    private float tongTienCa;
    private String trangThai;

    public CaChoi() {
    }

    public CaChoi(String maCaChoi, String maBan, String maNv,
                  String timeBegin, String timeEnd,
                  float tongTienCa, String trangThai) {
        this.maCaChoi = maCaChoi;
        this.maBan = maBan;
        this.maNv = maNv;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.tongTienCa = tongTienCa;
        this.trangThai = trangThai;
    }

    public String getMaCaChoi() {
        return maCaChoi;
    }

    public void setMaCaChoi(String maCaChoi) {
        this.maCaChoi = maCaChoi;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public float getTongTienCa() {
        return tongTienCa;
    }

    public void setTongTienCa(float tongTienCa) {
        this.tongTienCa = tongTienCa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}