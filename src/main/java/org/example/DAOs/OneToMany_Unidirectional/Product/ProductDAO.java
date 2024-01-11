package org.example.DAOs.OneToMany_Unidirectional.Product;
import org.example.Entities.OneToManyToOne_Unidirectional.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    void persist(ProductEntity product);
    void merge(ProductEntity product);
    boolean deleteById(Long product);
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> listAll();
    Optional<ProductEntity> getByIdEager(Long id);
}
