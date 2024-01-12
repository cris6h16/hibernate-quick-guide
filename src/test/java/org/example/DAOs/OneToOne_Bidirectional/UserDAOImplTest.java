package org.example.DAOs.OneToOne_Bidirectional;

import org.example.Entities.OneToOne_Bidirectional.AddressEntity;
import org.example.Entities.OneToOne_Bidirectional.UserEntity;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    protected static UserDAO userDAO;
    protected static AddressDAO addressDAO;
    protected static UserEntity userEntityTest;
    protected static AddressEntity addressEntityTest;

    @BeforeAll
    static void beforeAll() {
        userDAO = new UserDAOImpl();
        addressDAO = new AddressDAOImpl();

        userEntityTest = new UserEntity(null, "cristiann00", "1234", null);
        userDAO.persist(userEntityTest);

        addressEntityTest = new AddressEntity(null, "casa", "5120-W0", "State", null);
        addressDAO.persist(addressEntityTest);

        /*
         public void setAddress(AddressEntity addressEntity) {
            this.address = addressEntity;
            addressEntity.setUserEntity(this);
        }
        */
        userEntityTest.setAddress(addressEntityTest);
        userDAO.merge(userEntityTest);
    }

    @Test
    void getUserById() {
        Optional<UserEntity> userEntityOptional = userDAO.getUserById(userEntityTest.getId());
        assertTrue(userEntityOptional.isPresent(), "UserEntity with valid id must be present");
    }

    @Test
    void getUserByIdEager() {
        Optional<UserEntity> userEntityOptional = userDAO.getUserByIdEager(userEntityTest.getId());
        assertTrue(userEntityOptional.isPresent(), "UserEntity with valid id must be present");

        UserEntity userEntityEager = userEntityOptional.get();
        assertDoesNotThrow(() -> userEntityEager.getAddress(), "Address shouldn't be lazy loaded");
    }

    @Test
    void persist() {
        UserEntity userEntity = new UserEntity(null, "cristian 12", "1234", null);
        userDAO.persist(userEntity);
        assertNotNull(userEntity.getId(), "UserEntity id shouldn't be null");
    }

    @Test
    void merge() {
        UserEntity userEntity = new UserEntity(null, "cristian1", "1234", null);
        userDAO.persist(userEntity);
        assertNotNull(userEntity.getId(), "UserEntity id shouldn't be null");

        userEntity.setUsername("cristian2");
        userDAO.merge(userEntity);

        Optional<UserEntity> userEntityOptional = userDAO.getUserById(userEntity.getId());
        assertTrue(userEntityOptional.isPresent(), "UserEntity with valid id must be present");

        UserEntity userEntityMerged = userEntityOptional.get();
        assertEquals(userEntity.getUsername(), userEntityMerged.getUsername(), "UserEntity username should be updated");
    }

    @Test
    void removeById() {
        UserEntity userEntity = new UserEntity(null, "cristian10", "1234", null);
        userDAO.persist(userEntity);
        assertNotNull(userEntity.getId(), "UserEntity id shouldn't be null");

        boolean removed = userDAO.removeById(userEntity.getId());
        assertTrue(removed, "UserEntity with valid id should be removed");

        Optional<UserEntity> userEntityOptional = userDAO.getUserById(userEntity.getId());
        assertFalse(userEntityOptional.isPresent(), "UserEntity with valid id shouldn't be present");
    }

    @Test
    void refresh() {
        UserEntity userEntity = new UserEntity(null, "cristian10", "1234", null);
        userDAO.persist(userEntity);
        assertNotNull(userEntity.getId(), "UserEntity id shouldn't be null");

        userEntity.setUsername("cristian11");
        userDAO.merge(userEntity);

        Optional<UserEntity> userEntityOptional = userDAO.getUserById(userEntity.getId());
        assertTrue(userEntityOptional.isPresent(), "UserEntity with valid id must be present");

        UserEntity userEntityMerged = userEntityOptional.get();
        assertEquals(userEntity.getUsername(), userEntityMerged.getUsername(), "UserEntity username should be updated");

        userEntityMerged.setUsername("altered username");
        userDAO.refresh(userEntityMerged);
        assertNotEquals("altered username", userEntity.getUsername(), " UserEntity username should be refreshed");
    }

}