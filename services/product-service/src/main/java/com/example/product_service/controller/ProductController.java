package com.example.product_service.controller;

import com.example.product_service.dto.ProductRequestDTO;
import com.example.product_service.dto.ProductResponseDTO;
import com.example.product_service.dto.ProductUpdateDTO;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public String addProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
        return productService.addProduct(productRequestDTO);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProduct(@PathVariable Integer id){
        return productService.getProduct(id);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts(){
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@Valid @PathVariable Integer id, @RequestBody ProductUpdateDTO productUpdateDTO){
        return productService.updateProduct(id,productUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public ProductResponseDTO deleteProduct(@PathVariable Integer id){
        return productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> searchProducts(@RequestParam String keyword){
        return productService.searchProducts(keyword);
    }
}
