package com.ef.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import com.ef.dao.AccessLogDao;
import com.ef.domain.AccessLog;

public class AccessLogDaoImpl extends AbstractDao implements AccessLogDao {

    public AccessLogDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    public void insertAccessLog(AccessLog accesslog) throws Exception {
        final String insertSql = "INSERT INTO access_log(ACCESS_DATE, IP, REQUEST, STATUS, USER_AGENT)" + " VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(insertSql);
            ps.setString(1, accesslog.getDate());
            ps.setString(2, accesslog.getIp());
            ps.setString(3, accesslog.getRequest());
            ps.setString(4, accesslog.getStatus());
            ps.setString(5, accesslog.getUserAgent());
            ps.executeUpdate();
            conn.commit();
        } finally {
            close(conn, ps);
        }
    }

    public void insertAccessLogList(List<AccessLog> accesslogList) throws Exception {
        final String insertSql = "INSERT INTO access_log(ACCESS_DATE, IP, REQUEST, STATUS, USER_AGENT)" + " VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(insertSql);
            for (AccessLog accesslog : accesslogList) {
                ps.setString(1, accesslog.getDate());
                ps.setString(2, accesslog.getIp());
                ps.setString(3, accesslog.getRequest());
                ps.setString(4, accesslog.getStatus());
                ps.setString(5, accesslog.getUserAgent());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } finally {
            close(conn, ps);
        }
    }
    
    public void clearAccessLogs() throws Exception {
        final String truncateSql = "TRUNCATE access_log";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute(truncateSql);
        } finally {
            close(conn, stmt);
        }     
    }

}
