package org.example.DAOs.OneToMany_Unidirectional.Category;

import org.example.Entities.OneToManyToOne_Unidirectional.CategoryEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDAOImplTest {

    CategoryDAO categoryDAO;

    CategoryDAOImplTest() {
        categoryDAO = new CategoryDAOImpl();
    }


    @Test
    void listAll() {
        List<CategoryEntity> categoryEntities = categoryDAO.listAll();
        assertNotNull(categoryEntities, "List of categories should not be null");
    }

    @Test
    void findById() {
        CategoryEntity categoryEntity = new CategoryEntity(null, "CategoryName1");
        categoryDAO.persist(categoryEntity);
        Long id = categoryEntity.getId();
        assertNotNull(id, "Category id should not be null");

        CategoryEntity categoryEntity1 = categoryDAO.findById(id).orElse(null);
        assertNotNull(categoryEntity1, "Category should not be null");
    }

    @Test
    void findByName() {
        CategoryEntity categoryEntity = new CategoryEntity(null, "CategoryName2");
        categoryDAO.persist(categoryEntity);
        String name = categoryEntity.getName();
        assertNotNull(name, "Category name should not be null");

        CategoryEntity categoryEntity1 = categoryDAO.findByName(name).orElse(null);
        assertNotNull(categoryEntity1, "Category should not be null");
    }

    @Test
    void persist() {
        CategoryEntity categoryEntity = new CategoryEntity(null, "CategoryName3");
        categoryDAO.persist(categoryEntity);
        Long id = categoryEntity.getId();
        assertNotNull(id, "Category id should not be null");
    }

    @Test
    void merge() {
        CategoryEntity categoryEntity = new CategoryEntity(null, "CategoryName4");
        categoryDAO.persist(categoryEntity);
        Long id = categoryEntity.getId();
        assertNotNull(id, "Category id should not be null");

        CategoryEntity categoryEntity1 = categoryDAO.findById(id).orElse(null);
        assertNotNull(categoryEntity1, "Category should not be null");

        categoryEntity1.setName("CategoryName5");
        categoryDAO.merge(categoryEntity1);
        assertEquals("CategoryName5", categoryEntity1.getName(), "Category name should be CategoryName5");
    }

    @Test
    void deleteById() {
        CategoryEntity categoryEntity = new CategoryEntity(null, "CategoryName6");
        categoryDAO.persist(categoryEntity);
        Long id = categoryEntity.getId();
        assertNotNull(id, "Category id should not be null");

        CategoryEntity categoryEntity1 = categoryDAO.findById(id).orElse(null);
        assertNotNull(categoryEntity1, "Category should not be null");

        boolean deleted = categoryDAO.deleteById(id);
        assertTrue(deleted, "Category should be deleted");

        CategoryEntity categoryEntity2 = categoryDAO.findById(id).orElse(null);
        assertNull(categoryEntity2, "Category should be null");
    }
}