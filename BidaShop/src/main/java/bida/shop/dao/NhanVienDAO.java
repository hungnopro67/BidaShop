/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bida.shop.dao;

import bida.shop.entity.User;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class NhanVienDAO {
    
    public List<User> selectAll() {
        List<User> list = new ArrayList<>();
        String sql = "select MaNhanVien,TenDangNhap, MatKhau, TenNV, SDT, VaiTro, TrangThaiLamViec FROM NhanVien";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User nhanVien = new User();
                nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                nhanVien.setTenDangNhap(rs.getString("TenDangNhap"));
                nhanVien.setMatKhau(rs.getString("MatKhau"));
                nhanVien.setTenNv(rs.getString("TenNV"));
                nhanVien.setSoDienThoai(rs.getString("SDT"));
                nhanVien.setVaiTro(rs.getString("VaiTro"));
                nhanVien.setTrangThai(rs.getString("TrangThaiLamViec"));
                list.add(nhanVien);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public static User getNhanVienByUsername(String username) {
        User nv = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM NhanVien WHERE TenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nv = new User();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNv(rs.getString("TenNV"));
                nv.setVaiTro(rs.getString("VaiTro"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nv;
    }
    
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM NhanVien WHERE TenDangNhap = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByMaNV(String maNV) {
        String sql = "SELECT 1 FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== FIX DUY NHẤT: void -> int (giữ nguyên logic) =====
    public int insert(User u) {
        String sql = "INSERT INTO NhanVien (TenDangNhap, MatKhau, TenNV,SDT, VaiTro, TrangThaiLamViec,MaNhanVien) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getTenDangNhap());
            ps.setString(2, u.getMatKhau());
            ps.setString(3, u.getTenNv());
            ps.setString(4, u.getSoDienThoai());
            ps.setString(5, u.getVaiTro());
            ps.setString(6, u.getTrangThai());
            ps.setString(7, u.getMaNhanVien());

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== FIX DUY NHẤT: void -> int (giữ nguyên logic) =====
    public int update(User u) {
        String sql = "UPDATE NhanVien SET MatKhau=?, TenNV=?, SDT=?, VaiTro=?, TrangThaiLamViec=? WHERE TenDangNhap=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getMatKhau());
            ps.setString(2, u.getTenNv());
            ps.setString(3, u.getSoDienThoai());
            ps.setString(4, u.getVaiTro());
            ps.setString(5, u.getTrangThai());
            ps.setString(6, u.getTenDangNhap());

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void delete(String tenDangNhap) {
        String sql = "DELETE FROM NhanVien WHERE TenDangNhap=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findById(String tenDangNhap) {
        String sql = "SELECT * FROM NhanVien WHERE TenDangNhap=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("MaNhanVien"),
                    rs.getString("TenNV"),
                    rs.getString("TenDangNhap"),
                    rs.getString("MatKhau"),
                    rs.getString("VaiTro"),
                    rs.getString("SoDienThoai"),
                    rs.getString("TrangThaiLamViec")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User findByUsername(String username) {
        String sql = "SELECT MaNhanVien, TenDangNhap, MatKhau, TenNV, SDT, VaiTro, TrangThaiLamViec FROM NhanVien WHERE TenDangNhap = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User nhanVien = new User();
                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setTenDangNhap(rs.getString("TenDangNhap"));
                    nhanVien.setMatKhau(rs.getString("MatKhau"));
                    nhanVien.setTenNv(rs.getString("TenNV"));
                    nhanVien.setSoDienThoai(rs.getString("SDT"));
                    nhanVien.setVaiTro(rs.getString("VaiTro"));
                    nhanVien.setTrangThai(rs.getString("TrangThaiLamViec"));
                    return nhanVien;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void updateMk(User nhanVien) {
        String sql = "UPDATE NhanVien SET MatKhau = ? WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nhanVien.getMatKhau());
            stmt.setString(2, nhanVien.getMaNhanVien());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}