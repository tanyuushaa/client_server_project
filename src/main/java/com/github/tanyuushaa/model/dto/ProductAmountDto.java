package com.github.tanyuushaa.model.dto;

public class ProductAmountDto {
    private long productId;
    private long amount;

    public ProductAmountDto() {
    }

    public ProductAmountDto(long productId, long amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
