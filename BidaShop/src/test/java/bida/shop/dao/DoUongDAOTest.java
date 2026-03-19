package bida.shop.dao;

import bida.shop.entity.Drink;
import database.JDBCUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class DoUongDAOTest {

    private DoUongDAO dao;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    @BeforeMethod
    public void setup() throws Exception {
        dao = new DoUongDAO();
        conn = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    // TC10: selectAll() - DB có dữ liệu
    @Test
    public void testSelectAllWithData() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true, true, false);
            when(rs.getString("MaDoUong")).thenReturn("DU001", "DU002");
            when(rs.getString("TenDoUong")).thenReturn("Coca", "Pepsi");
            when(rs.getDouble("GiaBan")).thenReturn(10000.0, 12000.0);
            when(rs.getInt("SoLuong")).thenReturn(10, 20);

            List<Drink> list = dao.selectAll();

            Assert.assertEquals(list.size(), 2);
        }
    }

    // TC11: selectAll() - DB rỗng
    @Test
    public void testSelectAllEmpty() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false);

            List<Drink> list = dao.selectAll();

            Assert.assertEquals(list.size(), 0);
        }
    }

    // TC12: selectAll() - lỗi kết nối DB
    @Test
    public void testSelectAllDbError() {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection)
                  .thenThrow(new RuntimeException("DB error"));

            List<Drink> list = dao.selectAll();

            Assert.assertNotNull(list);
            Assert.assertEquals(list.size(), 0);
        }
    }

    // TC13: truSoLuongDoUong - đủ tồn kho
    @Test
    public void testTruSoLuongDu() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(1);

            boolean result = dao.truSoLuongDoUong("DU001", 5);

            Assert.assertTrue(result);
        }
    }

    // TC14: truSoLuongDoUong - không đủ tồn kho
    @Test
    public void testTruSoLuongKhongDu() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(0);

            boolean result = dao.truSoLuongDoUong("DU001", 5);

            Assert.assertFalse(result);
        }
    }

    // TC15: truSoLuongDoUong - số lượng âm
    @Test
    public void testTruSoLuongAm() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString()))
                    .thenThrow(new RuntimeException("Invalid quantity"));

            boolean result = dao.truSoLuongDoUong("DU001", -1);

            Assert.assertFalse(result);
        }
    }

    // TC16: deleteSelected - danh sách hợp lệ
    @Test
    public void testDeleteSelectedSuccess() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(1);

            dao.deleteSelected(Arrays.asList("DU001", "DU002"));

            verify(ps, atLeastOnce()).executeUpdate();
        }
    }

    // TC17: deleteSelected - danh sách rỗng
    @Test
    public void testDeleteSelectedEmptyList() {
        dao.deleteSelected(List.of());
        Assert.assertTrue(true);
    }

    // TC18: deleteSelected - ID không tồn tại
    @Test
    public void testDeleteSelectedNotFound() throws Exception {
        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(0);

            dao.deleteSelected(List.of("DU999"));

            verify(ps, atLeastOnce()).executeUpdate();
        }
    }
}
