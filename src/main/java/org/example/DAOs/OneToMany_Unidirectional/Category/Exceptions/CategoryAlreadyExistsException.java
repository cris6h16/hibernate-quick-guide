package org.example.DAOs.OneToMany_Unidirectional.Category.Exceptions;

import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;

public class CategoryAlreadyExistsException extends Exception {
    public CategoryAlreadyExistsException(CategoryEntity categoryEntity) {
        super("Category already exists with name: " + categoryEntity.getName());
    }
}
