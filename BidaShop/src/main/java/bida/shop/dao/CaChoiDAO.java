/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bida.shop.dao;

import bida.shop.entity.CaChoi;
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
public class CaChoiDAO {
     public List<CaChoi> selectAll() {
        List<CaChoi> list = new ArrayList<>();
        String sql = "SELECT MaCaChoi, MaBan, ThoiGianBatDau, ThoiGianKetThuc, TrangThaiCaThuc,TongTienCa FROM CaChoi";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CaChoi caChoi = new CaChoi();
                caChoi.setMaCaChoi(rs.getString("MaCaChoi"));
                caChoi.setMaBan(rs.getString("MaBan"));
                caChoi.setTimeBegin(rs.getString("ThoiGianBatDau"));
                caChoi.setTimeEnd(rs.getString("ThoiGianKetThuc"));
                caChoi.setTrangThai(rs.getString("TrangThaiCaThuc"));
                caChoi.setTongTienCa(rs.getFloat("TongTienCa"));
                list.add(caChoi);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
