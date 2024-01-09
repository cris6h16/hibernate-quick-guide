package org.example.DAOs.Category.Exceptions;

import org.example.Entities.CategoryEntity;

public class CategoryAlreadyExistsException extends Exception {
    public CategoryAlreadyExistsException(CategoryEntity categoryEntity) {
        super("Category already exists with name: " + categoryEntity.getName());
    }
}
