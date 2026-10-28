package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    @Query("""
            SELECT p FROM Product p
            WHERE p.productActive=true
            AND p.productId=:id
            """)
    Optional<Product> findActiveProductById(Integer id);

    List<Product> findByProductActiveTrue();

    @Query("""
            SELECT p FROM Product p
            WHERE p.productActive=true
            AND(
                LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%'))
                OR LOWER(p.productDescription) LIKE LOWER(CONCAT('%',:keyword,'%'))
                OR LOWER(p.productCategory) LIKE LOWER(CONCAT('%',:keyword,'%'))
            )
            """)
    List<Product> searchActiveProducts(@Param("keyword") String keyword);
}
