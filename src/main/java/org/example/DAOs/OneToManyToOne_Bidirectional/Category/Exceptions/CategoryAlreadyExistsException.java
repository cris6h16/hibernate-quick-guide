package org.example.DAOs.OneToManyToOne_Bidirectional.Category.Exceptions;

import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;

public class CategoryAlreadyExistsException extends Exception {
    public CategoryAlreadyExistsException(CategoryEntity categoryEntity) {
        super("Category already exists with name: " + categoryEntity.getName());
    }
}
