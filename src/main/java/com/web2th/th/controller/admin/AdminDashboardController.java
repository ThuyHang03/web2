package com.web2th.th.controller.admin;

import com.web2th.th.entity.User;
import com.web2th.th.entity.Order;
import com.web2th.th.entity.Product;
import com.web2th.th.repository.OrderRepository;
import com.web2th.th.repository.ProductRepository;
import com.web2th.th.repository.UserRepository;


import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private boolean isAdmin(HttpSession session) {

        User user = (User) session.getAttribute("user");

        return user != null &&
                "ADMIN".equals(user.getRole());
    }

    @GetMapping("")
public String dashboard(HttpSession session, Model model) {

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    long productCount = productRepository.count();
    long userCount = userRepository.count();
    long orderCount = orderRepository.count();
    long discountedProductCount = productRepository.findAll()
            .stream()
            .filter(p -> p.getDiscountPercent() != null && p.getDiscountPercent() > 0)
            .count();

    double revenue = orderRepository.findAll()
            .stream()
            .filter(o -> "Đã xác nhận".equals(o.getStatus()))
            .mapToDouble(o -> o.getTotal())
            .sum();

    List<Order> allOrders = orderRepository.findAll();
    long pendingOrderCount = allOrders.stream()
            .filter(o -> "Chờ xác nhận".equals(o.getStatus()))
            .count();
    long confirmedOrderCount = allOrders.stream()
            .filter(o -> "Đã xác nhận".equals(o.getStatus()))
            .count();
    long canceledOrderCount = allOrders.stream()
            .filter(o -> "Đã hủy".equals(o.getStatus()))
            .count();

    model.addAttribute("productCount", productCount);
    model.addAttribute("userCount", userCount);
    model.addAttribute("orderCount", orderCount);
    model.addAttribute("discountedProductCount", discountedProductCount);
    model.addAttribute("pendingOrderCount", pendingOrderCount);
    model.addAttribute("confirmedOrderCount", confirmedOrderCount);
    model.addAttribute("canceledOrderCount", canceledOrderCount);
    model.addAttribute("revenue", revenue);

    model.addAttribute(
            "recentOrders",
            allOrders
                    .stream()
                    .sorted(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .toList()
    );

    model.addAttribute(
            "recentProducts",
            productRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .toList()
    );

    model.addAttribute(
            "recentUsers",
            userRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(User::getId, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .toList()
    );

    return "admin";
}
}
