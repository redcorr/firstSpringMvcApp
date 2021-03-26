package org.firstSpringMvcApp.service;

import org.firstSpringMvcApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Boolean isRegistered(String email, String password){
        try{
                String pd = jdbcTemplate.queryForObject("select password from users where email = ?", new Object[]{email}, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString("password");
                    }
                });
                if(pd.equals(password)){
                    return true;
                }else{return false;}
            }catch (Exception e){
            return false;
        }

    }

    public void register(String email, String name, String password){
        jdbcTemplate.update("insert into users (email,name,password) values (?,?,?)",new Object[]{email,name,password});
    }

    public void changePassword(String email, String password){
        jdbcTemplate.update("update users set password = ? where email = ?",new Object[]{password, email});
    }

    public String getNameByEmail(String email){
        String name = jdbcTemplate.queryForObject("select name from users where email = ?", new Object[]{email}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("name");
            }
        });
        return name;
    }

    public int getIdByEmail(String email){
        int id = jdbcTemplate.queryForObject("select id from users where email = ?", new Object[]{email}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("id");
            }
        });
        return id;
    }

    public User login(String email, String password){
        String name = getNameByEmail(email);
        int id = getIdByEmail(email);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setId(id);
        return user;
    }

    public List<User> getUsers(){
        return jdbcTemplate.query("select * from users",new BeanPropertyRowMapper<>(User.class));
    }

    public User getUserById(int id){
        return jdbcTemplate.queryForObject("select * from users where id = ?",new Object[]{id},new BeanPropertyRowMapper<>(User.class));
    }


}
