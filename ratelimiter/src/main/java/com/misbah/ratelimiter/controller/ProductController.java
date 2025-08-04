
package com.misbah.ratelimiter.controller;

import com.misbah.ratelimiter.dto.ProductInventoryDto;
import com.misbah.ratelimiter.model.Product;
import com.misbah.ratelimiter.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/v1/products")
    public List<Product> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/v1/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(product -> ResponseEntity.ok(product)) // If product is found, wrap it in a 200 OK response.
                .orElse(ResponseEntity.notFound().build()); // If not found, return a 404 Not Found response.
    }

    @GetMapping("/v2/products/{id}/inventory")
    public ResponseEntity<ProductInventoryDto> getProductInventory(@PathVariable Long id) {
        return productService.getProductInventoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/v1/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
}
