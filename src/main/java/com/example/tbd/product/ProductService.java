package com.example.tbd.product;


import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public long countProducts() {
        return productRepository.countProducts();
    }
}
