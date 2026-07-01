package com.web2th.th.controller;

import com.web2th.th.entity.CartItem;
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

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping("/my-orders")
    public String myOrders(
            HttpSession session,
            Model model
    ) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "orders",
                orderRepository.findByUsername(user.getUsername())
        );

        return "my-orders";
    }

    @PostMapping("/checkout/success")
    public String checkoutSuccess(

            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String paymentMethod,

            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("user");

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        double total = 0;

        if (cart != null) {

            for (CartItem item : cart) {
                total += item.getTotalPrice();
            }
        }

        Order order = new Order();

        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setTotal(total);

        order.setStatus("Chờ xác nhận");

        if (paymentMethod.equals("COD")) {
            order.setPaymentStatus("Chưa thanh toán");
        } else {
            order.setPaymentStatus("Đang chờ VNPay");
        }

        if (user != null) {
            order.setUsername(user.getUsername());
        }

        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);

        if (cart != null) {

            for (CartItem item : cart) {

                OrderItem orderItem = new OrderItem();

                orderItem.setOrderId(order.getId());
                orderItem.setProductName(item.getProduct().getName());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getProduct().getPrice());

                orderItemRepository.save(orderItem);
            }
        }

        session.removeAttribute("cart");

        return "success";
    }
}