package bida.shop.entity;

import java.util.Date;

public class HistoryPriceTime {

    private String maLichSuGia;
    private String maBan;
    private double giaCu;
    private double giaMoi;
    private Date ngayCapNhat;
    private String maNvThayDoi;

    public HistoryPriceTime() {
    }

    // constructor ĐÚNG như test đang dùng
    public HistoryPriceTime(String maLichSuGia, String maBan,
                            double giaCu, double giaMoi,
                            Date ngayCapNhat, String maNvThayDoi) {
        this.maLichSuGia = maLichSuGia;
        this.maBan = maBan;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.ngayCapNhat = ngayCapNhat;
        this.maNvThayDoi = maNvThayDoi;
    }

    public String getMaLichSuGia() {
        return maLichSuGia;
    }

    public void setMaLichSuGia(String maLichSuGia) {
        this.maLichSuGia = maLichSuGia;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public double getGiaCu() {
        return giaCu;
    }

    public void setGiaCu(double giaCu) {
        this.giaCu = giaCu;
    }

    public double getGiaMoi() {
        return giaMoi;
    }

    public void setGiaMoi(double giaMoi) {
        this.giaMoi = giaMoi;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaNvThayDoi() {
        return maNvThayDoi;
    }

    public void setMaNvThayDoi(String maNvThayDoi) {
        this.maNvThayDoi = maNvThayDoi;
    }
}