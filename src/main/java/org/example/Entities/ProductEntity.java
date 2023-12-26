package org.example.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_seq")
    @SequenceGenerator(name = "products_id_seq", sequenceName = "products_id_seq", allocationSize = 50)
    private Integer id;

    //TODO, test what exception is thrown FOR EACH METHOD
    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(precision = 7, scale = 2)
    private BigDecimal price;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public ProductEntity(Integer id, String name, String description, BigDecimal price, CategoryEntity category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
}
