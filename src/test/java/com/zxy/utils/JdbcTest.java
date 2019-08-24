package com.zxy.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class JdbcTest {
    private static DruidDataSource dataSource;
    //加载datasource
    static {
        Properties props = CommUtils.loadProperities("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //测试查询操作
    @Test
    public void testQuery(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //获取和数据池的连接
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM USER";
            statement = connection.prepareStatement(sql);
            //执行SQL
            resultSet = statement.executeQuery();
            //取得返回值
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("userName");
                String password = resultSet.getString("password");
                String brief = resultSet.getString("brief");
                System.out.println("id为:"+id+",用户名为:"+userName+",密码为:"+
                password+",简介为:"+brief);
            }
        }catch (SQLException e){

        }finally {
            closeResources(connection,statement,resultSet);
        }
    }
    @Test
    //插入
    public void testInsert(){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String password = DigestUtils.md5Hex("1234");
            String sql = "INSERT INTO user(username,password,brief)"+"VALUES (?,?,?)";
            //插入有返回值，返回受影响的行数Statement.RETURN_GENERATED_KEYS
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"test2");
            statement.setString(2,password);
            statement.setString(3,"第三名");
            int row = statement.executeUpdate();
            Assert.assertEquals(1,row);
        }catch (SQLException e){

        }finally {
            closeResources(connection,statement);
        }
    }

    @Test
    //测试登录
    public void testLogin(){
        String userName = "test2";
        String password = "81dc9bdb52d04dc20036dbd8313ed055";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM user WHERE username = '"+userName+"'"+
                    "AND password = '"+password+"'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                System.out.println("登录成功！");
            }else {
                System.out.println("登录失败");
            }

        }catch (SQLException e){

        }finally {
            closeResources(connection,statement,resultSet);
        }
    }


    //关闭资源---用于更新，删除，插入
    public void closeResources(Connection connection, Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!= null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeResources(Connection connection,
                               Statement statement,
                               ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
