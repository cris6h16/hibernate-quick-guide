package org.example.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq")
//    @SequenceGenerator(name = "categories_id_seq", sequenceName = "categories_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "category", fetch = FetchType.LAZY)
    private List<ProductEntity> products = new ArrayList<>();

    public CategoryEntity() {
    }

    public CategoryEntity(Long id, String name, List<ProductEntity> products) {
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

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}