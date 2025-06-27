package com.github.tanyuushaa.controll;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.dto.ProductAmountDto;
import com.github.tanyuushaa.server.GroupService;
import com.github.tanyuushaa.server.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ApplicationController {

    private final ProductService productService;
    private final GroupService groupService;

    @Autowired
    public ApplicationController(ProductService productService, GroupService groupService) {
        this.productService = productService;
        this.groupService = groupService;
    }

    @GetMapping("/")
    public String editProduct() {
        return "login";
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        model.addAttribute("groups", groupService.findAll());
        return "groups";
    }

    @PostMapping("/request-delete-group")
    public String requestDeleteGroup(@ModelAttribute("groupID") long id, Model model) {
        groupService.delete(groupService.findGroupById(id));
        return "redirect:/groups";
    }

    @GetMapping("/add-group")
    public String addGroup(Model model) {
        if (model.getAttribute("group") == null) {
            model.addAttribute("group", new Group());
        }
        return "group-add";
    }

    @PostMapping("/request-add-group")
    public String requestAddGroup(@ModelAttribute Group group, Model model) {
        Response<Group> groupResponse = groupService.save(group);
        if (!groupResponse.isOkay()) {
            model.addAttribute("errors", groupResponse.getErrorMessage());
            return addGroup(model);
        }
        return "redirect:/groups";
    }

    @GetMapping("/products-find")
    public String productsByGroup(@ModelAttribute("groupId") Long groupId,
                                  @ModelAttribute("findProductId") Long searchedProductId,
                                  @ModelAttribute("findProductName") String productNameSnippet,
                                  Model model) {
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("groupId", groupId);
        model.addAttribute("findProductId", searchedProductId);
        model.addAttribute("findProductName", productNameSnippet);

        List<Product> productsResult = productService.searchProduct(groupId, searchedProductId, productNameSnippet);
        model.addAttribute("products", productsResult);

        BigDecimal value = productService.findSumFotList(productsResult);
        model.addAttribute("value", value == null ? 0 : value);
        return "products";
    }

    @GetMapping("/products")
    public String products(Model model) {
        return productsByGroup(0L, 0L, "", model);
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        if (model.getAttribute("product") == null) {
            model.addAttribute("product", new Product());
            model.addAttribute("groupId", 0);
        }
        model.addAttribute("groups", groupService.findAll());
        return "product-add";
    }

    @PostMapping("/request-add-product")
    public String requestAddProduct(@ModelAttribute Product product, @ModelAttribute("groupId") Long groupId, Model model) {
        if (groupId == 0) {
            model.addAttribute("errors", new LinkedList<>(Collections.singleton("Select group")));
            model.addAttribute("product", product);
            return addProduct(model);
        }
        product.setGroup(groupService.findGroupById(groupId));
        Response<Product> productResponse = productService.save(product);
        if (!productResponse.isOkay()) {
            model.addAttribute("errors", productResponse.getErrorMessage());
            model.addAttribute("product", product);
            return addProduct(model);
        }
        return "redirect:/products";
    }

    @GetMapping("/edit-group")
    public String editGroup(@ModelAttribute("groupID") long id, Model model) {
        model.addAttribute("group", groupService.findGroupById(id));
        return "group-edit";
    }


    @PostMapping("/request-edit-group")
    public String requestEditGroup(@ModelAttribute Group group, Model model) {
        Response<Group> groupResponse = groupService.update(group);
        if (!groupResponse.isOkay()) {
            model.addAttribute("errors", groupResponse.getErrorMessage());
            return editGroup(group.getId(), model);
        }
        return "redirect:/groups";
    }

    @GetMapping("/edit-product")
    public String editProduct(@ModelAttribute("productID") long id, Model model) {
        Product product = productService.findProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("groupId", product.getGroup().getId());
        return "product-edit";
    }


    @PostMapping("/request-edit-product")
    public String requestEditProduct(@ModelAttribute Product product, @ModelAttribute("groupId") Long groupId, Model model) {
        product.setGroup(groupService.findGroupById(groupId));
        Response<Product> productResponse = productService.update(product);
        if (!productResponse.isOkay()) {
            model.addAttribute("errors", productResponse.getErrorMessage());
            return editProduct(product.getId(), model);
        }
        return "redirect:/products";
    }


    @PostMapping("/request-delete-product")
    public String requestDeleteProduct(@ModelAttribute("productID") long id, Model model) {
        productService.delete(productService.findProductById(id));
        return "redirect:/products";
    }

    @GetMapping("/arrival-product")
    public String openAcceptMoreProductPage(@ModelAttribute("productID") long productId, Model model){
        Product product = productService.findProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("arrivedAmount", 0L);
        return "arrival-product";
    }

    @PostMapping("/arrival-product")
    public String acceptMoreProduct(@ModelAttribute("productID") long id, @ModelAttribute("arrivedAmount") long amount, Model model) {
        Response<Product> productResponse = productService.addProductAmount(id, amount);
        if (!productResponse.isOkay()) {
            model.addAttribute("errors", productResponse.getErrorMessage());
            return openAcceptMoreProductPage(id, model);
        }
        return "redirect:/products";
    }

    @GetMapping("/withdraw-product")
    public String openWithdrawProductPage(@ModelAttribute("productID") long productId, Model model){
        Product product = productService.findProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("withdrawnAmount", 0L);
        return "withdraw-product";
    }

    @PostMapping("/withdraw-product")
    public String withdrawProductPage(@ModelAttribute("productID") long id, @ModelAttribute("withdrawnAmount") long amount, Model model) {
        Response<Product> productResponse = productService.withdrawProduct(id, amount);
        if (!productResponse.isOkay()) {
            model.addAttribute("errors", productResponse.getErrorMessage());
            return openWithdrawProductPage(id, model);
        }
        return "redirect:/products";
    }

    @PostMapping("/product/receive")
    public ResponseEntity<Response<Product>> receiveProduct(@RequestBody ProductAmountDto dto) {
        return ResponseEntity.ok(productService.receiveProduct(dto.getProductId(), dto.getAmount()));
    }

    @PostMapping("/product/consume")
    public ResponseEntity<Response<Product>> consumeProduct(@RequestBody ProductAmountDto dto) {
        return ResponseEntity.ok(productService.consumeProduct(dto.getProductId(), dto.getAmount()));
    }

    @GetMapping("/products/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchByKeyword(keyword);
    }

    @GetMapping("/groups/search")
    @ResponseBody
    public List<Group> searchGroups(@RequestParam String keyword) {
        return groupService.searchByKeyword(keyword);
    }


}
