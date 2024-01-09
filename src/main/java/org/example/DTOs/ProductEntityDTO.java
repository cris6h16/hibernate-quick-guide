package org.example.DTOs;

import java.math.BigDecimal;

public class ProductEntityDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    private CategoryEntityDTO category;

    public ProductEntityDTO() {
    }

    public ProductEntityDTO(Long id, String name, String description, BigDecimal price, CategoryEntityDTO category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CategoryEntityDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryEntityDTO category) {
        this.category = category;
        category.getProducts().add(this);
    }
}
