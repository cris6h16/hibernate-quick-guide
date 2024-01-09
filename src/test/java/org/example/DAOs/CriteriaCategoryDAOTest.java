package org.example.DAOs;

import org.example.DAOs.Category.CategoryDAOCriteria;


public class CriteriaCategoryDAOTest extends CategoryDAOTest {
    public CriteriaCategoryDAOTest() {
        categoryDAO = new CategoryDAOCriteria();
    }

}
