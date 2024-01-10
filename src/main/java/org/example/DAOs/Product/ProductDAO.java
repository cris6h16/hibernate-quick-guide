package org.example.DAOs.Product;
import org.example.Entities.ProductEntity;

public interface ProductDAO {
    void save(ProductEntity product);
    void merge(ProductEntity product);
}
