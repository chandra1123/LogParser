package com.ef.dao;

import javax.sql.DataSource;

public interface DataSourceFactory {
    public DataSource getDataSource() throws Exception;
}
