package org.example.DAOs.Category;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.example.Entities.CategoryEntity;
import org.example.Exceptions.ExceptionHandler;
import org.example.Util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Database Access Object of {@link CategoryEntity} using Hibernate Methods. <br>
 * DAO implementation of {@link CategoryDAO},
 *
 * @author <a href="https://github.com/cris6h16/" rel="Noopener noreferrer" target="_blank">Cristian</a>
 */

public class CategoryDAOImpl implements CategoryDAO {
    public final SessionFactory sessionFactory;
    public final Logger logger = Logger.getLogger(CategoryDAOImpl.class.getName());

    public CategoryDAOImpl() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Returns a list of all categories in the database.
     *
     * @return a list of all categories in the database
     */
    @Override
    public List<CategoryEntity> listAll() {
        List<CategoryEntity> categories = new ArrayList();

        try (Session session = sessionFactory.openSession()) {
            categories = session
                    .createQuery("from CategoryEntity", CategoryEntity.class)
                    .list();

        } catch (HibernateException he) {
            logger.severe("Error in listAll: " + he.getMessage());
        }
        return categories;
    }

    /**
     * Returns an Optional of a CategoryEntity with the given id, including its associated products.
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    @Override
    public Optional<CategoryEntity> getByIdEager(Long id) {
        if (id == null) return Optional.empty();

        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = session
                    .createQuery("FROM CategoryEntity ce LEFT JOIN FETCH ce.products WHERE ce.c_id = :id", CategoryEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();

        } catch (HibernateException e) {
            logger.severe("Error in getByIdEager: " + e.getMessage());
        }

        return category;
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    @Override
    public Optional<CategoryEntity> findById(Long id) {
        if (id == null) return Optional.empty();

        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = Optional.ofNullable(session.get(CategoryEntity.class, id));

        } catch (HibernateException e) {
            handleException(e, "findById", ExceptionHandler.SEVERE, String.valueOf(id));
        }

        return category;
    }

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given name exists
     */
    public Optional<CategoryEntity> findByName(String name) {
        if (name == null || name.isEmpty()) return Optional.empty();

        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = session
                    .createQuery("FROM CategoryEntity ce WHERE ce.c_name = :name", CategoryEntity.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();

        } catch (HibernateException he) {
            handleException(he, "findByName", ExceptionHandler.SEVERE, name);
        }

        return category;
    }

    /**
     * <b>For testing purposes.</b>
     * First, delete all rows from CategoryEntity table.
     * Second, retrieve the rows from the empty table.
     * Third, rollback the deletion.
     *
     * @return List<CategoryEntity> with all the rows from CategoryEntity table.
     */
    @Override
    public List<CategoryEntity> listAllWithEmptyRows() {
        List<CategoryEntity> categories = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            //PD: It deletes rows only if we commit the transaction
            session.createMutationQuery("delete from CategoryEntity").executeUpdate();
            categories = session.createQuery("from CategoryEntity", CategoryEntity.class).list();

            session.getTransaction().rollback();

        } catch (IllegalStateException | PersistenceException e) {
            handleException(e, "listAllWithEmptyRows", ExceptionHandler.SEVERE, "n/a");
        }

        return categories;
    }

    //TODO: Improve the docs

    /**
     * Saves a CategoryEntity object to the database. if was saved successfully
     * the category will have id assigned
     *
     * @param category the CategoryEntity object to be saved
     */
    @Override
    public void persist(CategoryEntity category) {
        if (category == null) return;
        if (category.getC_name() == null) return;
        if (category.getC_name().isEmpty()) return;
        if (category.getC_id() != null) return;


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.persist(category);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (IllegalStateException | PersistenceException e) {
            handleException(e, "save", ExceptionHandler.SEVERE, category.toString());
        }
    }

    /**
     * Update the category, saving the products not persisted and updating the existing products
     *
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */

    @Override
    public boolean merge(CategoryEntity category) {

        if (category == null) return false;
        if (category.getC_id() == null) return false;
        if (category.getC_name() == null) return false;
        if (category.getC_name().isEmpty()) return false;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.merge(category);
                category.getProducts().forEach(session::merge);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
            //RollbackException, HibernateException extends PersistenceException
        } catch (IllegalStateException | PersistenceException e) {
            handleException(e, "update", ExceptionHandler.SEVERE, category.toString());
        }
        return true;
    }

    /**
     * Deletes a CategoryEntity from the database.
     *
     * @param id the id of the CategoryEntity to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteById(Long id) {
        if (id == null) return false;

        int affectedRows = 0;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaDelete<CategoryEntity> criteriaDelete = builder.createCriteriaDelete(CategoryEntity.class);
                Root<CategoryEntity> root = criteriaDelete.from(CategoryEntity.class);
                criteriaDelete = criteriaDelete.where(builder.equal(root.get("id"), id));

                affectedRows = session
                        .createMutationQuery(criteriaDelete)
                        .executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
            //HibernateException, RollbackException extends PersistenceException
        } catch (PersistenceException | IllegalStateException e) {
            handleException(e, "deleteById", ExceptionHandler.SEVERE, String.valueOf(id));
        } catch (IllegalArgumentException ie) {
            handleException(ie, "deleteById", ExceptionHandler.WARNING, String.valueOf(id));
        }

        return affectedRows > 0;
    }

    private void handleException(Exception e, String method, String type, String... params) {
        ExceptionHandler.handleException(this.getClass().getName(), e, method, type, params);

    }
}
