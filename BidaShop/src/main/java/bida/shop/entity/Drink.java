package bida.shop.entity;

public class Drink {

    private String maDoUong;
    private String tenDoUong;
    private double giaBan;
    private int soLuong;

    public Drink() {
    }

    public Drink(String maDoUong, String tenDoUong,
                 double giaBan, int soLuong) {
        this.maDoUong = maDoUong;
        this.tenDoUong = tenDoUong;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
    }

    public String getMaDoUong() {
        return maDoUong;
    }

    public void setMaDoUong(String maDoUong) {
        this.maDoUong = maDoUong;
    }

    public String getTenDoUong() {
        return tenDoUong;
    }

    public void setTenDoUong(String tenDoUong) {
        this.tenDoUong = tenDoUong;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}