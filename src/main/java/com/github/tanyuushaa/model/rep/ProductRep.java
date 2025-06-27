package com.github.tanyuushaa.model.rep;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRep extends JpaRepository<Product, Long> {

    Product findProductById(long id);

    Product findProductByName(String name);

    List<Product> findAllByGroup(Group group);

    List<Product> findAllByGroup_Id(long groupId);

    List<Product> findProductsByNameContainsIgnoreCase(String snippet);

    List<Product> findProductByNameContainsIgnoreCaseAndGroup_Id(String snippet, long groupId);

    List<Product> findProductByIdAndGroup_Id(long id, long groupId);

    List<Product> findAllById(long id);

    @Query(value = "select sum(p.amount * p.price)from products p where p.id in ?1", nativeQuery = true)
    BigDecimal sumForList(List<Long> productIds);

    @Query("SELECT p FROM products p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.producer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);


}
