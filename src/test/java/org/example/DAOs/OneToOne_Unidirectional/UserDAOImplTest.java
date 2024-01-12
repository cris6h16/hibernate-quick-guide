package org.example.DAOs.OneToOne_Unidirectional;

import org.example.DAOs.OneToOne_Unidirectional.AddressDAOImpl;
import org.example.DAOs.OneToOne_Unidirectional.UserDAOImpl;
import org.example.Entities.OneToOne_Unidirectional.AddressEntity;
import org.example.Entities.OneToOne_Unidirectional.UserEntity;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class  {
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

        addressEntityTest = new AddressEntity(null, "casa", "5120-W0", "State");
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
        UserEntity userEntity = userDAO.getUserById(userEntityTest.getId()).orElse(null);
        assertNotNull(userEntity);
        assertEquals(userEntityTest.getId(), userEntity.getId());
        assertEquals(userEntityTest.getUsername(), userEntity.getUsername());
        assertEquals(userEntityTest.getPassword(), userEntity.getPassword());
        assertThrows(LazyInitializationException.class, () -> userEntityTest.getAddress().getId());
    }

    @Test
    void getUserByIdEager() {
        UserEntity userEntity = userDAO.getUserByIdEager(userEntityTest.getId()).orElse(null);
        assertNotNull(userEntity);
        assertEquals(userEntityTest.getId(), userEntity.getId());
        assertEquals(userEntityTest.getUsername(), userEntity.getUsername());
        assertEquals(userEntityTest.getPassword(), userEntity.getPassword());
        assertEquals(userEntityTest.getAddress().getId(), userEntity.getAddress().getId());
        assertEquals(userEntityTest.getAddress().getName(), userEntity.getAddress().getName());
        assertEquals(userEntityTest.getAddress().getZipcode(), userEntity.getAddress().getZipcode());
        assertEquals(userEntityTest.getAddress().getState(), userEntity.getAddress().getState());
    }

    @Test
    void persist() {
        UserEntity userEntity = new UserEntity(null, "cristiann010", "1234", null);
        AddressEntity addressEntity = new AddressEntity(null, "casa", "5120-W0", "State");
    }

    @Test
    void merge() {
    }

    @Test
    void removeById() {
    }

    @Test
    void refresh() {
    }

    @Test
    void detach() {
    }
}