package org.example.DAOs.OneToOne_Unidirectional;

import org.example.Entities.OneToOne_Unidirectional.AddressEntity;
import org.example.Entities.OneToOne_Unidirectional.UserEntity;
import org.example.Util.HibernateUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        HibernateUtil.getSessionFactory().openSession();
        HibernateUtil.getSessionFactory().close();
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
        assertThrows(org.hibernate.LazyInitializationException.class, () -> userEntity.getAddress().getName());
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