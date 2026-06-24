package com.web2th.th.controller;

import com.web2th.th.model.Product;
import com.web2th.th.model.User;
import com.web2th.th.repository.ProductRepository;
import com.web2th.th.repository.UserRepository;
import com.web2th.th.repository.OrderRepository;
import com.web2th.th.repository.OrderItemRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
private OrderRepository orderRepository;

@Autowired
private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ProductRepository productRepository;

    private boolean isAdmin(HttpSession session) {

        User user = (User) session.getAttribute("user");

        return user != null
                && "ADMIN".equals(user.getRole());
    }
 @GetMapping("/users")
public String adminUsers(
        HttpSession session,
        Model model
){

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    model.addAttribute(
            "users",
            userRepository.findAll()
    );

    return "admin-users";
}

@GetMapping("/user/delete/{id}")
public String deleteUser(
        @PathVariable Long id,
        HttpSession session
){

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    userRepository.deleteById(id);

    return "redirect:/admin/users";
}

    @GetMapping("/products")
    public String products(
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/";
        }

        model.addAttribute(
                "products",
                productRepository.findAll()
        );

        return "admin-products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            HttpSession session
    ) {

        if (!isAdmin(session)) {
            return "redirect:/";
        }

        productRepository.deleteById(id);

        return "redirect:/admin/products";
    }
    @GetMapping("/orders")
    public String adminOrders(
        HttpSession session,
        Model model
){

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    model.addAttribute(
            "orders",
            orderRepository.findAll()
    );

    return "admin-orders";
}
@GetMapping("/orders/{id}")
public String orderDetail(
        @PathVariable Long id,
        HttpSession session,
        Model model
){

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    model.addAttribute(
            "order",
            orderRepository.findById(id).orElse(null)
    );

    model.addAttribute(
            "items",
            orderItemRepository.findByOrderId(id)
    );

    return "admin-order-detail";
}






}