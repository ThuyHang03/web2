package com.web2th.th.controller.admin;

import com.web2th.th.entity.Order;
import com.web2th.th.entity.OrderItem;
import com.web2th.th.entity.User;
import com.web2th.th.repository.OrderItemRepository;
import com.web2th.th.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private boolean isAdmin(HttpSession session){

        User user = (User) session.getAttribute("user");

        return user != null
                && "ADMIN".equals(user.getRole());
    }

  @GetMapping("")
public String orders(
        HttpSession session,
        Model model){

    if(!isAdmin(session)){
        return "redirect:/";
    }

    model.addAttribute(
            "orders",
            orderRepository.findAll()
    );

    return "admin-orders";
}

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            HttpSession session,
            Model model){

        if(!isAdmin(session)){
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
    @PostMapping("/confirm/{id}")
public String confirmOrder(
        @PathVariable Long id,
        HttpSession session
){

    if(!isAdmin(session)){
        return "redirect:/";
    }

    Order order = orderRepository.findById(id).orElse(null);

    if(order != null){
        order.setStatus("Đã xác nhận");
        orderRepository.save(order);
    }

    return "redirect:/admin/orders";
}

@PostMapping("/cancel/{id}")
public String cancelOrder(
        @PathVariable Long id,
        HttpSession session
){

    if(!isAdmin(session)){
        return "redirect:/";
    }

    Order order = orderRepository.findById(id).orElse(null);

    if(order != null){
        order.setStatus("Đã hủy");
        orderRepository.save(order);
    }

    return "redirect:/admin/orders";
}


}