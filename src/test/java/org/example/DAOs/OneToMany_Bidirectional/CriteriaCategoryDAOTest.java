package org.example.DAOs.OneToMany_Bidirectional;

import org.example.DAOs.OneToManyToOne_Bidirectional.Category.CategoryDAOCriteria;


public class CriteriaCategoryDAOTest extends CategoryDAOTest {
    public CriteriaCategoryDAOTest() {
        categoryDAO = new CategoryDAOCriteria();
    }

}
