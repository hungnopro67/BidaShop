package bida.shop.entity;

public class PhieuDoUong {

    private int maPhieu;
    private int maCaChoi;
    private int maDoUong;
    private int soLuong;
    private double thanhTien;

    public PhieuDoUong() {
    }

    public PhieuDoUong(int maPhieu, int maCaChoi,
                       int maDoUong, int soLuong,
                       double thanhTien) {
        this.maPhieu = maPhieu;
        this.maCaChoi = maCaChoi;
        this.maDoUong = maDoUong;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    public int getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(int maPhieu) {
        this.maPhieu = maPhieu;
    }

    public int getMaCaChoi() {
        return maCaChoi;
    }

    public void setMaCaChoi(int maCaChoi) {
        this.maCaChoi = maCaChoi;
    }

    public int getMaDoUong() {
        return maDoUong;
    }

    public void setMaDoUong(int maDoUong) {
        this.maDoUong = maDoUong;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }
}