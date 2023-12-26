package org.example.DAOs;

import org.example.Entities.CategoryEntity;
import org.example.Entities.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class CategoryDAOTest {
    private static CategoryDAO categoryDAO;

    // Attributes for testing
    private static Long categoryId;
    private static String categoryName;

    @BeforeAll
    static void beforeAllSetDAO() {
        categoryDAO = new CategoryDAO();
    }

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
    @DisplayName("Save category with valid name and products")
    void saveValidNameAndProductsCascadePersist() {
        ProductEntity product = new ProductEntity(null, "Hp Victus 15", "Laptop gamer, Model: fb-0028nr", null, null);
        ProductEntity product2 = new ProductEntity(null, "Generic Mechanical keyboard", "Laptop gamer, Model: Logitech", null, null);

        CategoryEntity category = new CategoryEntity(null, "Laptops", null);
        category.setProducts(List.of(product, product2));
        categoryDAO.save(category);

        assertNotNull(category.getId(), "Category with valid name and products should be saved");
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
        CategoryEntity category = categoryDAO.findByName(categoryName).get();
        category.setName("UpdatedName");
        boolean updated = categoryDAO.update(category);
        assertTrue(updated, "Category with valid name should be updated");

        category.setName(categoryName);
        categoryDAO.update(category);
        boolean updated2 = categoryDAO.update(category);
        assertTrue(updated2, "Category with valid name should be updated Again");
    }

    @Test
    @DisplayName("Update category with valid name and products")
    void updateValidNameAndProductsCascadePersist() {
        ProductEntity product = new ProductEntity(null, "SSD 2tb", "ssd description", null, null);
        ProductEntity product2 = new ProductEntity(null, "Motherboard", "motherboard description", null, null);

        CategoryEntity category = new CategoryEntity(null, "Hardware", null);
        category.setProducts(List.of(product, product2));
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
}
