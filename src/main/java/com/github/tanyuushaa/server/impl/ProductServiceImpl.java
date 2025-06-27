package com.github.tanyuushaa.server.impl;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.rep.ProductRep;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.server.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRep productRepository;

    @Autowired
    public ProductServiceImpl(ProductRep productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Response<Product> save(Product product) {
        if (product == null) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("Product can't be null")));
        }
        List<String> errors = validateProduct(product);
        if (!errors.isEmpty()) {
            return new Response<>(product, errors);
        }
        if (productRepository.findProductByName(product.getName()) != null) {
            errors.add("Product with name " + product.getName() + " already exists");
            return new Response<>(product, errors);
        }

        return new Response<>(productRepository.save(product), new LinkedList<>());
    }

    @Override
    public List<String> validateProduct(Product product) {

        List<String> errors = new LinkedList<>();

        if (StringUtils.isAllBlank(product.getName())) {
            errors.add("Name cannot be empty");
        }
        if (product.getAmount() < 0) {
            errors.add("Amount cannot be < 0");
        }
        if (product.getPrice() < 0) {
            errors.add("Price cannot be < 0");
        }
        if (product.getGroup() == null || product.getGroup().getId() == 0) {
            errors.add("Select group");
        }

        return errors;
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public void delete(List<Product> productList) {
        productRepository.deleteAll(productList);
    }

    @Override
    public Product findProductById(long id) {
        return productRepository.findProductById(id);
    }

    @Override
    public List<Product> findProductByIdAsList(long id) {
        return productRepository.findAllById(id);
    }

    @Override
    public List<Product> findAllByGroup(Group group) {
        return productRepository.findAllByGroup(group);
    }

    @Override
    public List<Product> findAllByGroupId(long groupId) {
        return productRepository.findAllByGroup_Id(groupId);
    }


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public double findOverallCost(Product product) {
        return product.getAmount() * product.getPrice();
    }

    @Override
    public List<Product> findAllByNameSnippet(String snippet) {
        return productRepository.findProductsByNameContainsIgnoreCase(snippet);
    }

    @Override
    public List<Product> findAllByNameSnippetAndGroup(String snippet, long groupId) {
        return productRepository.findProductByNameContainsIgnoreCaseAndGroup_Id(snippet, groupId);
    }

    @Override
    public List<Product> findProductByIdAndGroup(long id, long groupId) {
        return productRepository.findProductByIdAndGroup_Id(id, groupId);
    }

    @Override
    public List<Product> searchProduct(long groupId, long productId, String nameSnippet) {
        List<Product> result = findAll();
        if (productId != 0 && groupId == 0) {
            result = findProductByIdAsList(productId);
        } else if (productId != 0) {
            result = findProductByIdAndGroup(productId, groupId);
        } else if (!StringUtils.isAllBlank(nameSnippet) && groupId != 0) {
            result = findAllByNameSnippetAndGroup(nameSnippet, groupId);
        } else if (!StringUtils.isAllBlank(nameSnippet)) {
            result = findAllByNameSnippet(nameSnippet);
        } else if (groupId != 0) {
            result = findAllByGroupId(groupId);
        }
        if (result == null) {
            result = new LinkedList<>();
        }

        return result;
    }

    @Override
    public BigDecimal findSumFotList(List<Product> products) {
        return productRepository.sumForList(products.stream().map(Product::getId)
                .collect(Collectors.toList()));
    }

    @Override
    public Response<Product> addProductAmount(long id, long amount) {
        if (id == 0) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("Product can't be null")));
        }
        Product product = findProductById(id);
        if (product == null) {
            return new Response<>(product,  new LinkedList<>(Collections.singleton("Product can't be null")));
        }
        if (amount < 0) {
            return new Response<>(product, new LinkedList<>(Collections.singleton("Amount can't be less than 0")));
        }

        product.setAmount(product.getAmount() + amount);
        return new Response<>(productRepository.save(product), new LinkedList<>());
    }

    @Override
    public Response<Product> withdrawProduct(long id, long amount) {
        if (id == 0) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("Product can't be null")));
        }
        Product product = findProductById(id);
        if (product == null) {
            return new Response<>(null,  new LinkedList<>(Collections.singleton("Product can't be null")));
        }
        if (amount > product.getAmount()) {
            return new Response<>(product, new LinkedList<>(Collections.singleton("Can't withdraw more than existing product amount")));
        }

        product.setAmount(product.getAmount() - amount);
        return new Response<>(productRepository.save(product), new LinkedList<>());
    }


    @Override
    public Response<Product> update(Product product) {

        List<String> errors = validateProduct(product);
        if (!errors.isEmpty()) {
            return new Response<>(product, errors);
        }

        Product sameNameProduct = productRepository.findProductByName(product.getName());
        if (sameNameProduct != null && sameNameProduct.getId() != product.getId()) {
            errors.add("Product with name " + product.getName() + " already exists");
            return new Response<>(product, errors);
        }

        Product productDB = productRepository.findProductById(product.getId());
        productDB.setName(product.getName());
        productDB.setGroup(product.getGroup());
        productDB.setPrice(product.getPrice());
        productDB.setAmount(product.getAmount());
        productDB.setProducer(product.getProducer());
        productDB.setDescription(product.getDescription());
        return new Response<>(productRepository.save(productDB), errors);

    }

    @Override
    public Response<Product> receiveProduct(long id, long amount) {
        Product product = productRepository.findProductById(id);
        if (product == null) {
            return new Response<>(null, List.of("Product not found"));
        }
        if (amount <= 0) {
            return new Response<>(product, List.of("Amount must be greater than 0"));
        }
        product.setAmount(product.getAmount() + amount);
        return new Response<>(productRepository.save(product), new LinkedList<>());
    }

    @Override
    public Response<Product> consumeProduct(long id, long amount) {
        Product product = productRepository.findProductById(id);
        if (product == null) {
            return new Response<>(null, List.of("Product not found"));
        }
        if (amount <= 0 || amount > product.getAmount()) {
            return new Response<>(product, List.of("Invalid amount to consume"));
        }
        product.setAmount(product.getAmount() - amount);
        return new Response<>(productRepository.save(product), new LinkedList<>());
    }
    @Override
    public List<Product> searchByKeyword(String keyword) {
        return productRepository.searchProducts(keyword);
    }

}
