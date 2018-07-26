package com.ef.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import com.ef.dao.BlockedIPDao;
import com.ef.domain.BlockedIP;

public class BlockedIPDAOImpl extends AbstractDao implements BlockedIPDao {
    
    public BlockedIPDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void insertBlockedIP(BlockedIP bockedIP) throws Exception {
        final String insertSql = "INSERT INTO blocked_ip(IP, REASON) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(insertSql);
            ps.setString(1, bockedIP.getIp());
            ps.setString(2, bockedIP.getReason());
            ps.executeUpdate();
            conn.commit();
        } finally {
            close(conn, ps);
        }
    }

    
}
