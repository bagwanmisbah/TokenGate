
package com.misbah.ratelimiter.service;

import com.misbah.ratelimiter.dto.ProductInventoryDto;
import com.misbah.ratelimiter.model.Product;
import com.misbah.ratelimiter.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate stringRedisTemplate;


    public ProductService(ProductRepository productRepository, StringRedisTemplate stringRedisTemplate) {
        this.productRepository = productRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }


    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findProductById(Long id) {
        System.out.println("--- Cache miss for ID " + id + ". Fetching from DATABASE. ---");
        long startTime = System.currentTimeMillis();
        Optional<Product> product = productRepository.findById(id);

        long endTime = System.currentTimeMillis();
        System.out.println("--- Database fetch took: " + (endTime - startTime) + "ms ---");

        return product;
    }


    public Optional<ProductInventoryDto> getProductInventoryById(Long id) {

        String viewerKey = "product:viewers:" + id;

        Long liveViewers = stringRedisTemplate.opsForValue().increment(viewerKey);

        stringRedisTemplate.expire(viewerKey, Duration.ofSeconds(60));


        return productRepository.findById(id)
                .map(product -> new ProductInventoryDto(
                        product.getId(),
                        product.getPrice(),
                        product.getDescription(),
                        liveViewers != null ? liveViewers.intValue() : 0
                ));
    }


    public Product createProduct(Product product) {

        return productRepository.save(product);
    }
}