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
    private Long c_id;

    @Column(length = 100, nullable = false, unique = true)
    private String c_name;

    /*
    //=================== One to many ||| bidirectional ===================\\
    // - Must set the "One" entity explicitly in the "Many" entity, when it is added.

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "category", orphanRemoval = true, targetEntity = ProductEntity.class)
    private List<ProductEntity> products = new ArrayList<>();

    public void addProducts(ProductEntity... products) {
        this.products.addAll(Arrays.asList(products));
        //"One" is set in the "Many" entity
        Arrays.asList(products).forEach(product -> product.setCategory(this));
    }
    //adicional: constructor
    public CategoryEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    //============================================================================\\
    */

    @OneToMany(/*cascade = {CascadeType.ALL},*/fetch = FetchType.LAZY, mappedBy = "category", orphanRemoval = true, targetEntity = ProductEntity.class)
    private List<ProductEntity> products = new ArrayList<>();
    public void addProducts(ProductEntity... products) {
        this.products.addAll(Arrays.asList(products));
        //"One" is set in the "Many" entity
        Arrays.asList(products).forEach(product -> product.setCategory(this));
    }

    //=============================== Constructors ==================================\\

    public CategoryEntity() {
    }

    public CategoryEntity(Long id, String name) {
        this.c_id = id;
        this.c_name = name;
    }


    //=============================== Getters and Setters ==================================\\
    public Long getC_id() {
        return c_id;
    }

    public void setC_id(Long id) {
        this.c_id = id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String name) {
        this.c_name = name;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + c_id +
                ", name='" + c_name + '\'' +
//                ", products=" + products +
                '}';
    }
}