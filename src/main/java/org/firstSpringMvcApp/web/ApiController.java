package org.firstSpringMvcApp.web;

import org.firstSpringMvcApp.entity.User;
import org.firstSpringMvcApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public DeferredResult<List<User>> users(){
        DeferredResult<List<User>> result = new DeferredResult<>(3000L);
        new Thread(()->{
            result.setResult(userService.getUsers());
        }).start();
        return result;
    }
//    public List<User> users(){
//        return userService.getUsers();
//    }

    @GetMapping("/user/{id}")
    public User user(@PathVariable("id") int id){
        return userService.getUserById(id);
    }

    @GetMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest){
        try{
            User user = userService.login(loginRequest.email, loginRequest.password);
            return Map.of("user", user);
        }catch(Exception e){
            return Map.of("error", "Login_Failed");
        }
    }

    public static class LoginRequest{
        String email;
        String password;
    }

}
