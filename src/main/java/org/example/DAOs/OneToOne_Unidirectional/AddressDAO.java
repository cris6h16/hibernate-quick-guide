package org.example.DAOs.OneToOne_Unidirectional;

import org.example.Entities.OneToOne_Unidirectional.AddressEntity;

import java.util.Optional;

public interface AddressDAO {
    Optional<AddressEntity> getAddressById(Long id);
//    Optional<AddressEntity> getAddressByIdEager(Long id);
    boolean removeById(Long id);
    void persist(AddressEntity addressEntity);
    void merge(AddressEntity addressEntity);
    void refresh(AddressEntity addressEntity);
    void detach(AddressEntity addressEntity);
}
