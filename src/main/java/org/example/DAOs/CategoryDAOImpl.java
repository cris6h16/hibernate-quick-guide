package org.example.DAOs;

import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.example.Entities.CategoryEntity;
import org.example.Entities.ProductEntity;
import org.example.Exceptions.ExceptionHandler;
import org.example.Util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

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
    private final SessionFactory sessionFactory;
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";

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
            Query<CategoryEntity> query = session.createQuery
                    ("from CategoryEntity", CategoryEntity.class);
            categories = query.list();

        } catch (HibernateException he) {
            handleException(he, "listAll", ExceptionHandler.SEVERE, "n/a");
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

        List<ProductEntity> products = new ArrayList<>();
        CategoryEntity category = null;

        try (Session session = sessionFactory.openSession()) {
            //retrieve the category
            category = session.find(CategoryEntity.class, id);
            //retrieve products
            Query<ProductEntity> query = session.createQuery
                    ("from ProductEntity p WHERE p.category.id = :id", ProductEntity.class);
            query.setParameter(ID_FIELD, id);
            products = query.list();

//            Never happens, because method already accepts Longs and verify if isn't null
//        } catch (IllegalArgumentException ie) {
//            handleException(ie, "getByIdEager", ExceptionHandler.WARNING, String.valueOf(id));
        } catch (HibernateException e) {
            handleException(e, "getByIdEager", ExceptionHandler.SEVERE, String.valueOf(id));
        }

        if (category == null) return Optional.empty();

        category.setProducts(products);

        return Optional.of(category);
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

        CategoryEntity category = null;

        try (Session session = sessionFactory.openSession()) {
            category = session.get(CategoryEntity.class, id);
        } catch (HibernateException e) {
            handleException(e, "findById", ExceptionHandler.SEVERE, String.valueOf(id));
        }

        return Optional.ofNullable(category);
    }

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given name exists
     */
    public Optional<CategoryEntity> findByName(String name) {

        if (name == null) return Optional.empty();
        CategoryEntity category = null;

        try (Session session = sessionFactory.openSession()) {
            Query<CategoryEntity> query = session.createQuery
                    ("FROM CategoryEntity ce WHERE ce.name = :name", CategoryEntity.class);
            query.setParameter(NAME_FIELD, name);
            //Only one result, CategoryEntity.name is UNIQUE
            category = query.getSingleResultOrNull();

//        Never happens, because CategoryEntity.name is UNIQUE
//        } catch (NonUniqueResultException ne) {
//            handleException(ne, "findByName", ExceptionHandler.SEVERE, name);
        } catch (HibernateException he) {
            handleException(he, "findByName", ExceptionHandler.SEVERE, name);
        }

        return Optional.ofNullable(category);
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
        List<CategoryEntity> categories = null;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // It deletes rows only if we commit the transaction
            session.createQuery("delete from CategoryEntity").executeUpdate();
            Query<CategoryEntity> query = session.createQuery("from CategoryEntity", CategoryEntity.class);
            categories = query.list();

            session.getTransaction().rollback();

            //HibernateException extends PersistenceException
        } catch (IllegalStateException | PersistenceException e) {
            handleException(e, "listAllWithEmptyRows", ExceptionHandler.SEVERE, "n/a");
        }

        return categories;
    }

    /**
     * Saves a CategoryEntity object to the database. if was saved successfully
     * the category will have id assigned
     *
     * @param category the CategoryEntity object to be saved
     */
    @Override
    public void save(CategoryEntity category) {
        if (category == null) return;
        if (category.getName() == null) return;
        if (category.getName().isEmpty()) return;


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.persist(category);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
            //RollbackException, HibernateException extends PersistenceException
        } catch (IllegalStateException | PersistenceException e) {
            handleException(e, "save", ExceptionHandler.SEVERE, category.toString());
        }
    }

    /**
     * Use for update {@link Session#refresh(Object)}
     *
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */

    @Override
    public boolean update(CategoryEntity category) {

        if (category == null) return false;
        if (category.getId() == null) return false;
        if (category.getName() == null) return false;
        if (category.getName().isEmpty()) return false;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.refresh(category);
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
                criteriaDelete = criteriaDelete.where(builder.equal(root.get(ID_FIELD), id));

                MutationQuery query = session.createMutationQuery(criteriaDelete);
                affectedRows = query.executeUpdate();
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
