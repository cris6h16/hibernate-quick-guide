package org.example.DAOs.OneToOne_Unidirectional;

import org.example.Entities.OneToOne_Unidirectional.UserEntity;

import java.util.Optional;

public interface UserDAO {
    Optional<UserEntity> getUserById(Long id);
    Optional<UserEntity> getUserByIdEager(Long id);
    boolean removeById(Long id);
    void persist(UserEntity userEntity);
    void merge(UserEntity userEntity);
    void refresh(UserEntity userEntity);
    void detach(UserEntity userEntity);

}
