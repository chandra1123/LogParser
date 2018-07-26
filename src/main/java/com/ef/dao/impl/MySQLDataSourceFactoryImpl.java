package com.ef.dao.impl;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.ef.dao.DataSourceFactory;
import com.mysql.cj.jdbc.MysqlDataSource;

public class MySQLDataSourceFactoryImpl implements DataSourceFactory {

    public DataSource getDataSource() throws Exception {
        Properties props = new Properties();
        MysqlDataSource mysqlDS = null;
        InputStream is = this.getClass().getResourceAsStream("/com/ef/db.properties");
        props.load(is);
        //System.out.println(props);
        mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
        mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        return mysqlDS;
    }
}
