package com.web2th.th.controller.admin;

import com.web2th.th.entity.User;
import com.web2th.th.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    private boolean isAdmin(HttpSession session){

        User user = (User) session.getAttribute("user");

        return user != null &&
                "ADMIN".equals(user.getRole());
    }

    @GetMapping("")
    public String users(
            HttpSession session,
            Model model
    ){

        if(!isAdmin(session)){
            return "redirect:/";
        }

        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        return "admin-users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            HttpSession session
    ){

        if(!isAdmin(session)){
            return "redirect:/";
        }

        userRepository.deleteById(id);

        return "redirect:/admin/users";
    }

}