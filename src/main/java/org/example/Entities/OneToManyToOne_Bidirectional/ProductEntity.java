package org.example.Entities.OneToManyToOne_Bidirectional;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity {
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_PRICE = "price";
    public static final String ATTR_CATEGORY = "category";
    public static final String FIELD_CATEGORY = "category_id";
    public static final String TABLE_NAME = "products";
    public static final String SCHEMA_NAME = "tienda";

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_seq")
//    @SequenceGenerator(name = "products_id_seq", sequenceName = "products_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private java.lang.Long id;

    //TODO, test what exception is thrown FOR EACH METHOD
    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(precision = 7, scale = 2)
    private BigDecimal price;

    //=================== One to many ||| bidirectional ===================\\
    // - Many is the owner of the relationship (have the @JoinColumn), Relationship is inverse
    // - Must add "MANY" entity explicitly in the "ONE" entity, when "ONE" is set (this can also be done in the "ONE")
    @ManyToOne(/*cascade = {CascadeType.ALL},*/ fetch = FetchType.EAGER, targetEntity = CategoryEntity.class, optional = true)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public void setCategory(CategoryEntity category) {
        this.category = category;
        category.getProducts().add(this);
    }

    //=============================== Constructors ==================================\\


    public ProductEntity() {
    }

    public ProductEntity(java.lang.Long id, String name, String description, BigDecimal price/*, CategoryEntity category*/) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
//        this.category = category;
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


    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price + '}';
    }
}
