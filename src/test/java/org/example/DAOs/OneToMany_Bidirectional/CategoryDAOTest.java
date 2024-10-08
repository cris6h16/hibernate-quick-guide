package org.example.DAOs.OneToMany_Bidirectional;

import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import org.example.DAOs.OneToManyToOne_Bidirectional.Category.CategoryDAO;
import org.example.DAOs.OneToManyToOne_Bidirectional.Category.CategoryDAOImpl;
import org.example.DAOs.OneToManyToOne_Bidirectional.Product.ProductDAOImpl;
import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;
import org.example.Entities.OneToManyToOne_Bidirectional.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class CategoryDAOTest {
    protected static CategoryDAO categoryDAO;
    protected static ProductDAOImpl productDAO;

    // Attributes for testing
    protected static java.lang.Long categoryId;
    protected static String categoryName;

    public CategoryDAOTest() {
        categoryDAO = new CategoryDAOImpl();
        productDAO = new ProductDAOImpl();
    }

    @BeforeAll
    public static void setUpTestData() {
        CategoryEntity categoryForTesting = new CategoryEntity(null, "CategoryForTesting");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            try {

                String deleteSQL = String.format("DELETE FROM CategoryEntity c WHERE c.%s = :name",
                        CategoryEntity.ATTR_NAME);
                // Delete CategoryForTesting if it exists
                session.beginTransaction();
                session.createMutationQuery(deleteSQL)
                        .setParameter("name", categoryForTesting.getName())
                        .executeUpdate();

                // Persist CategoryForTesting
                session.persist(categoryForTesting);
                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Set values in test attributes
        categoryId = categoryForTesting.getId();
        categoryName = categoryForTesting.getName();
    }

    //=============================== categoryDAO.findById(Long id) ===============================\\
    @Test
    @DisplayName("findById by valid Id NO EAGER FETCHING")
    void getByIdLazyValidId() {
        java.lang.Long id = categoryId;
        Optional<CategoryEntity> category = categoryDAO.findById(id);
        assertTrue(category.isPresent(), "Category with Id " + id + " should be present");
        assertThrows(LazyInitializationException.class, () -> category.get().getProducts().isEmpty(), "Products of Category with Id " + id + " shouldn't be null");
    }

    @Test
    @DisplayName("Find category by valid ID")
    void findByIdValid() {
        // categoryId, categoryName are set in setUpTestData()
        java.lang.Long validId = categoryId;
        Optional<CategoryEntity> category = categoryDAO.findById(validId);
        assertTrue(category.isPresent(), "Category with ID " + validId + " should be present");
        assertEquals(categoryName, category.get().getName(), "Category with ID " + validId + " should be " + categoryName);
    }

    @Test
    @DisplayName("Find category by null ID")
    void findByIdNull() {
        java.lang.Long nullId = null;
        Optional<CategoryEntity> category = categoryDAO.findById(nullId);
        assertTrue(category.isEmpty(), "Category with ID " + nullId + " should be empty");
    }

    @Test
    @DisplayName("Find category by Long.MAX_VALUE ID")
    void findByIdMaxValue() {
        java.lang.Long maxValueId = java.lang.Long.MAX_VALUE;
        Optional<CategoryEntity> category = categoryDAO.findById(maxValueId);
        assertFalse(category.isPresent(), "Category with ID " + maxValueId + " shouldn't be present");
    }

    @Test
    @DisplayName("Find category by Long.MIN_VALUE ID")
    void findByIdMinValue() {
        java.lang.Long minValueId = java.lang.Long.MIN_VALUE;
        Optional<CategoryEntity> category = categoryDAO.findById(minValueId);
        assertFalse(category.isPresent(), "Category with ID " + minValueId + " shouldn't be present");
    }


    //=============================== categoryDAO.listAll() ===============================\\
    @Test
    @DisplayName("List all categories when multiple categories exist")
    @com.anarsoft.vmlens.concurrent.junit.ThreadCount(3)
    void listAllMultipleCategories() {
        List<CategoryEntity> categories = categoryDAO.listAll();
        assertFalse(categories.isEmpty(), "List of categories should not be empty");

    }

    @Test
    @DisplayName("List all PAGINATION")
    void listAllPagination() {
        int resPerPage = 3;

        int pages = categoryDAO.countPages(resPerPage);
        for (int page = 1; page <= pages; page++) {
            System.out.println("\nPage: " + page + " of " + pages);
            List<CategoryEntity> categories = categoryDAO.listPagination(page, resPerPage);
            categories.forEach(category -> System.out.println(category));
            assertFalse(categories.isEmpty(), "List of categories should not be empty");
        }

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
    @ThreadCount(4)
    @DisplayName("Save category with null name")
    void saveNullName() {
        CategoryEntity category = new CategoryEntity(null, null);
        categoryDAO.persist(category);
        assertNull(category.getId(), "Category with null name shouldn't be saved");
    }

    @Test
    @DisplayName("Save category with empty name")
    void saveEmptyName() {
        CategoryEntity category = new CategoryEntity(null, "");
        categoryDAO.persist(category);
        assertNull(category.getId(), "Category with empty name shouldn't be saved");
    }

    @Test
    @ThreadCount(4)
    @DisplayName("Save category with valid name")
    void saveValidName() {
        CategoryEntity category = new CategoryEntity(null, "Food");
        categoryDAO.persist(category);
        assertNotNull(category.getId(), "Category with valid name should be saved");
    }

    @Test
    @DisplayName("Save category with id and valid name")
    void saveIdAndValidName() {
        CategoryEntity category = new CategoryEntity(9999999L, "FoodTest");
        categoryDAO.persist(category);

        Optional<CategoryEntity> categoryOp = categoryDAO.findById(9999999L);
        assertTrue(categoryOp.isEmpty(), "Category tried save with id mustn't be saved");
    }

    //    @Test
//    @DisplayName("Save category with valid name and products")
//    void saveValidNameAndProductsCascadePersist() {
//
//        ProductEntity product = new ProductEntity(null, "Hp Victus 15", "Laptop gamer, Model: fb-0028nr", null);
//        ProductEntity product2 = new ProductEntity(null, "Generic Mechanical keyboard", "Laptop gamer, Model: Logitech", null);
//        productDAO.save(product);
//        productDAO.save(product2);
//
//        CategoryEntity category = new CategoryEntity(null, "Laptops");
//        categoryDAO.save(category);
//
//        category.addProducts(product2, product);
//        categoryDAO.merge(category);
//        productDAO.merge(product);
//
//        Optional<CategoryEntity> categoryOp = categoryDAO.getByIdEager(category.getC_id());
//        assertTrue(categoryOp.isPresent(), "Category with valid name and products should be saved");
//
//        category = categoryOp.get();
//        assertNotNull(category.getC_id(), "Category with valid name and products should be saved");
//        assertEquals(2, category.getProducts().size(), "Category should have 2 products");
//        assertNotNull(category.getProducts().get(0).getId(), "Product with valid name and products should be saved");
//    }
    @Test
    @DisplayName("Save category with valid name and products")
    void saveValidNameAndProducts() {

        ProductEntity product = new ProductEntity(null, "Hp Victus 15", "Laptop gamer, Model: fb-0028nr", null);
        ProductEntity product2 = new ProductEntity(null, "Generic Mechanical keyboard", "Laptop gamer, Model: Logitech", null);
        productDAO.save(product);
        productDAO.save(product2);

        CategoryEntity category = new CategoryEntity(null, "Laptops");
        categoryDAO.persist(category);

        product.setCategory(category);
        product2.setCategory(category);
        productDAO.merge(product);
        productDAO.merge(product2);

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
        CategoryEntity category = new CategoryEntity(categoryId, null);
        boolean updated = categoryDAO.merge(category);
        assertFalse(updated, "Category with null name shouldn't be updated");
    }

    @Test
    @DisplayName("Update category with empty name")
    void updateEmptyName() {
        CategoryEntity category = new CategoryEntity(categoryId, "");
        boolean updated = categoryDAO.merge(category);
        assertFalse(updated, "Category with empty name shouldn't be updated");
    }

    @Test
    @DisplayName("Update category with null ID")
    void updateNullId() {
        CategoryEntity category = new CategoryEntity(null, "UpdatedName");
        boolean updated = categoryDAO.merge(category);
        assertFalse(updated, "Category with null ID shouldn't be updated");
    }

    @Test
    @ThreadCount(10)
    @DisplayName("Update category with valid name")
    void updateValid() {
        Optional<CategoryEntity> categoryOp = categoryDAO.getByIdEager(categoryId);
        assertTrue(categoryOp.isPresent(), "Category valid id must be present");

        CategoryEntity categoryEager = categoryOp.get();
        categoryEager.setName("UpdatedName");
        boolean updated = categoryDAO.merge(categoryEager);
        assertTrue(updated, "Category with valid name should be updated");

        categoryEager.setName(categoryName);
        categoryDAO.merge(categoryEager);
        boolean updated2 = categoryDAO.merge(categoryEager);
        assertTrue(updated2, "Category with valid name should be updated Again");
    }

    @Test
    @DisplayName("Update category with valid name and Lazy Products")
    void updateValidNameLazyProducts() {
        Optional<CategoryEntity> categoryOp = categoryDAO.findById(categoryId);
        assertTrue(categoryOp.isPresent(), "Category valid id must be present");

        CategoryEntity category = categoryOp.get();
        category.setName("UpdatedName");
        boolean updated = categoryDAO.merge(category);
        assertTrue(updated, "Category with valid name should be updated");

        category.setName(categoryName);
        categoryDAO.merge(category);
        boolean updated2 = categoryDAO.merge(category);
        assertTrue(updated2, "Category with valid name should be updated Again");
    }


    @Test
    @DisplayName("Update category with valid name and products")
    void updateValidNameAndProducts() {
        ProductEntity product = new ProductEntity(null, "SSD 2tb", "ssd description", null);
        ProductEntity product2 = new ProductEntity(null, "Motherboard", "motherboard description", null);
        productDAO.save(product);
        productDAO.save(product2);

        CategoryEntity category = new CategoryEntity(null, "Hardware");
        categoryDAO.persist(category);
        assertNotNull(category.getId(), "Category with valid name and products should be saved");

        product.setCategory(category);
        product2.setCategory(category);
        productDAO.merge(product);
        productDAO.merge(product2);

        // the name isn't updated
        category.setName("UpdatedName");
        boolean updated = categoryDAO.merge(category);
        assertTrue(updated, "Category with valid name and products should be updated");

        category.setName("Hardware");
        categoryDAO.merge(category);
        boolean updated2 = categoryDAO.merge(category);
        assertTrue(updated2, "Category with valid name and products should be updated Again");
    }

    @Test
    @DisplayName("Update products category")
    void updateProductCategory() {
        //create products and save
        ProductEntity product = new ProductEntity(null, "updateProductCategory 1", "updateProductCategory description 1", null);
        ProductEntity product2 = new ProductEntity(null, "updateProductCategory 2", "updateProductCategory description 2", null);
        productDAO.save(product);
        productDAO.save(product2);

        //create category and save
        CategoryEntity category = new CategoryEntity(null, "updateProductCategory Category 1");
        categoryDAO.persist(category);
        assertNotNull(category.getId(), "Category with valid name should be saved");

        //add category to products and save
        product.setCategory(category);
        product2.setCategory(category);
        productDAO.merge(product);
        productDAO.merge(product2);
/* public void setCategory(CategoryEntity category) {
        this.category = category;
        category.getProducts().add(this);
    }
*/
        //change category name and merge
        category.getProducts().forEach(productEntity -> productEntity.setName("UpdatedName" + UUID.randomUUID().toString()));
        category.getProducts().forEach(productEntity -> productDAO.merge(productEntity));

        Optional<CategoryEntity> categoryUpdate = categoryDAO.getByIdEager(category.getId());
        assertTrue(categoryUpdate.isPresent(), "Category valid id must be present");
        assertTrue(categoryUpdate.get().getProducts().size() == 2, "We added and updated 2 products, Category should have 2 products");

        System.out.println(System.getProperties().replace(",", "\n"));
        assertTrue(categoryUpdate.get().getProducts().get(0).getName().contains("UpdatedName"),
                "First Product name should be updated");
        assertTrue(categoryUpdate.get().getProducts().get(1).getName().contains("UpdatedName"),
                "Last Product name should be updated");

    }

    //=============================== categoryDAO.delete(CategoryEntity category) ===============================\\
    @Test
    @DisplayName("Delete by null Id")
    void deleteByNullId() {
        java.lang.Long idDel = null;
        Boolean deleted = categoryDAO.deleteById(idDel);
        assertFalse(deleted, "Category with null Id shouldn't be deleted");
    }

    @Test
    @DisplayName("Delete by Long.MAX_VALUE Id")
    void DeleteByMaxValue() {
        java.lang.Long id = java.lang.Long.MAX_VALUE;
        boolean deleted = categoryDAO.deleteById(id);
        assertFalse(deleted, "Shouldn't be category with Id " + id + " for delete");
    }

    @Test
    @DisplayName("Delete by Long.MIN_VALUE Id")
    void DeleteByMinValue() {
        java.lang.Long id = java.lang.Long.MIN_VALUE;
        boolean deleted = categoryDAO.deleteById(id);
        assertFalse(deleted, "Shouldn't be category with Id " + id + " for delete");
    }

    @Test
    @DisplayName("Delete by valid Id")
    void DeleteByValidId() {
        java.lang.Long id = categoryId;
        boolean deleted = categoryDAO.deleteById(id);
        assertTrue(deleted, "Category with Id " + id + " should be deleted");

        setUpTestData();
    }

    //=============================== categoryDAO.getByIdEager(Long id) ===============================\\
    @Test
    @DisplayName("Get by valid Id Eager")
    void getByIdEagerValidId() {
        java.lang.Long id = categoryId;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isPresent(), "Category with Id " + id + " should be present");
        assertNotNull(category.get().getProducts(), "Products of Category with Id " + id + " shouldn't be null");
    }

    @Test
    @DisplayName("Get by null Id Eager")
    void getByIdEagerNullId() {
        java.lang.Long id = null;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("Get by Long.MAX_VALUE Id Eager")
    void getByIdEagerMaxValueId() {
        java.lang.Long id = java.lang.Long.MAX_VALUE;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("Get by Long.MIN_VALUE Id Eager")
    void getByIdEagerMinValueId() {
        java.lang.Long id = java.lang.Long.MIN_VALUE;
        Optional<CategoryEntity> category = categoryDAO.getByIdEager(id);
        assertTrue(category.isEmpty(), "Category with Id " + id + " shouldn't be present");
    }

    @Test
    @DisplayName("BIDIRECTIONAL association, Get Category from a product(element of List) into Category")
    void getCategoryFromProduct() {

        ProductEntity product = new ProductEntity(null, "Metal filing cabinet", "size 150x125", BigDecimal.valueOf(12));
        ProductEntity product2 = new ProductEntity(null, "Folder", "Blue folder with transparent cover", BigDecimal.valueOf(12.847));
        productDAO.save(product);
        productDAO.save(product2);

        CategoryEntity category = new CategoryEntity(null, "Office");
        categoryDAO.persist(category);

        product.setCategory(category);
        product2.setCategory(category);
        categoryDAO.merge(category);
        productDAO.merge(product);

        java.lang.Long id = category.getId();
        assertNotNull(id, "Category with valid name and products should be saved");

        Optional<CategoryEntity> categoryOp = categoryDAO.getByIdEager(id);
        assertTrue(categoryOp.isPresent(), "Category with Id " + id + " should be present");
        assertNotNull(categoryOp.get().getProducts(), "Products of Category with Id " + id + " shouldn't be null");
        assertNotNull(categoryOp.get().getProducts().get(0).getCategory(), "Category of Product with Id " + id + " shouldn't be null");


    }


}
