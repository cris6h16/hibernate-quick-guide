package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity implements Serializable {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq")
//    @SequenceGenerator(name = "categories_id_seq", sequenceName = "categories_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    // One to many ||| unidirectional
    @OneToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_products_category_id"))
    private List<ProductEntity> products = new ArrayList<>();

    //=============================== Constructors ==================================\\

    public CategoryEntity() {
    }

    public CategoryEntity(Long id, String name, List<ProductEntity> products) {
        this.id = id;
        this.name = name;
    }


    //=============================== Getters and Setters ==================================\\
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

    //TODO: document this
    public void setProducts(List<ProductEntity> products) {
        this.products.addAll(products);
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
//                ", products=" + products +
                '}';
    }
}