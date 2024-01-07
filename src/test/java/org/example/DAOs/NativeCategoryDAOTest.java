package org.example.DAOs;

import org.example.DAOs.Category.CategoryDAONative;

public class NativeCategoryDAOTest extends CategoryDAOTest {
    public NativeCategoryDAOTest() {
        categoryDAO = new CategoryDAONative();
    }
}
