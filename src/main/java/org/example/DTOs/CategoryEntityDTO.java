package org.example.DTOs;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntityDTO {
    private Long id;
    private String name;
    private List<ProductEntityDTO> products = new ArrayList<>();

    public CategoryEntityDTO() {
    }

    public CategoryEntityDTO(Long id, String name, List<ProductEntityDTO> products) {
        this.id = id;
        this.name = name;
        this.products = products;
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

    public List<ProductEntityDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntityDTO> products) {
        this.products = products;
    }
}
