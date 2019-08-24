package com.zxy.client.dao;

import com.zxy.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

//业务层调用DAO层
public class Account extends BaseDao {
    public boolean userReg(User user){
        //用户注册
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO user(username, password, brief) "+"VALUES (?,?,?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getUserName());
            statement.setString(2,DigestUtils.md5Hex(user.getPassword()));
            statement.setString(3,user.getBrief());
            int rows = statement.executeUpdate();
            if(rows == 1){
                return true;
            }
        }catch (SQLException e){
            System.out.println("用户注册失败");
            e.printStackTrace();
        }finally {
            closeResource(connection,statement);
        }
        return false;
    }
    //用户登录
    public User userLogin(String userName,String password){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
           connection = getConnection();
           String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
           statement = connection.prepareStatement(sql);
           statement.setString(1,userName);
           statement.setString(2,DigestUtils.md5Hex(password));
           resultSet = statement.executeQuery();
           //从结果集变为对象
            if(resultSet.next()){
                User user = getUser(resultSet);
                return user;
            }
        }catch (SQLException e){
            System.out.println("用户登录失败");
            e.printStackTrace();

        }finally {
            closeResource(connection,statement,resultSet);
        }
        return null;
    }
    private User getUser(ResultSet resultSet) throws SQLException{
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("userName"));
        user.setPassword(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }

}
