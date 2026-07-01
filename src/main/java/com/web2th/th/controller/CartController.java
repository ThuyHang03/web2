package com.web2th.th.controller;

import com.web2th.th.repository.ProductRepository;

import com.web2th.th.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;
import com.web2th.th.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.web2th.th.entity.CartItem;
import com.web2th.th.entity.Product;
import com.web2th.th.entity.User;
import com.web2th.th.repository.OrderItemRepository;


import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
   
    @Autowired
private OrderRepository orderRepository;
    @Autowired
    private ProductRepository repository;
   
    // =========================
    // THÊM VÀO GIỎ HÀNG
    // =========================
// @GetMapping("/my-orders")
// public String myOrders(
//         HttpSession session,
//         Model model
// ) {

//     User user =
//             (User) session.getAttribute("user");

//     if(user == null){
//         return "redirect:/login";
//     }

//     model.addAttribute(
//             "orders",
//             orderRepository.findByUsername(
//                     user.getUsername()
//             )
//     );

//     return "my-orders";
// }


    @GetMapping("/cart/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            HttpSession session
    ) {

        Product product = repository.findById(id).orElse(null);

        if(product == null){
            return "redirect:/product/" + id + "?success=true";
        }

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart == null){
            cart = new ArrayList<>();
        }

        boolean found = false;

        for(CartItem item : cart){

            if(item.getProduct().getId().equals(id)){

                item.setQuantity(
                        item.getQuantity() + 1
                );

                found = true;
                break;
            }
        }

        if(!found){
            cart.add(new CartItem(product, 1));
        }

        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    // =========================
    // XEM GIỎ HÀNG
    // =========================

    @GetMapping("/cart")
    public String cart(
            HttpSession session,
            Model model
    ) {

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart == null){
            cart = new ArrayList<>();
        }

        double total = 0;

        for(CartItem item : cart){
            total += item.getTotalPrice();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        User user =
        (User) session.getAttribute("user");

if(user != null){

    model.addAttribute(
            "orderCount",
            orderRepository
                    .findByUsername(
                            user.getUsername()
                    )
                    .size()
    );
}

        return "cart";
    }


    @GetMapping("/cart/increase/{id}")
    public String increaseQuantity(
            @PathVariable Long id,
            HttpSession session
    ) {

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart != null){

            for(CartItem item : cart){

                if(item.getProduct().getId().equals(id)){

                    item.setQuantity(
                            item.getQuantity() + 1
                    );

                    break;
                }
            }
        }

        return "redirect:/cart";
    }

  
    @GetMapping("/cart/decrease/{id}")
    public String decreaseQuantity(
            @PathVariable Long id,
            HttpSession session
    ) {

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart != null){

            cart.removeIf(item -> {

                if(item.getProduct().getId().equals(id)){

                    item.setQuantity(
                            item.getQuantity() - 1
                    );

                    return item.getQuantity() <= 0;
                }

                return false;
            });
        }

        return "redirect:/cart";
    }

  

    @GetMapping("/cart/remove/{id}")
    public String removeProduct(
            @PathVariable Long id,
            HttpSession session
    ) {

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart != null){

            cart.removeIf(
                    item -> item.getProduct()
                            .getId()
                            .equals(id)
            );
        }

        return "redirect:/cart";
    }


  @GetMapping("/checkout")
public String checkout(
        HttpSession session,
        Model model
) {

    if(session.getAttribute("user") == null){
        return "redirect:/login";
    }

        List<CartItem> cart =
                (List<CartItem>) session.getAttribute("cart");

        if(cart == null){
            cart = new ArrayList<>();
        }

        double total = 0;

        for(CartItem item : cart){
            total += item.getTotalPrice();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        User user =
        (User) session.getAttribute("user");

if(user != null){

    model.addAttribute(
            "orderCount",
            orderRepository
                    .findByUsername(
                            user.getUsername()
                    )
                    .size()
    );
}
        return "checkout";
    }

    // =========================
    // XỬ LÝ ĐẶT HÀNG
    // =========================

//    @PostMapping("/checkout/success")
// public String checkoutSuccess(

//         @RequestParam String customerName,
//         @RequestParam String phone,
//         @RequestParam String address,
//         @RequestParam String paymentMethod,

//         HttpSession session
// ) {

//     User user =
//             (User) session.getAttribute("user");

//     List<CartItem> cart =
//             (List<CartItem>) session.getAttribute("cart");

//     double total = 0;

//     if(cart != null){

//         for(CartItem item : cart){
//             total += item.getTotalPrice();
//         }
//     }

//     Order order = new Order();

//     order.setCustomerName(customerName);
//     order.setPhone(phone);
//     order.setAddress(address);
//     order.setPaymentMethod(paymentMethod);
//     if(paymentMethod.equals("COD")){
//     order.setPaymentStatus("Chưa thanh toán");
// }else{
//     order.setPaymentStatus("Đang chờ VNPay");
//     order.setStatus("Chờ xác nhận");
// }
//     order.setTotal(total);

//     if(user != null){
//         order.setUsername(user.getUsername());
//     }

//     order.setOrderDate(LocalDateTime.now());

// orderRepository.save(order);

// if(cart != null){

//     for(CartItem item : cart){

//         OrderItem orderItem = new OrderItem();

//         orderItem.setOrderId(order.getId());

//         orderItem.setProductName(
//                 item.getProduct().getName()
//         );

//         orderItem.setQuantity(
//                 item.getQuantity()
//         );

//         orderItem.setPrice(
//                 item.getProduct().getPrice()
//         );

//         orderItemRepository.save(orderItem);
//     }
// }

//     session.removeAttribute("cart");

//     return "success";
// }


}