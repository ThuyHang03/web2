package com.web2th.th.controller.admin;

import com.web2th.th.entity.Category;
import com.web2th.th.entity.Product;
import com.web2th.th.entity.User;
import com.web2th.th.repository.CategoryRepository;
import com.web2th.th.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

import com.web2th.th.repository.CategoryRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;
@Autowired
private CategoryRepository categoryRepository;
    private boolean isAdmin(HttpSession session) {

        User user = (User) session.getAttribute("user");

        return user != null &&
                "ADMIN".equals(user.getRole());
    }

    @GetMapping("")
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

    @GetMapping("/delete/{id}")
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

    @GetMapping("/search")
    public String search(
            @RequestParam String keyword,
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/";
        }

        model.addAttribute(
                "products",
                productRepository.findByNameContainingIgnoreCase(keyword)
        );

        model.addAttribute("keyword", keyword);

        return "admin-products";
    }
@GetMapping("/add")
public String addPage(
        HttpSession session,
        Model model
) {

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    model.addAttribute("product", new Product());
    model.addAttribute("categories", categoryRepository.findAll());

    return "add-product";
}

@PostMapping("/add")
public String saveProduct(

        HttpSession session,

        @RequestParam String name,
        @RequestParam Double price,
        @RequestParam String description,
        @RequestParam Long categoryId,
        @RequestParam(defaultValue = "0") Integer discountPercent,
        @RequestParam MultipartFile image

) {

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    try {

        String fileName = image.getOriginalFilename();

        String uploadDir = "src/main/resources/static/images/";

        Path path = Paths.get(uploadDir + fileName);

        Files.write(path, image.getBytes());

        Category category = categoryRepository
                .findById(categoryId)
                .orElse(null);

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setDiscountPercent(discountPercent);
        product.setImage(fileName);
        product.setCategory(category);

        productRepository.save(product);

    } catch (Exception e) {

        e.printStackTrace();

    }

    return "redirect:/admin/products";
}
@GetMapping("/edit/{id}")
public String editPage(
        @PathVariable Long id,
        HttpSession session,
        Model model
) {

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    Product product = productRepository.findById(id).orElse(null);

    if (product == null) {
        return "redirect:/admin/products";
    }

    model.addAttribute("product", product);
    model.addAttribute("categories", categoryRepository.findAll());

    return "edit-product";
}

@PostMapping("/edit/{id}")
public String updateProduct(

        @PathVariable Long id,

        HttpSession session,

        @RequestParam String name,
        @RequestParam Double price,
        @RequestParam String description,
        @RequestParam Long categoryId,
        @RequestParam Integer discountPercent,
        @RequestParam(required = false) MultipartFile image

) {

    if (!isAdmin(session)) {
        return "redirect:/";
    }

    Product product = productRepository.findById(id).orElse(null);

    if (product == null) {
        return "redirect:/admin/products";
    }

    product.setName(name);
    product.setPrice(price);
    product.setDescription(description);
    product.setDiscountPercent(discountPercent);

    Category category = categoryRepository
            .findById(categoryId)
            .orElse(null);

    product.setCategory(category);

    try {

        if (image != null && !image.isEmpty()) {

            String fileName = image.getOriginalFilename();

            String uploadDir = "src/main/resources/static/images/";

            Path path = Paths.get(uploadDir + fileName);

            Files.write(path, image.getBytes());

            product.setImage(fileName);

        }

    } catch (Exception e) {

        e.printStackTrace();

    }

    productRepository.save(product);

    return "redirect:/admin/products";
}
}