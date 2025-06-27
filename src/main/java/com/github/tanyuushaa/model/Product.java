package com.github.tanyuushaa.model;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "GROUP_ID_FK"))
    private Group group;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private String description;

    public Product() {
        this("",null,"",0,0,"");
    }

    public Product(String name, Group group, String producer, double price, long amount, String description) {
        this.name = name;
        this.group = group;
        this.producer = producer;
        this.price = price;
        this.amount = amount;
        this.description = description;
    }

    public void changeAmount(long change){
        this.amount+=change;
        if (this.amount<0) this.amount = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", group=" + group +
                ", producer='" + producer + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                amount == product.amount &&
                Objects.equals(name, product.name) &&
                Objects.equals(group, product.group) &&
                Objects.equals(producer, product.producer) &&
                Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, group, producer, price, amount, description);
    }
}
