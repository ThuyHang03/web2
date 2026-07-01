package com.web2th.th.controller;

import com.web2th.th.entity.User;
import com.web2th.th.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // ======================
    // REGISTER
    // ======================

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(

            @RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String password,

            Model model
    ) {

        User existed =
                userRepository.findByUsername(username);

        if(existed != null){

            model.addAttribute(
                    "error",
                    "Tên đăng nhập đã tồn tại!"
            );

            return "register";
        }

        User user = new User();

        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        return "redirect:/login";
    }

    // ======================
    // LOGIN
    // ======================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(

            @RequestParam String username,
            @RequestParam String password,

            HttpSession session,
            Model model
    ) {

        User user =
                userRepository.findByUsernameAndPassword(
                        username,
                        password
                );

        if(user == null){

            model.addAttribute(
                    "error",
                    "Sai tài khoản hoặc mật khẩu!"
            );

            return "login";
        }

        session.setAttribute("user", user);

        return "redirect:/";
    }

    // LOGOUT


    @GetMapping("/logout")
    public String logout(
            HttpSession session
    ) {

        session.removeAttribute("user");

        return "redirect:/";
    }
}