package com.ef.dao;

import java.util.List;

import com.ef.domain.AccessLog;

public interface AccessLogDao {
    public void insertAccessLog(AccessLog accesslog) throws Exception;
    public void insertAccessLogList(List<AccessLog> accesslogList) throws Exception;
    public void clearAccessLogs() throws Exception;
}
