package org.example.DAOs.OneToManyToOne_Bidirectional.Category;

import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;
import org.example.Util.HibernateUtil;
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

        } catch (Exception he) {
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
        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = session
                    .createQuery("FROM CategoryEntity ce LEFT JOIN FETCH ce.products WHERE ce.id = :id", CategoryEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();

        } catch (IllegalArgumentException ie) {
            logger.warning("Invalid id: " + id);
        } catch (Exception e) {
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
        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = Optional.ofNullable(session.get(CategoryEntity.class, id));

        } catch (IllegalArgumentException ie) {
            logger.warning("Invalid id: " + id);
        } catch (Exception e) {
            logger.severe("Error in findById: " + e.getMessage());
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
        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            category = session
                    .createQuery("FROM CategoryEntity ce WHERE ce.name = :name", CategoryEntity.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();

        } catch (IllegalArgumentException ie) {
            logger.warning("Invalid name: " + name);
        } catch (Exception e) {
            logger.severe("Error in findByName: " + e.getMessage());
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

        } catch (Exception e) {
            logger.severe("Error in listAllWithEmptyRows: " + e.getMessage());
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

        if (category == null) {
            logger.warning("Category can't be null");
            return;
        }
        if (category.getName() == null) {
            logger.warning("Category name can't be null");
            return;
        }
        if (category.getName().isEmpty()) {
            logger.warning("Category name can't be empty");
            return;
        }
        if (category.getId() != null) {
            logger.warning("Category id must be null");
            return;
        }


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.persist(category);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            logger.severe("Error in persist: " + e.getMessage());
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

        if (category == null) {
            logger.warning("Category can't be null");
            return false;
        }
        if (category.getId() == null) {
            logger.warning("Category id can't be null");
            return false;
        }
        if (category.getName() == null) {
            logger.warning("Category name can't be null");
            return false;
        }
        if (category.getName().isEmpty()) {
            logger.warning("Category name can't be empty");
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.merge(category);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            logger.severe("Error in merge: " + e.getMessage());
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
        if (id == null) {
            logger.warning("Category id can't be null");
            return false;
        }

        int affectedRows = 0;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                affectedRows = session
                        .createMutationQuery("DELETE FROM CategoryEntity c WHERE c.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            logger.severe("Error in deleteById: " + e.getMessage());

        }

        return affectedRows > 0;
    }

}
