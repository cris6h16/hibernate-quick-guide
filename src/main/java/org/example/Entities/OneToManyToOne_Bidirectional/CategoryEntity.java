package org.example.Entities.OneToManyToOne_Bidirectional;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity {
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_PRODUCTS = "products";
    public static final String TABLE_NAME = "categories";
    public static final String SCHEMA_NAME = "tienda";

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq")
//    @SequenceGenerator(name = "categories_id_seq", sequenceName = "categories_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private java.lang.Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    /*
    //=================== One to many ||| bidirectional ===================\\
    // - Must set the "One" entity explicitly in the "Many" entity, when it is added.(If we don't do this here we must do it in the "Many" entity)

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

    //=============================== Constructors ==================================\\

    public CategoryEntity() {
    }

    public CategoryEntity(java.lang.Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryEntity(java.lang.Long c_id, String c_name, List<ProductEntity> c_products) {
        this.id = c_id;
        this.name = c_name;
        this.products = c_products;
    }

    //=============================== Getters and Setters ==================================\\
    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
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

    public void setProductsList(List<ProductEntity> products) {
        this.products = products;
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