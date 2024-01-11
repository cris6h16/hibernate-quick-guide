package org.example.DAOs.OneToMany_Unidirectional.Product;

import org.example.DAOs.OneToMany_Unidirectional.Category.CategoryDAO;
import org.example.DAOs.OneToMany_Unidirectional.Category.CategoryDAOImpl;
import org.example.Entities.OneToManyToOne_Unidirectional.ProductEntity;
import org.example.Entities.OneToManyToOne_Unidirectional.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOImplTest {

    public ProductDAO productDAO;
    public CategoryDAO categoryDAO;

    public ProductDAOImplTest() {
        productDAO = new ProductDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Test
    void persist() {
        ProductEntity product = new ProductEntity(null, "Product1", "product1 description", BigDecimal.valueOf(25.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category1");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        productDAO.getByIdEager(product.getId()).ifPresentOrElse(
                productEntity -> {
                    assertEquals(product.getName(), productEntity.getName());
                    assertEquals(product.getDescription(), productEntity.getDescription());
                    assertEquals(product.getCategory().getName(), productEntity.getCategory().getName());
                },
                () -> fail("Product not found")
        );
    }

    @Test
    void merge() {
        ProductEntity product = new ProductEntity(null, "Product2", "product2 description", BigDecimal.valueOf(250.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category2");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        product.setName("Product2 Updated");
        productDAO.merge(product);

        Optional<ProductEntity> productEntity = productDAO.findById(product.getId());
        assertTrue(productEntity.isPresent(), "Product with valid id must be present");
        assertTrue(productEntity.get().getName().equals("Product2 Updated"), "Product name must be updated");
    }

    @Test
    void deleteById() {
        ProductEntity product = new ProductEntity(null, "Product3", "product3 description", BigDecimal.valueOf(2500.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category3");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        productDAO.deleteById(product.getId());

        Optional<ProductEntity> productEntity = productDAO.findById(product.getId());
        assertTrue(productEntity.isEmpty(), "Product with valid id must be deleted");
    }

    @Test
    void findById() {
        ProductEntity product = new ProductEntity(null, "Product4", "product4 description", BigDecimal.valueOf(25000.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category4");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        Optional<ProductEntity> productEntity = productDAO.findById(product.getId());
        assertTrue(productEntity.isPresent(), "Product with valid id must be present");
        assertTrue(productEntity.get().getName().equals("Product4"), "Product name must be Product4");
    }

    @Test
    void listAll() {
        ProductEntity product = new ProductEntity(null, "Product5", "product5 description", BigDecimal.valueOf(2.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category5");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        assertTrue(productDAO.listAll().size() > 0, "Product list must be greater than 0");
    }

    @Test
    void getByIdEager() {
        ProductEntity product = new ProductEntity(null, "Product6", "product6 description", BigDecimal.valueOf(250.4), null);
        productDAO.persist(product);
        CategoryEntity category = new CategoryEntity(null, "Category6");
        categoryDAO.persist(category);

        product.setCategory(category);
        productDAO.merge(product);

        productDAO.getByIdEager(product.getId()).ifPresentOrElse(
                productEntity -> {
                    assertEquals(product.getName(), productEntity.getName());
                    assertEquals(product.getDescription(), productEntity.getDescription());
                    assertEquals(product.getCategory().getName(), productEntity.getCategory().getName());
                },
                () -> fail("Product not found")
        );
    }
}