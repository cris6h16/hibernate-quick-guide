package org.example.DAOs;

import org.example.Entities.CategoryEntity;
import org.example.Entities.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class CategoryDAOTest {
    protected static CategoryDAO categoryDAO;

    // Attributes for testing
    protected static Long categoryId;
    protected static String categoryName;

    public CategoryDAOTest() {
        categoryDAO = new CategoryDAOImpl();
    }

    //    @BeforeAll
//    static void beforeAllSetDAO() {
//        categoryDAO = new CategoryDAOImpl();
//    }

    @BeforeAll
    public static void setUpTestData() {
        CategoryEntity categoryForTesting = new CategoryEntity(null, "CategoryForTesting", null);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Delete CategoryForTesting if it exists
            session.beginTransaction();
            session.createQuery("from CategoryEntity c where c.name = :name", CategoryEntity.class)
                    .setParameter("name", categoryForTesting.getName())
                    .getResultList()
                    .forEach(session::detach); // session.delete is deprecated
            session.getTransaction().commit();

            // Persist CategoryForTesting
            session.beginTransaction();
            session.persist(categoryForTesting);
            session.getTransaction().commit();

            // Set values in test attributes
            categoryId = categoryForTesting.getId();
            categoryName = categoryForTesting.getName();
        }
    }

    //=============================== categoryDAO.findById(Long id) ===============================\\
    @Test
    @DisplayName("findById by valid Id NO EAGER FETCHING")
    void getByIdNoEagerValidId() {
        Long id = categoryId;
        Optional<CategoryEntity> category = categoryDAO.findById(id);
        assertTrue(category.isPresent(), "Category with Id " + id + " should be present");
        assertThrows(LazyInitializationException.class, () -> category.get().getProducts().isEmpty(), "Products of Category with Id " + id + " shouldn't be null");
    }

    @Test
    @DisplayName("Find category by valid ID")
    void findByIdValid() {
        // categoryId, categoryName are set in setUpTestData()
        Long validId = categoryId;
        Optional<CategoryEntity> category = categoryDAO.findById(validId);
        assertTrue(category.isPresent(), "Category with ID " + validId + " should be present");
        assertEquals(categoryName, category.get().getName(), "Category with ID " + validId + " should be " + categoryName);
    }

    @Test
    @DisplayName("Find category by null ID")
    void findByIdNull() {
        Long nullId = null;
        Optional<CategoryEntity> category = categoryDAO.findById(nullId);
        assertTrue(category.isEmpty(), "Category with ID " + nullId + " should be empty");
    }

    @Test
    @DisplayName("Find category by Long.MAX_VALUE ID")
    void findByIdMaxValue() {
        Long maxValueId = Long.MAX_VALUE;
        Optional<CategoryEntity> category = categoryDAO.findById(maxValueId);
        assertFalse(category.isPresent(), "Category with ID " + maxValueId + " shouldn't be present");
    }

    @Test
    @DisplayName("Find category by Long.MIN_VALUE ID")
    void findByIdMinValue() {
        Long minValueId = Long.MIN_VALUE;
        Optional<CategoryEntity> category = categoryDAO.findById(minValueId);
        assertFalse(category.isPresent(), "Category with ID " + minValueId + " shouldn't be present");
    }


    //=============================== categoryDAO.listAll() ===============================\\
    @Test
    @DisplayName("List all categories when multiple categories exist")
    void listAllMultipleCategories() {
        List<CategoryEntity> categories = categoryDAO.listAll();
        assertFalse(categories.isEmpty(), "List of categories should not be empty");
    }

    //=============================== categoryDAO.listAllWithEmptyRows() ===============================\\

    @Test
    @DisplayName("List all categories when no category exists")
    void listAllNoCategory() {
        List<CategoryEntity> noCategories = categoryDAO.listAllWithEmptyRows();
        assertTrue(noCategories.isEmpty(), "List of categories should be empty");
    }

    //=============================== categoryDAO.findByName(String name) ===============================\\
    @Test
    @DisplayName("Find by Non-existent Name")
    void findByNonExistentName() {
        String name = "HelloWord123";
        Optional<CategoryEntity> category = categoryDAO.findByName(name);
        assertTrue(category.isEmpty(), "Category with name: " + name + " shouldn't exist");
    }

    @Test
    @DisplayName("Find by null Name")
    void findByNullName() {
        String name = null;
        Optional<CategoryEntity> category = categoryDAO.findByName(name);
        assertTrue(category.isEmpty(), "Category with name: " + name + " shouldn't exist");
    }

    @Test
    @DisplayName("Find by empty Name")
    void findByEmptyName() {
        String name = "";
        Optional<CategoryEntity> category = categoryDAO.findByName(name);
        assertTrue(category.isEmpty(), "Category with name: " + name + " shouldn't exist");
    }

    @Test
    @DisplayName("Find by valid Name")
    void findByValidName() {
        String name = categoryName;
        Optional<CategoryEntity> category = categoryDAO.findByName(name);
        assertTrue(category.isPresent(), "Category with name: " + name + " should exist");
    }

    //=============================== categoryDAO.save(CategoryEntity category) ===============================\\
    @Test
    @DisplayName("Save category with null name")
    void saveNullName() {
        CategoryEntity category = new CategoryEntity(null, null, null);
        categoryDAO.save(category);
        assertNull(category.getId(), "Category with null name shouldn't be saved");
    }

    @Test
    @DisplayName("Save category with empty name")
    void saveEmptyName() {
        CategoryEntity category = new CategoryEntity(null, "", null);
        categoryDAO.save(category);
        assertNull(category.getId(), "Category with empty name shouldn't be saved");
    }

    @Test
    @DisplayName("Save category with valid name")
    void saveValidName() {
        CategoryEntity category = new CategoryEntity(null, "Food", null);
        categoryDAO.save(category);
        assertNotNull(category.getId(), "Category with valid name should be saved");
    }

    @Test
    @DisplayName("Save category with id and valid name")
    void saveIdAndValidName() {
        CategoryEntity category = new CategoryEntity(9999999L, "FoodTest", null);
        categoryDAO.save(category);

        Optional<CategoryEntity> categoryOp = categoryDAO.findById(9999999L);
        assertTrue(categoryOp.isEmpty(), "Category tried save with id mustn't be saved");
    }

    @Test
    @DisplayName("Save category with valid name and products")
    void saveValidNameAndProductsCascadePersist() {

        ProductEntity product = new ProductEntity(null, "Hp Victus 15", "Laptop gamer, Model: fb-0028nr", null, null);
        ProductEntity product2 = new ProductEntity(null, "Generic Mechanical keyboard", "Laptop gamer, Model: Logitech", null, null);
        CategoryEntity category = new CategoryEntity(null, "Laptops", null);
        category.addProducts(product2, product);

        categoryDAO.save(category);

        Optional<CategoryEntity> categoryOp = categoryDAO.getByIdEager(category.getId());
        assertTrue(categoryOp.isPresent(), "Category with valid name and products should be saved");

        category = categoryOp.get();
        assertNotNull(category.getId(), "Category with valid name and products should be saved");
        assertEquals(2, category.getProducts().size(), "Category should have 2 products");
        assertNotNull(category.getProducts().get(0).getId(), "Product with valid name and products should be saved");
    }


    //=============================== categoryDAO.update(CategoryEntity category) ===============================\\
    @Test
    @DisplayName("Update category with null name")
    void updateNullName() {
        CategoryEntity category = new CategoryEntity(categoryId, null, null);
        boolean updated = categoryDAO.update(category);
        assertFalse(updated, "Category with null name shouldn't be updated");
    }

    @Test
    @DisplayName("Update category with empty name")
    void updateEmptyName() {
        CategoryEntity category = new CategoryEntity(categoryId, "", null);
        boolean updated = categoryDAO.update(category);
        assertFalse(updated, "Category with empty name shouldn't be updated");
    }

    @Test
    @DisplayName("Update category with null ID")
    void updateNullId() {
        CategoryEntity category = new CategoryEntity(null, "UpdatedName", null);
        boolean updated = categoryDAO.update(category);
        assertFalse(updated, "Category with null ID shouldn't be updated");
    }

    @Test
    @DisplayName("Update category with valid name")
    void updateValid() {
        Optional<CategoryEntity> categoryOp = categoryDAO.getByIdEager(categoryId);
        assertTrue(categoryOp.isPresent(), "Category valid id must be present");

        CategoryEntity categoryEager = categoryOp.get();
        categoryEager.setName("UpdatedName");
        boolean updated = categoryDAO.update(categoryEager);
        assertTrue(updated, "Category with valid name should be updated");

        categoryEager.setName(categoryName);
        categoryDAO.update(categoryEager);
        boolean updated2 = categoryDAO.update(categoryEager);
        assertTrue(updated2, "Category with valid name should be updated Again");
    }

    @Test
    @DisplayName("Update category with valid name and Lazy Products")
    void updateValidNameLazyProducts() {
        Optional<CategoryEntity> categoryOp = categoryDAO.findById(categoryId);
        assertTrue(categoryOp.isPresent(), "Category valid id must be present");

        CategoryEntity categoryEager = categoryOp.get();
        categoryEager.setName("UpdatedName");
        boolean updated = categoryDAO.update(categoryEager);
        assertTrue(updated, "Category with valid name should be updated");

        categoryEager.setName(categoryName);
        categoryDAO.update(categoryEager);
        boolean updated2 = categoryDAO.update(categoryEager);
        assertTrue(updated2, "Category with valid name should be updated Again");
    }

    @Test
    @DisplayName("Update category with valid name and products")
    void updateValidNameAndProductsCascadePersist() {
        ProductEntity product = new ProductEntity(null, "SSD 2tb", "ssd description", null, null);
        ProductEntity product2 = new ProductEntity(null, "Motherboard", "motherboard description", null, null);

        CategoryEntity category = new CategoryEntity(null, "Hardware", null);
        category.addProducts(product, product2);
        categoryDAO.save(category);

        assertNotNull(category.getId(), "Category with valid name and products should be saved");

        category.setName("UpdatedName");
        boolean updated = categoryDAO.update(category);
        assertTrue(updated, "Category with valid name and products should be updated");

        category.setName("Hardware");
        categoryDAO.update(category);
        boolean updated2 = categoryDAO.update(category);
        assertTrue(updated2, "Category with valid name and products should be updated Again");
    }

    @Test
    @DisplayName("Update products category")
    void updateProductCategory() {
        ProductEntity product = new ProductEntity(null, "SSD 2tb", "ssd description", null, null);
        ProductEntity product2 = new ProductEntity(null, "Motherboard", "motherboard description", null, null);
        CategoryEntity category = new CategoryEntity(null, "Hardware", null);
        category.addProducts(product, product2);
        categoryDAO.save(category);
        assertNotNull(category.getId(), "Category with valid name and products should be saved");

        category.getProducts().forEach(productEntity -> productEntity.setName("UpdatedName" + UUID.randomUUID().toString()));
        boolean updated = categoryDAO.update(category);
        assertTrue(updated, "Category with valid name and products should be updated");

        ////
        Optional<CategoryEntity> categoryUpdate = categoryDAO.getByIdEager(category.getId());
        assertTrue(categoryUpdate.isPresent(), "Category valid id must be present");
        assertTrue(categoryUpdate.get().getProducts().size() == 2, "We added and updated 2 products, Category should have 2 products");

        assertTrue(categoryUpdate.get().getProducts().getFirst().getName().contains("UpdatedName"),
                "First Product name should be updated");
        assertTrue(categoryUpdate.get().getProducts().getLast().getName().contains("UpdatedName"),
                "Last Product name should be updated");
    }

    //=============================== categoryDAO.delete(CategoryEntity category) ===============================\\
    @Test
    @DisplayName("Delete by null Id")
    void deleteByNullId() {
        Long idDel = null;
        Boolean deleted = categoryDAO.deleteById(idDel);
        assertFalse(deleted, "Category with null Id shouldn't be deleted");
    }

    @Test
    @DisplayName("Delete by Long.MAX_VALUE Id")
    void DeleteByMaxValue() {
        Long id = Long.MAX_VALUE;
        boolean deleted = categoryDAO.deleteById(id);
        assertFalse(deleted, "Shouldn't be category with Id " + id + " for delete");
    }

    @Test
    @DisplayName("Delete by Long.MIN_VALUE Id")
    void DeleteByMinValue() {
        Long id = Long.MIN_VALUE;
        boolean deleted = categoryDAO.deleteById(id);
        assertFalse(deleted, "Shouldn't be category with Id " + id + " for delete");
    }

    @Test
    @DisplayName("Delete by valid Id")
    void DeleteByValidId() {
        Long id = categoryId;
        boolean deleted = categoryDAO.deleteById(id);
        assertTrue(deleted, "Category with Id " + id + " should be deleted");

        setUpTestData();
    }

    //=============================== categoryDAO.getByIdEager(Long id) ===============================\\
    @Test
    @DisplayName("Get by valid Id Eager")
    void getByIdEagerValidId() {
        Long id = categoryId;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isPresent(), "Category with Id " + id + " should be present");
        assertNotNull(category.get().getProducts(), "Products of Category with Id " + id + " shouldn't be null");
    }

    @Test
    @DisplayName("Get by null Id Eager")
    void getByIdEagerNullId() {
        Long id = null;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("Get by Long.MAX_VALUE Id Eager")
    void getByIdEagerMaxValueId() {
        Long id = Long.MAX_VALUE;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("Get by Long.MIN_VALUE Id Eager")
    void getByIdEagerMinValueId() {
        Long id = Long.MIN_VALUE;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("BIDIRECTIONAL association, Get Category from a product(element of List) into Category")
    void getCategoryFromProduct() {
        Long id = categoryId;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isPresent(), "Category with Id " + id + " should be present");
        assertNotNull(category.get().getProducts(), "Products of Category with Id " + id + " shouldn't be null");
        assertNotNull(category.get().getProducts().getFirst().getCategory(), "Category of Product with Id " + id + " shouldn't be null");
    }


}
