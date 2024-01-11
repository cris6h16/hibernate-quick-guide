package org.example.Entities.OneToManyToOne_Unidirectional;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    //=================== One to many ||| Unidirectional - First Way ===================\\
    // All logic is in the "Many" entity
    //==================================================================================\\

    /*
    //=================== One to many ||| bidirectional - First Way ===================\\
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
    //=================================================================================\\



    //=================== One to many ||| bidirectional - Second Way ===================\\
    @OneToMany(/*cascade = {CascadeType.ALL},/* fetch = FetchType.LAZY, mappedBy = "category", orphanRemoval = true, targetEntity = ProductEntity.class)
    private List<ProductEntity> products = new ArrayList<>();
    //==================================================================================\\
    */


    //=============================== Constructors ==================================\\


    public CategoryEntity() {
    }

    public CategoryEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //============================ GETTERS & SETTERS ================================\\
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

    //=============================== toString ==================================\\

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}