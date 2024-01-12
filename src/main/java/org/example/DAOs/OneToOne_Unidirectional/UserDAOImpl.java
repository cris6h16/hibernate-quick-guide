package org.example.DAOs.OneToOne_Unidirectional;

import org.example.Entities.OneToOne_Unidirectional.UserEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
    private static final Logger LOG = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        if (id == null) {
            LOG.warning("Id is null");
            return Optional.empty();
        }

        Optional<UserEntity> userEntityOptional = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            userEntityOptional = Optional.ofNullable(session.find(UserEntity.class, id));
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }
        return userEntityOptional;
    }

    @Override
    public Optional<UserEntity> getUserByIdEager(Long id) {
        if (id == null) {
            LOG.warning("Id is null");
            return Optional.empty();
        }

        Optional<UserEntity> userEntityOptional = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            userEntityOptional = session
                    .createQuery("FROM UserEntity u JOIN FETCH u.address WHERE u.id = :id", UserEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }

        return userEntityOptional;
    }

    @Override
    public void persist(UserEntity userEntity) {
        if (userEntity == null) {
            LOG.warning("UserEntity is null");
            return;
        }
        if (userEntity.getId() != null) {
            LOG.warning("UserEntity is not null");
            return;
        }
        if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty()) {
            LOG.warning("UserEntity username is invalid: " + userEntity.getUsername());
            return;
        }
        if (userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
            LOG.warning("UserEntity password is invalid: " + userEntity.getPassword());
            return;
        }


        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(userEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void merge(UserEntity userEntity) {
        if (userEntity == null) {
            LOG.warning("UserEntity is null");
            return;
        }
        if (userEntity.getId() == null) {
            LOG.warning("UserEntity id is null");
            return;
        }
        if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty()) {
            LOG.warning("UserEntity username is invalid: " + userEntity.getUsername());
            return;
        }
        if (userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
            LOG.warning("UserEntity password is invalid: " + userEntity.getPassword());
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            userEntity = session.merge(userEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeById(Long id) {
        if (id == null) {
            LOG.warning("UserEntity is null");
            return false;
        }
        int affectedRows = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            affectedRows = session @Override
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

                    .createMutationQuery("DELETE FROM UserEntity ue WHERE ue.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }


        return affectedRows > 0;
    }

    @Override
    public void refresh(UserEntity userEntity) {
        if (userEntity == null) {
            LOG.warning("UserEntity is null");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.refresh(userEntity);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void detach(UserEntity userEntity) {
        if (userEntity == null) {
            LOG.warning("UserEntity is null");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.detach(userEntity);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }
    }
}
