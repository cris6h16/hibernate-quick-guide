package org.example.Entities.OneToManyToOne_Unidirectional;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_seq")
//    @SequenceGenerator(name = "products_id_seq", sequenceName = "products_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO, test what exception is thrown FOR EACH METHOD
    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(precision = 7, scale = 2)
    private BigDecimal price;


    //=================== Many to One ||| Unidirectional - First Way ===================\\0
    @ManyToOne(
            //cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            targetEntity = CategoryEntity.class,
            optional = true)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    //==================================================================================\\

    /*
    //=================== One to many ||| bidirectional - First Way ===================\\
    // - Many is the owner of the relationship (have the @JoinColumn), Relationship is inverse
    // - Must add "MANY" entity explicitly in the "ONE" entity, when "ONE" is set (this can also be done in the "ONE")
    @ManyToOne(/*cascade = {CascadeType.ALL},/* fetch = FetchType.EAGER, targetEntity = CategoryEntity.class, optional = true)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    */

    /*
    //=================== One to many ||| bidirectional - Second Way ===================\\
    // - Many is the owner of the relationship (have the @JoinColumn), Relationship is inverse
    // - Must add "MANY" entity explicitly in the "ONE" entity, when "ONE" is set (this can also be done in the "ONE")
    @ManyToOne(/*cascade = {CascadeType.ALL},/* fetch = FetchType.EAGER, targetEntity = CategoryEntity.class, optional = true)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public void setCategory(CategoryEntity category) {
        this.category = category;
        category.getProducts().add(this);
    }
    */

    //=============================== Constructors ==================================\\

    public ProductEntity() {
    }

    public ProductEntity(Long id, String name, String description, BigDecimal price, CategoryEntity category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
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

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    //=============================== toString ==================================\\

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}
