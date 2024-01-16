package org.example.DAOs.OneToMany_Bidirectional;

import org.example.DAOs.OneToManyToOne_Bidirectional.Product.ProductDAOImpl;
import org.example.Entities.DTOs.ProductDTOBasic;
import org.example.Entities.OneToManyToOne_Bidirectional.ProductEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class ProductDAOTest {
    ProductDAOImpl productDAO;

    public ProductDAOTest() {
        productDAO = new ProductDAOImpl();
    }

    @Test
    void listAllDTOBasic() {
        ProductEntity product = new ProductEntity(null,"product111", "product111 description", BigDecimal.valueOf(152.3));
        ProductEntity product2 = new ProductEntity(null,"product222", "product222 description", BigDecimal.valueOf(322.9));
        productDAO.save(product);
        productDAO.save(product2);

        List<ProductDTOBasic> list = productDAO.listAllDTOBasic();
        list.forEach(System.out::println);;
    }
}
