package bida.shop.dao;

import java.sql.*;
import java.util.*;
import bida.shop.entity.Ban;
import database.JDBCUtil;

public class BanDAO {

    public List<Ban> selectAll() {
        List<Ban> list = new ArrayList<>();
        String sql = "SELECT * FROM Ban";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ban ban = new Ban();
                ban.setMaBan(rs.getString("MaBan"));
                ban.setTenBan(rs.getString("TenBan"));
                ban.setGiaGioChoi(rs.getFloat("GiaBan"));
                ban.setTrangThai(rs.getString("TrangThaiBan"));
                list.add(ban);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
   
}