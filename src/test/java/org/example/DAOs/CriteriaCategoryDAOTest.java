package org.example.DAOs;

import org.example.Entities.CategoryEntity;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CriteriaCategoryDAOTest extends CategoryDAOTest {
    public CriteriaCategoryDAOTest() {
        categoryDAO = new CategoryDAOCriteria();
    }

    @Override
    @Test
    @DisplayName("Update category with valid name and Lazy Products")
    void updateValidNameLazyProducts() {
        Optional<CategoryEntity> categoryOp = categoryDAO.findById(categoryId);
        assertTrue(categoryOp.isPresent(), "Category valid id must be present");

        CategoryEntity categoryEager = categoryOp.get();
        categoryEager.setName("UpdatedName");
        assertThrows(LazyInitializationException.class,
                () -> categoryDAO.update(categoryEager),
                "Category lazy fetched with id: " + categoryId + " should throw LazyInitializationException");
    }
}
