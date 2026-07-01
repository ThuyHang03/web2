package com.web2th.th.controller;


import com.web2th.th.entity.Product;
import com.web2th.th.entity.User;
import com.web2th.th.repository.CategoryRepository;
import com.web2th.th.repository.OrderRepository;
import com.web2th.th.repository.ProductRepository;


import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;






@Controller
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;




  @GetMapping("/")
public String home(
        @RequestParam(defaultValue = "0") int page,
        Model model,
        HttpSession session
) {

    Pageable pageable = PageRequest.of(page, 8);

    Page<Product> productPage = repository.findAll(pageable);

    model.addAttribute("products", productPage.getContent());

    model.addAttribute("currentPage", page);

    model.addAttribute("totalPages", productPage.getTotalPages());

    model.addAttribute(
            "categories",
            categoryRepository.findAll()
    );

    User user = (User) session.getAttribute("user");

    if (user != null) {

        model.addAttribute(
                "orderCount",
                orderRepository
                        .findByUsername(user.getUsername())
                        .size()
        );
    }

    return "home";
}


    @GetMapping("/search")
    public String searchProduct(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, 8);

        Page<Product> productPage =
                repository.findByNameContainingIgnoreCase(
                        keyword,
                        pageable
                );

        model.addAttribute(
                "products",
                productPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                productPage.getTotalPages()
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        return "home";
    }


    // LỌC THEO DANH MỤC
    // =========================

    @GetMapping("/category/{id}")
    public String category(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, 8);

        Page<Product> productPage =
                repository.findByCategoryId(
                        id,
                        pageable
                );

        model.addAttribute(
                "products",
                productPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                productPage.getTotalPages()
        );

        model.addAttribute(
                "selectedCategory",
                id
        );

        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        return "home";
    }


    @GetMapping("/product/{id}")
    public String productDetail(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {

        Product product =
                repository.findById(id)
                        .orElse(null);

        if (product == null) {
            return "redirect:/";
        }

        model.addAttribute(
                "product",
                product
        );

        User user =
                (User) session.getAttribute("user");

        if (user != null) {

            model.addAttribute(
                    "orderCount",
                    orderRepository
                            .findByUsername(
                                    user.getUsername()
                            )
                            .size()
            );
        }

        return "product-detail";
    
}

}