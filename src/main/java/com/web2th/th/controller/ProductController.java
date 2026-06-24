package com.web2th.th.controller;

import com.web2th.th.model.Category;
import com.web2th.th.model.Product;
import com.web2th.th.model.User;
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
import org.springframework.web.multipart.MultipartFile;



import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Controller
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =========================
    // TRANG CHỦ
    // =========================



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

    // =========================
    // TÌM KIẾM KHÁCH HÀNG
    // =========================

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

    // =========================
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

    // =========================
    // TRANG QUẢN TRỊ
    // =========================

    @GetMapping("/admin")
    public String admin(Model model) {

        model.addAttribute(
                "products",
                repository.findAll()
        );

        return "admin";
    }

    // =========================
    // TÌM KIẾM ADMIN
    // =========================

    @GetMapping("/admin/search")
    public String adminSearch(
            @RequestParam("keyword") String keyword,
            Model model
    ) {

        model.addAttribute(
                "products",
                repository.findByNameContainingIgnoreCase(keyword)
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "admin";
    }

    // =========================
    // THÊM SẢN PHẨM
    // =========================

    @GetMapping("/admin/add")
    public String addProductPage(Model model) {

        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        model.addAttribute(
                "product",
                new Product()
        );

        return "add-product";
    }

    @PostMapping("/admin/add")
    public String saveProduct(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") Integer discountPercent,
            @RequestParam MultipartFile image
    ) {

        try {

            String fileName =
                    image.getOriginalFilename();

            String uploadDir =
                    "src/main/resources/static/images/";

            Path path =
                    Paths.get(uploadDir + fileName);

            Files.write(path, image.getBytes());

            Category category =
                    categoryRepository
                            .findById(categoryId)
                            .orElse(null);

            Product product = new Product();

            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setImage(fileName);
            product.setCategory(category);
            product.setDiscountPercent(discountPercent);

            repository.save(product);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin";
    }

    // =========================
    // CẬP NHẬT SẢN PHẨM
    // =========================

    @PostMapping("/admin/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") Integer discountPercent,
            @RequestParam(required = false) MultipartFile image
    ) {

        Product product =
                repository.findById(id).orElse(null);

        if (product == null) {
            return "redirect:/admin";
        }

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setDiscountPercent(discountPercent);

        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElse(null);

        product.setCategory(category);

        try {

            if (image != null && !image.isEmpty()) {

                String fileName =
                        image.getOriginalFilename();

                String uploadDir =
                        "src/main/resources/static/images/";

                Path path =
                        Paths.get(uploadDir + fileName);

                Files.write(path, image.getBytes());

                product.setImage(fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        repository.save(product);

        return "redirect:/admin";
    }

    // =========================
    // XÓA SẢN PHẨM
    // =========================

    @GetMapping("/admin/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id
    ) {

        repository.deleteById(id);

        return "redirect:/admin";
    }

    // =========================
    // TRANG SỬA
    // =========================

    @GetMapping("/admin/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model
    ) {

        Optional<Product> product =
                repository.findById(id);

        if (product.isPresent()) {

            model.addAttribute(
                    "product",
                    product.get()
            );

            model.addAttribute(
                    "categories",
                    categoryRepository.findAll()
            );

            return "edit-product";
        }

        return "redirect:/admin";
    }

    // =========================
    // CHI TIẾT SẢN PHẨM
    // =========================

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