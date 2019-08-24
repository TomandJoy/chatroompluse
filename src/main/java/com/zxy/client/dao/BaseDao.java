package com.zxy.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zxy.utils.CommUtils;

import java.sql.*;
import java.util.Properties;

/**
 * 公共部分：
 * 1.加载数据源
 * 2.获取连接
 * 3.关闭资源
 */

public class BaseDao {
    //加载数据源  private是因为所以子类不需要获取连接
    private static DruidDataSource dataSource;
    //为了保证数据源在类加载的时候就加载，使用static静态代码块，并且只执行一次
    static {
        //获取数据源的配置文件
        Properties properties = CommUtils.loadProperities("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            System.out.println("加载数据源失败");
            e.printStackTrace();
        }

    }
    //获取连接
    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        } catch (SQLException e) {
            System.out.println("数据库链接获取失败");
            e.printStackTrace();
        }
        return null;
    }
   //更新操作
    protected void closeResource(Connection connection, Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //查询操作
    protected void closeResource(Connection connection,Statement statement,ResultSet resultSet){
        closeResource(connection,statement);
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
