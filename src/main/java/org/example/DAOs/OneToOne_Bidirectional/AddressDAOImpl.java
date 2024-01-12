package org.example.DAOs.OneToOne_Bidirectional;

import org.example.Entities.OneToOne_Bidirectional.AddressEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;
import java.util.logging.Logger;

public class AddressDAOImpl implements AddressDAO {
    private static Logger logger = Logger.getLogger(AddressDAOImpl.class.getName());

    @Override
    public Optional<AddressEntity> getAddressById(Long id) {
        if (id == null) {
            logger.warning("Address id is null");
            return Optional.empty();
        }

        Optional<AddressEntity> addressEntityOp = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            addressEntityOp = Optional.ofNullable(session.get(AddressEntity.class, id));

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return addressEntityOp;
    }

    @Override
    public Optional<AddressEntity> getAddressByIdEager(Long id) {
        if (id == null) {
            logger.warning("Address id is null");
            return Optional.empty();
        }

        Optional<AddressEntity> addressEntityOp = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            addressEntityOp = session
                    .createQuery("FROM AddressEntity a JOIN FETCH a.userEntity WHERE a.id = :id", AddressEntity.class)
                    .uniqueResultOptional();

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return addressEntityOp;
    }


    @Override
    public boolean removeById(Long id) {
        if (id == null) {
            logger.warning("Address id is null");
            return false;
        }

        int affectedRows = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                affectedRows = session
                        .createMutationQuery("DELETE FROM AddressEntity a WHERE a.id = :id")
                        .executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return affectedRows > 0;
    }


    @Override
    public void persist(AddressEntity addressEntity) {
        if (addressEntity == null) {
            logger.warning("Address is null");
            return;
        }
        if (addressEntity.getName() == null || addressEntity.getName().isEmpty()) {
            logger.warning("Address name is invalid");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {

                session.beginTransaction();
                session.persist(addressEntity);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void merge(AddressEntity addressEntity) {
        if (addressEntity == null) {
            logger.warning("Address is null");
            return;
        }
        if (addressEntity.getName() == null || addressEntity.getName().isEmpty()) {
            logger.warning("Address name is invalid");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                addressEntity = session.merge(addressEntity);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(AddressEntity addressEntity) {
        if (addressEntity == null) {
            logger.warning("Address is null");
            return;
        }
        if (addressEntity.getName() == null || addressEntity.getName().isEmpty()) {
            logger.warning("Address name is invalid");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                session.refresh(addressEntity);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void detach(AddressEntity addressEntity) {
        if (addressEntity == null) {
            logger.warning("Address is null");
            return;
        }
        if (addressEntity.getName() == null || addressEntity.getName().isEmpty()) {
            logger.warning("Address name is invalid");
            return;
        }
        if (addressEntity.getId() == null) {
            logger.warning("Address id is null");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                session.detach(addressEntity);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();

        }
    }
}
