package com.example.Eshop.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Eshop.model.Product;
import com.example.Eshop.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final Path uploadDir = Paths.get("uploads");

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> create(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam String category,
            @RequestParam MultipartFile image
    ) {
        if (image.isEmpty() || name.isBlank() || description.isBlank() || category.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Files.createDirectories(uploadDir);
            String filename = System.currentTimeMillis() + "_" + sanitizeFilename(image.getOriginalFilename());
            Path filePath = uploadDir.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Product product = new Product(name, description, price, category, filename);
            return ResponseEntity.ok(productService.save(product));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam String category,
            @RequestParam(required = false) MultipartFile image
    ) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) return ResponseEntity.notFound().build();

        Product existing = optionalProduct.get();
        existing.setName(name);
        existing.setDescription(description);
        existing.setPrice(price);
        existing.setCategory(category);

        if (image != null && !image.isEmpty()) {
            try {
                if (existing.getImagePath() != null) {
                    Path oldImagePath = uploadDir.resolve(existing.getImagePath());
                    Files.deleteIfExists(oldImagePath);
                }

                String filename = System.currentTimeMillis() + "_" + sanitizeFilename(image.getOriginalFilename());
                Path newImagePath = uploadDir.resolve(filename);
                Files.copy(image.getInputStream(), newImagePath, StandardCopyOption.REPLACE_EXISTING);
                existing.setImagePath(filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.ok(productService.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) return ResponseEntity.notFound().build();

        Product product = optionalProduct.get();
        try {
            if (product.getImagePath() != null) {
                Path imagePath = uploadDir.resolve(product.getImagePath());
                Files.deleteIfExists(imagePath);
            }
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
    Path imagePath = uploadDir.resolve(sanitizeFilename(filename)).normalize();
    Resource resource = new UrlResource(imagePath.toUri());

    if (resource.exists() && resource.isReadable()) {
        String contentType = Files.probeContentType(imagePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                .body(resource);
    } else {
        return ResponseEntity.notFound().build();
    }
    } catch (MalformedURLException e) {
    e.printStackTrace();
    return ResponseEntity.internalServerError().build();
    } catch (IOException e) {
    e.printStackTrace();
    return ResponseEntity.internalServerError().build();
}
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}
