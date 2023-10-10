package com.github.jsbxyyx.xid;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class Xid {

    private static DataSource dataSource;

    private static String table_name = "tb_sequence";

    public static void setDataSource(DataSource dataSource) {
        Xid.dataSource = Objects.requireNonNull(dataSource);
    }

    public static long get() {
        return get(null);
    }

    /**
     * 获取id
     *
     * @param key
     * @return
     */
    public static long get(String key) {
        String seqName = isBlank(key) ? "xid" : key;
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("select seq_name, current_val, increment_val from " + table_name + " where seq_name = ? for update");
            ps.setObject(1, seqName);
            rs = ps.executeQuery();
            if (rs.next()) {
                long currValue = rs.getLong("current_val");
                int incrementVal = rs.getInt("increment_val");
                ps = conn.prepareStatement("update " + table_name + " set current_val = current_val + increment_val where seq_name = ?");
                ps.setObject(1, seqName);
                ps.execute();
                long id = currValue + incrementVal;
                conn.commit();
                return id;
            }
            throw new RuntimeException("[" + seqName + "] not found");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
    }

    private static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

}
