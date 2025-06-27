package com.github.tanyuushaa.server.impl;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.rep.ProductRep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    private ProductRep productRep;
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        productRep = mock(ProductRep.class);
        productService = new ProductServiceImpl(productRep);
    }


    @Test
    public void testSaveProductWithDuplicateNameReturnsError() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Milk");

        when(productRep.findProductByName("Milk")).thenReturn(existingProduct);

        Group dummyGroup = new Group();
        dummyGroup.setId(1L);

        Product newProduct = new Product();
        newProduct.setName("Milk");
        newProduct.setProducer("Farm");
        newProduct.setPrice(20.0);
        newProduct.setAmount(5);
        newProduct.setDescription("Fresh");
        newProduct.setGroup(dummyGroup);

        Response<Product> response = productService.save(newProduct);

        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("Product with name Milk already exists"));
        verify(productRep, never()).save(any(Product.class));
    }

    @Test
    void testReceiveProductWorks() {
        Product product = new Product("Sugar", null, "Refinery", 20.0, 5, "White sugar");
        product.setId(1L);
        when(productRep.findProductById(1L)).thenReturn(product);
        when(productRep.save(any())).thenReturn(product);

        Response<Product> response = productService.receiveProduct(1L, 3);

        assertTrue(response.isOkay());
        assertEquals(8, response.getObject().getAmount());
    }

    @Test
    void testConsumeProductFailsIfTooMuch() {
        Product product = new Product("Salt", null, "Sea", 10.0, 2, "Sea salt");
        product.setId(2L);
        when(productRep.findProductById(2L)).thenReturn(product);

        Response<Product> response = productService.consumeProduct(2L, 5);

        assertFalse(response.isOkay());
        assertTrue(response.getErrorMessage().contains("Invalid amount to consume"));
    }

    @Test
    void testSearchByKeywordFound() {
        Group grp = new Group("Grains", "Various grains");
        Product p = new Product("Buckwheat", grp, "Producer", 10.0, 50, "High quality");

        when(productRep.findProductsByNameContainsIgnoreCase("buck"))
                .thenReturn(Collections.singletonList(p));

        List<Product> result = productService.findAllByNameSnippet("buck");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Buckwheat", result.get(0).getName());
    }

    @Test
    void testSearchByKeywordEmpty() {
        when(productRep.findProductsByNameContainsIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

        List<Product> result = productService.findAllByNameSnippet("xyz");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
