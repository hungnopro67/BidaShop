package bida.shop.dao;


import database.JDBCUtil;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DoanhThuDAO {

    /**
     * Lấy doanh thu gộp theo từng ngày trong khoảng thời gian
     */
//    public static List<Object[]> getDoanhThu(Date tuNgay, Date denNgay) {
//        List<Object[]> list = new ArrayList<>();
//
//        String sql = """
//            SELECT 
//                CONVERT(DATE, c.ThoiGianBatDau) AS NgayBaoCao,
//                SUM(c.TongTienCa) - ISNULL(SUM(p.ThanhTien), 0) AS DoanhThuBanChoi,
//                ISNULL(SUM(p.ThanhTien), 0) AS DoanhThuDoUong,
//                SUM(c.TongTienCa) AS TongDoanhThu
//            FROM CaChoi c
//            LEFT JOIN PhieuDoUong p ON c.MaCaChoi = p.MaCaChoi
//            WHERE CONVERT(DATE, c.ThoiGianBatDau) BETWEEN ? AND ?
//            GROUP BY CONVERT(DATE, c.ThoiGianBatDau)
//            ORDER BY NgayBaoCao
//        """;
//
//        try (Connection conn = JDBCUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
//            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Object[] row = {
//                    rs.getDate("NgayBaoCao"),
//                    rs.getDouble("DoanhThuBanChoi"),
//                    rs.getDouble("DoanhThuDoUong"),
//                    rs.getDouble("TongDoanhThu")
//                };
//                list.add(row);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
    public static List<Object[]> getDoanhThu(Date tuNgay, Date denNgay) {
    List<Object[]> list = new ArrayList<>();

    String sql = """
    SELECT 
        bc.NgayBaoCao,
        bc.TongDoanhThu - ISNULL(du.DoanhThuDoUong, 0) AS DoanhThuBanChoi,
        ISNULL(du.DoanhThuDoUong, 0) AS DoanhThuDoUong,
        bc.TongDoanhThu
    FROM (
        SELECT 
            CAST(ThoiGianBatDau AS DATE) AS NgayBaoCao,
            SUM(TongTienCa) AS TongDoanhThu
        FROM CaChoi
        WHERE CAST(ThoiGianBatDau AS DATE) BETWEEN ? AND ?
        GROUP BY CAST(ThoiGianBatDau AS DATE)
    ) bc
    LEFT JOIN (
        SELECT 
            CAST(c.ThoiGianBatDau AS DATE) AS NgayBaoCao,
            SUM(p.ThanhTien) AS DoanhThuDoUong
        FROM PhieuDoUong p
        JOIN CaChoi c ON p.MaCaChoi = c.MaCaChoi
        WHERE CAST(c.ThoiGianBatDau AS DATE) BETWEEN ? AND ?
        GROUP BY CAST(c.ThoiGianBatDau AS DATE)
    ) du
    ON bc.NgayBaoCao = du.NgayBaoCao
    ORDER BY bc.NgayBaoCao
""";
    

    try (Connection conn = JDBCUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
        ps.setDate(2, new java.sql.Date(denNgay.getTime()));
        ps.setDate(3, new java.sql.Date(tuNgay.getTime()));
        ps.setDate(4, new java.sql.Date(denNgay.getTime()));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getDate("NgayBaoCao"),
                rs.getDouble("DoanhThuBanChoi"),
                rs.getDouble("DoanhThuDoUong"),
                rs.getDouble("TongDoanhThu")
            };
            list.add(row);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}


    /**
     * Lấy doanh thu tổng của một ngày
     */
//    public Object[] getDoanhThuNgay(Date ngay) {
//        String sql = """
//            SELECT 
//                SUM(c.TongTienCa) - ISNULL(SUM(p.ThanhTien), 0) AS DoanhThuBanChoi,
//                ISNULL(SUM(p.ThanhTien), 0) AS DoanhThuDoUong,
//                SUM(c.TongTienCa) AS TongDoanhThu
//            FROM CaChoi c
//            LEFT JOIN PhieuDoUong p ON c.MaCaChoi = p.MaCaChoi
//            WHERE CONVERT(DATE, c.ThoiGianBatDau) = ?
//        """;
//
//        try (Connection conn = JDBCUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setDate(1, new java.sql.Date(ngay.getTime()));
//
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return new Object[]{
//                    rs.getDouble("DoanhThuBanChoi"),
//                    rs.getDouble("DoanhThuDoUong"),
//                    rs.getDouble("TongDoanhThu")
//                };
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public Object[] getDoanhThuNgay(Date ngay) {
    String sql = """
    SELECT 
        bc.TongDoanhThu - ISNULL(du.DoanhThuDoUong, 0) AS DoanhThuBanChoi,
        ISNULL(du.DoanhThuDoUong, 0) AS DoanhThuDoUong,
        bc.TongDoanhThu
    FROM (
        SELECT 
            CAST(ThoiGianBatDau AS DATE) AS NgayBaoCao,
            SUM(TongTienCa) AS TongDoanhThu
        FROM CaChoi
        WHERE CAST(ThoiGianBatDau AS DATE) = ?
        GROUP BY CAST(ThoiGianBatDau AS DATE)
    ) bc
    LEFT JOIN (
        SELECT 
            CAST(c.ThoiGianBatDau AS DATE) AS NgayBaoCao,
            SUM(p.ThanhTien) AS DoanhThuDoUong
        FROM PhieuDoUong p
        JOIN CaChoi c ON p.MaCaChoi = c.MaCaChoi
        WHERE CAST(c.ThoiGianBatDau AS DATE) = ?
        GROUP BY CAST(c.ThoiGianBatDau AS DATE)
    ) du
    ON bc.NgayBaoCao = du.NgayBaoCao
""";



    try (Connection conn = JDBCUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        java.sql.Date sqlNgay = new java.sql.Date(ngay.getTime());
        ps.setDate(1, sqlNgay);
        ps.setDate(2, sqlNgay);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Object[]{
                rs.getDouble("DoanhThuBanChoi"),
                rs.getDouble("DoanhThuDoUong"),
                rs.getDouble("TongDoanhThu")
            };
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return new Object[]{0.0, 0.0, 0.0};
}

}
