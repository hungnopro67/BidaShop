// ===== LichSuGiaGioChoiDAO.java (FIX LỖI setTimestamp – GIỮ NGUYÊN LOGIC) =====
package bida.shop.dao;

import bida.shop.entity.HistoryPriceTime;
import java.sql.*;
import java.util.*;
import database.JDBCUtil;

public class LichSuGiaGioChoiDAO {

    public List<HistoryPriceTime> selectAll() {
        List<HistoryPriceTime> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuGiaGioChoi";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HistoryPriceTime history = new HistoryPriceTime();
                history.setMaLichSuGia(rs.getString("MaLichSuGia"));
                history.setMaBan(rs.getString("MaBan"));
                history.setGiaCu(rs.getDouble("GiaCu"));
                history.setGiaMoi(rs.getDouble("GiaMoi"));
                history.setNgayCapNhat(rs.getTimestamp("NgayCapNhat"));
                history.setMaNvThayDoi(rs.getString("MaNVThayDoi"));
                list.add(history);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // FIX: Date → Timestamp
    public int insert(HistoryPriceTime history) {
        String sql = "INSERT INTO LichSuGiaGioChoi (MaLichSuGia, MaBan, GiaCu, GiaMoi, NgayCapNhat, MaNVThayDoi) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, history.getMaLichSuGia());
            stmt.setString(2, history.getMaBan());
            stmt.setDouble(3, history.getGiaCu());
            stmt.setDouble(4, history.getGiaMoi());
            stmt.setTimestamp(
                5,
                new Timestamp(history.getNgayCapNhat().getTime())
            );
            stmt.setString(6, history.getMaNvThayDoi());
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FIX: Date → Timestamp
    public int update(HistoryPriceTime history) {
        String sql = "UPDATE LichSuGiaGioChoi SET MaBan=?, GiaCu=?, GiaMoi=?, NgayCapNhat=?, MaNVThayDoi=? WHERE MaLichSuGia=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, history.getMaBan());
            stmt.setDouble(2, history.getGiaCu());
            stmt.setDouble(3, history.getGiaMoi());
            stmt.setTimestamp(
                4,
                new Timestamp(history.getNgayCapNhat().getTime())
            );
            stmt.setString(5, history.getMaNvThayDoi());
            stmt.setString(6, history.getMaLichSuGia());
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(String maLichSuGia) {
        String sql = "DELETE FROM LichSuGiaGioChoi WHERE MaLichSuGia = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maLichSuGia);
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // overload cho TEST/UI (int)
    public int delete(int maLichSuGia) {
        return delete(String.valueOf(maLichSuGia));
    }
}