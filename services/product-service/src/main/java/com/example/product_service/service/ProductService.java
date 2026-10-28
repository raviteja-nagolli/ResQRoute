package com.example.product_service.service;

import com.example.product_service.dto.ProductRequestDTO;
import com.example.product_service.dto.ProductResponseDTO;
import com.example.product_service.dto.ProductUpdateDTO;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.ProductNotFoundException;
import com.example.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductResponseDTO mapToResponse(Product product){
        ProductResponseDTO responseDTO=new ProductResponseDTO();
        responseDTO.setProductId(product.getProductId());
        responseDTO.setProductName(product.getProductName());
        responseDTO.setProductDescription(product.getProductDescription());
        responseDTO.setProductPrice(product.getProductPrice());
        responseDTO.setProductCategory(product.getProductCategory());
        responseDTO.setProductActive(product.getProductActive());
        return responseDTO;
    }

    public String addProduct(ProductRequestDTO productRequestDTO){
        log.debug("Adding new product: productName={}, productDescription={}",productRequestDTO.getProductName(),productRequestDTO.getProductDescription());
        Product product=new Product();
        product.setProductName(productRequestDTO.getProductName());
        product.setProductDescription(productRequestDTO.getProductDescription());
        product.setProductPrice(productRequestDTO.getProductPrice());
        product.setProductCategory(productRequestDTO.getProductCategory());
        product.setProductActive(true);
        Product saved=productRepository.save(product);
        log.info("Product added: productId={}, productName={}",saved.getProductId(),saved.getProductName());
        return "Product Added Successfully.";
    }

    public ProductResponseDTO getProduct(Integer id){
        log.debug("Fetching product: productId={}",id);
        Product product=productRepository.findActiveProductById(id).orElseThrow(()->{
                log.warn("Active product not found: productId={}",id);
                return new ProductNotFoundException("Product with id "+id+" not found");
        });
        log.debug("Active product found: productId={}",id);
        return mapToResponse(product);
    }

    public List<ProductResponseDTO> getAllProducts(){
        log.debug("Fetching all products");
        return productRepository.findByProductActiveTrue().stream().map(this::mapToResponse).toList();
    }

    public ProductResponseDTO updateProduct(Integer id, ProductUpdateDTO productUpdateDTO){
        log.debug("Updating product: productId={}",id);
        Product product=productRepository.findById(id).orElseThrow(()-> {
            log.warn("Active product not found for update: productId={}",id);
            return new ProductNotFoundException("Product with id " + id + " not found");
        });
        product.setProductName(productUpdateDTO.getProductName());
        product.setProductDescription(productUpdateDTO.getProductDescription());
        product.setProductPrice(productUpdateDTO.getProductPrice());
        product.setProductCategory(productUpdateDTO.getProductCategory());
        product.setProductActive(productUpdateDTO.getProductActive());
        productRepository.save(product);
        log.info("Updated product: productId={}",id);
        return mapToResponse(product);
    }

    public ProductResponseDTO deleteProduct(Integer id){
        log.debug("Deleting product: productId={}",id);
        Product product=productRepository.findById(id).orElseThrow(()-> {
            log.warn("Active product not found for delete: productId={}",id);
            return new ProductNotFoundException("Product with id " + id + " not found");
        });
        product.setProductActive(false);
        productRepository.save(product);
        log.info("Deleted product: productId={}",id);
        return mapToResponse(product);
    }

    public List<ProductResponseDTO> searchProducts(String keyword){
        log.debug("Searching product: keyword={}",keyword);
        return productRepository.searchActiveProducts(keyword).stream().map(this::mapToResponse).toList();
    }
}
