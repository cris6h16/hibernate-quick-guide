package org.example.DAOs;

import org.example.DAOs.Category.CategoryDAOCriteria;
import org.example.Entities.CategoryEntity;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CriteriaCategoryDAOTest extends CategoryDAOTest {
    public CriteriaCategoryDAOTest() {
        categoryDAO = new CategoryDAOCriteria();
    }

}
