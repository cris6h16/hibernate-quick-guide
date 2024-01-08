package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity implements Serializable {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_seq")
//    @SequenceGenerator(name = "products_id_seq", sequenceName = "products_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long p_id;

    //TODO, test what exception is thrown FOR EACH METHOD
    @Column(length = 100, nullable = false, unique = true)
    private String p_name;

    @Column(length = 100)
    private String p_description;

    @Column(precision = 7, scale = 2)
    private BigDecimal p_price;

    //=================== One to many ||| bidirectional ===================\\
    // - Many is the owner of the relationship (have the @JoinColumn), Relationship is inverse
    @ManyToOne(/*cascade = {CascadeType.ALL},*/ fetch = FetchType.EAGER, targetEntity = CategoryEntity.class, optional = true)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public void setCategory(CategoryEntity category) {
        this.category = category;
        category.getProducts().add(this);
    }


    public ProductEntity() {
    }

    public ProductEntity(Long id, String name, String description, BigDecimal price/*, CategoryEntity category*/) {
        this.p_id = id;
        this.p_name = name;
        this.p_description = description;
        this.p_price = price;
//        this.category = category;
    }

    //=============================== Getters and Setters ==================================\\

    public Long getP_id() {
        return p_id;
    }

    public void setP_id(Long id) {
        this.p_id = id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String name) {
        this.p_name = name;
    }

    public String getP_description() {
        return p_description;
    }

    public void setP_description(String description) {
        this.p_description = description;
    }

    public BigDecimal getP_price() {
        return p_price;
    }

    public void setP_price(BigDecimal price) {
        this.p_price = price;
    }

    public CategoryEntity getCategory() {
        return category;
    }


    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + p_id +
                ", name='" + p_name + '\'' +
                ", description='" + p_description + '\'' +
                ", price=" + p_price + '}';
    }
}
