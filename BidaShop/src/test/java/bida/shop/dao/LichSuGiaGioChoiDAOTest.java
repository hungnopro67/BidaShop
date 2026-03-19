// ===== LichSuGiaGioChoiDAOTest.java =====
package bida.shop.dao;

import bida.shop.entity.HistoryPriceTime;
import database.JDBCUtil;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.testng.Assert.*;

public class LichSuGiaGioChoiDAOTest {

    // ===== insert =====

    @Test
    public void testInsertSuccess() throws Exception {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS01");
        h.setMaBan("B01");
        h.setGiaCu(100);
        h.setGiaMoi(120);
        h.setNgayCapNhat(new Date());
        h.setMaNvThayDoi("NV01");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            int result = new LichSuGiaGioChoiDAO().insert(h);
            assertEquals(result, 1);
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testInsertNullField() {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS02");
        h.setNgayCapNhat(null); // gây NullPointerException theo logic DAO

        new LichSuGiaGioChoiDAO().insert(h);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testInsertDuplicateKey() throws Exception {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS01");
        h.setMaBan("B01");
        h.setGiaCu(100);
        h.setGiaMoi(120);
        h.setNgayCapNhat(new Date());
        h.setMaNvThayDoi("NV01");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenThrow(new RuntimeException("Duplicate key"));

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            new LichSuGiaGioChoiDAO().insert(h);
        }
    }

    // ===== update =====

    @Test
    public void testUpdateSuccess() throws Exception {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS01");
        h.setMaBan("B02");
        h.setGiaCu(120);
        h.setGiaMoi(150);
        h.setNgayCapNhat(new Date());
        h.setMaNvThayDoi("NV02");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            int result = new LichSuGiaGioChoiDAO().update(h);
            assertEquals(result, 1);
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testUpdateInvalidData() {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS01");
        h.setNgayCapNhat(null); // đúng hành vi fail

        new LichSuGiaGioChoiDAO().update(h);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testUpdateNotFound() throws Exception {
        HistoryPriceTime h = new HistoryPriceTime();
        h.setMaLichSuGia("LS99");
        h.setMaBan("B01");
        h.setGiaCu(100);
        h.setGiaMoi(120);
        h.setNgayCapNhat(new Date());
        h.setMaNvThayDoi("NV01");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenReturn(0); // không có bản ghi

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            int result = new LichSuGiaGioChoiDAO().update(h);
            assertEquals(result, 0);
        }
    }

    // ===== delete =====

    @Test
    public void testDeleteSuccess() throws Exception {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            int result = new LichSuGiaGioChoiDAO().delete("LS01");
            assertEquals(result, 1);
        }
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            int result = new LichSuGiaGioChoiDAO().delete("LS99");
            assertEquals(result, 0);
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testDeleteInvalidId() throws Exception {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
        Mockito.when(ps.executeUpdate()).thenThrow(new RuntimeException("Invalid id"));

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            new LichSuGiaGioChoiDAO().delete("###");
        }
    }
}
