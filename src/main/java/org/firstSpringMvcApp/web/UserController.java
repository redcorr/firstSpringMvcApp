package org.firstSpringMvcApp.web;

import org.firstSpringMvcApp.entity.User;
import org.firstSpringMvcApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    ModelAndView index(){
        return new ModelAndView("index.html");
    }

    @GetMapping("/register")
    ModelAndView register(){
        return new ModelAndView("register.html");
    }

    @PostMapping("/register")
    ModelAndView doRegister(@RequestParam("email") String email,@RequestParam("name") String name, @RequestParam("password") String password){
        String string = new String();
        if(userService.isRegistered(email, password)){
            string = "Already registered.";
        }else{
            userService.register(email,name,password);
            string = "Registered.";
        }
        return new ModelAndView("login.html");
    }

    @GetMapping("/login")
    ModelAndView login(){return new ModelAndView("login.html");}

    @PostMapping("/login")
    ModelAndView doLogin(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session){
        if(userService.isRegistered(email,password)){
            User user = userService.login(email,password);
            session.setAttribute("User",user);
            return new ModelAndView("redirect:/profile");
        }else{
            return new ModelAndView("register.html");
        }
    }

    @GetMapping("/profile")
    ModelAndView profile(HttpSession session){
        return new ModelAndView("profile.html");
    }
}
