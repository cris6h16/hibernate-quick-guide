package org.example.DAOs;

import org.example.DAOs.Category.CategoryDAONative;
import org.example.Entities.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NativeCategoryDAOTest extends CategoryDAOTest {
    public NativeCategoryDAOTest() {
        categoryDAO = new CategoryDAONative();
    }

    @Test
    @DisplayName("Update category with valid name")
    @Override
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
}
