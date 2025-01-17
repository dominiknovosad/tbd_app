package com.example.tbd.product;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCompanyId(Integer companyId);
    List<Product> findAll();
    Optional<Product> findById(Integer id);
    @Query(value = "SELECT COUNT(*) FROM Product WHERE deleted = 'N'", nativeQuery = true)
    long countProducts();
}
