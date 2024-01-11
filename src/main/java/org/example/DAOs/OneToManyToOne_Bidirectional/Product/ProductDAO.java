package org.example.DAOs.OneToManyToOne_Bidirectional.Product;
import org.example.Entities.OneToManyToOne_Bidirectional.ProductEntity;

public interface ProductDAO {
    void save(ProductEntity product);
    void merge(ProductEntity product);
}
