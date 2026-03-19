package bida.shop.dao;

import java.sql.*;
import java.util.*;
import bida.shop.entity.Drink;
import database.JDBCUtil;
import javax.swing.JOptionPane;

public class DoUongDAO {

    public List<Drink> selectAll() {
        List<Drink> list = new ArrayList<>();
        String sql = "SELECT * FROM DoUong";

        try (Connection conn = JDBCUtil.getConnection()) {
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }
            System.out.println("Kết nối thành công, thực thi truy vấn: " + sql);
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Drink doUong = new Drink();
                    doUong.setMaDoUong(rs.getString("MaDoUong"));
                    doUong.setTenDoUong(rs.getString("TenDoUong"));
                    doUong.setGiaBan(rs.getDouble("GiaBan"));
                    doUong.setSoLuong(rs.getInt("SoLuong"));
                    list.add(doUong);
                    System.out.println("Đọc: " + doUong.getMaDoUong() + ", " + doUong.getTenDoUong());
                }
            }
            System.out.println("Tổng số bản ghi: " + list.size());
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println("Lỗi khác: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return list;
    }

    public void deleteSelected(List<String> idsToDelete) {
        String sql = "DELETE FROM DoUong WHERE MaDoUong = ?";
        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String id : idsToDelete) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xoá nhiều mục: " + e.getMessage());
        }
    }

    public boolean truSoLuongDoUong(String maDoUong, int soLuongTru) {
        String sql = "UPDATE DoUong SET SoLuong = SoLuong - ? WHERE MaDoUong = ? AND SoLuong >= ?";
        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuongTru);
            ps.setString(2, maDoUong);
            ps.setInt(3, soLuongTru);
            int result = ps.executeUpdate();
            return result > 0; // true nếu trừ thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
