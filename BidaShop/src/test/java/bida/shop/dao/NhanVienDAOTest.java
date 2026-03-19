package bida.shop.dao;

import bida.shop.entity.User;
import database.JDBCUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Unit Test cho NhanVienDAO
 * Framework: TestNG + Mockito (mock static JDBCUtil)
 */
public class NhanVienDAOTest {

    // ===== TC01: insert - dữ liệu hợp lệ =====
    @Test
    public void testInsertSuccess() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();
        User u = new User("NV01", "Nguyen Van A", "user1", "123", "Employee", "0123", "Active");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeUpdate()).thenReturn(1);

            int result = dao.insert(u);
            Assert.assertEquals(result, 1);
        }
    }

    // ===== TC02: insert - lỗi SQL =====
    @Test
    public void testInsertFail() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();
        User u = new User();

        Connection conn = Mockito.mock(Connection.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenThrow(new RuntimeException());

            int result = dao.insert(u);
            Assert.assertEquals(result, 0);
        }
    }

    // ===== TC03: update - thành công =====
    @Test
    public void testUpdateSuccess() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();
        User u = new User("NV01", "A", "user1", "123", "Admin", "0123", "Active");

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeUpdate()).thenReturn(1);

            int result = dao.update(u);
            Assert.assertEquals(result, 1);
        }
    }

    // ===== TC04: update - thất bại =====
    @Test
    public void testUpdateFail() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();
        User u = new User();

        Connection conn = Mockito.mock(Connection.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenThrow(new RuntimeException());

            int result = dao.update(u);
            Assert.assertEquals(result, 0);
        }
    }

    // ===== TC05: delete - không exception =====
    @Test
    public void testDeleteSuccess() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);

            dao.delete("user1");
            Mockito.verify(ps).executeUpdate();
        }
    }

    // ===== TC06: existsByUsername - tồn tại =====
    @Test
    public void testExistsByUsernameTrue() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeQuery()).thenReturn(rs);
            Mockito.when(rs.next()).thenReturn(true);

            Assert.assertTrue(dao.existsByUsername("user1"));
        }
    }

    // ===== TC07: existsByUsername - không tồn tại =====
    @Test
    public void testExistsByUsernameFalse() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeQuery()).thenReturn(rs);
            Mockito.when(rs.next()).thenReturn(false);

            Assert.assertFalse(dao.existsByUsername("user1"));
        }
    }

    // ===== TC08: findByUsername - có dữ liệu =====
    @Test
    public void testFindByUsernameFound() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeQuery()).thenReturn(rs);

            Mockito.when(rs.next()).thenReturn(true);
            Mockito.when(rs.getString(anyString())).thenReturn("DATA");

            User u = dao.findByUsername("user1");
            Assert.assertNotNull(u);
        }
    }

    // ===== TC09: findByUsername - không có dữ liệu =====
    @Test
    public void testFindByUsernameNotFound() throws Exception {
        NhanVienDAO dao = new NhanVienDAO();

        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        try (MockedStatic<JDBCUtil> mocked = Mockito.mockStatic(JDBCUtil.class)) {
            mocked.when(JDBCUtil::getConnection).thenReturn(conn);
            Mockito.when(conn.prepareStatement(anyString())).thenReturn(ps);
            Mockito.when(ps.executeQuery()).thenReturn(rs);
            Mockito.when(rs.next()).thenReturn(false);

            User u = dao.findByUsername("user1");
            Assert.assertNull(u);
        }
    }
}
