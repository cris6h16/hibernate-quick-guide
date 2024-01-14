package org.example.DAOs.OneToOne_Others;

import org.example.Entities.OneToOne_Others.AddressEntity;
import org.example.Entities.OneToOne_Others.UserEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestOthers {

    @Test
    void test() {
        // create tables
        HibernateUtil.getSessionFactory().openSession();
        HibernateUtil.getSessionFactory().close();
        System.out.println();
    }

    @Test
    void primary_key_columns() {
        UserEntity user1 = new UserEntity(null, "user1", "1234", null);
        UserEntity user2 = new UserEntity(null, "user2", "1234", null);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(user1);
        session.persist(user2);
        session.getTransaction().commit();
        session.close();

        UserEntity user3 = new UserEntity(null, "user3", "1234", null);
        AddressEntity address1 = new AddressEntity(null, "address1", "5120-W0", "State");
        user3.setAddress(address1);

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        session2.beginTransaction();
        session2.persist(user3); //cascade.all
        session2.getTransaction().commit();
        session2.close();

        Session session3 = HibernateUtil.getSessionFactory().openSession();
        UserEntity userDB = session3.get(UserEntity.class, user3.getId());
        assertEquals(userDB.getAddress().getId(), userDB.getId());

    }

}
