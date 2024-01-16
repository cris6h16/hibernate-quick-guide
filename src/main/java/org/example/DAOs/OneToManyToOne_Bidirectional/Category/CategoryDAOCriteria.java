package org.example.DAOs.OneToManyToOne_Bidirectional.Category;

import jakarta.persistence.criteria.*;
import org.example.DAOs.OneToManyToOne_Bidirectional.Category.Exceptions.CategoryAlreadyExistsException;
import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Database Access Object of {@link CategoryEntity} using Hibernate Criteria. <br>
 * DAO implementation of {@link CategoryDAO},
 *
 * @author <a href="https://github.com/cris6h16/" rel="Noopener noreferrer" target="_blank">Cristian</a>
 */

public class CategoryDAOCriteria implements CategoryDAO {
    public final SessionFactory sessionFactory;
    public static final Logger LOGGER = Logger.getLogger(CategoryDAOCriteria.class.getName());

    public CategoryDAOCriteria() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Deletes a CategoryEntity from the database.
     *
     * @param id the id of the CategoryEntity to delete
     * @return true if the deletion was successful, false otherwise
     */
    @Override
    public boolean deleteById(Long id) {
        if (!isIdValid(id)) LOGGER.warning("Invalid id: " + id);

        int affectedRows = 0;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaDelete<CategoryEntity> delete = criteriaBuilder.createCriteriaDelete(CategoryEntity.class);
                Root<CategoryEntity> root = delete.from(CategoryEntity.class);
                Predicate predicate = criteriaBuilder.equal(root.get(CategoryEntity.ATTR_ID), id);
                CriteriaDelete<CategoryEntity> deleteFinal = delete.where(predicate);
                MutationQuery query = session.createMutationQuery(deleteFinal);
                affectedRows = query.executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Exception in deleteById: " + e.getMessage());
            e.printStackTrace();
        }

        return affectedRows > 0;
    }

    /**
     * //================================================================\\
     * <br>PD: Be careful with: {@link LazyInitializationException} <br>
     * <pre>
     * {@code
     * @Test
     * @DisplayName("Update category with valid name")
     * void updateValid() {
     * CategoryEntity category = categoryDAO.findByName(categoryName).get();
     * category.setName("UpdatedName");
     * boolean updated = categoryDAO.update(category);
     * }
     * ==================================================================
     * @Override public boolean update(CategoryEntity category) {
     * category.getProducts().isEmpty();   //failed to lazily initialize a collection of role
     * }
     * }
     * </pre>
     * //================================================================\\
     *
     * @param category CategoryEntity must be eagerly fetched. we'll do .isEmpty(), here it fails: <br><br>
     */
    @Override
    public boolean merge(CategoryEntity category) {

        if (category == null) {
            LOGGER.warning("Category is null");
            return false;
        }
        if (category.getId() == null) {
            LOGGER.warning("Category id is null");
            return false;
        }
        if (category.getName() == null) {
            LOGGER.warning("Category name is null");
            return false;
        }
        if (category.getName().isEmpty()) {
            LOGGER.warning("Category name is empty");
            return false;
        }

        int affectedRowsCategory = 0;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                //Obtain the criteria builder
                CriteriaBuilder builder = session.getCriteriaBuilder();
                //Update the category
                CriteriaUpdate<CategoryEntity> update = builder.createCriteriaUpdate(CategoryEntity.class);
                Root<CategoryEntity> root = update.from(CategoryEntity.class);
                affectedRowsCategory = session
                        .createMutationQuery(update
                                .where(builder.equal(root.get(CategoryEntity.ATTR_ID), category.getId()))
                                .set(CategoryEntity.ATTR_NAME, category.getName()))
                        .executeUpdate();

                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Exception in merge: " + e.getMessage());
            e.printStackTrace();
        }

        return affectedRowsCategory > 0;
    }

    /**
     * Saves a CategoryEntity object to the database. if was saved successfully
     * the category will have id assigned
     *
     * @param category the CategoryEntity object to be saved
     */
    @Override
    public void persist(CategoryEntity category) {
        if (category == null) {
            LOGGER.warning("Category is null");
            return;
        }
        if (category.getName() == null) {
            LOGGER.warning("Category name is null");
            return;
        }
        if (category.getName().isEmpty()) {
            LOGGER.warning("Category name is empty");
            return;
        }
        if (category.getId() != null) {
            LOGGER.warning("Category ID must be null");
            return;
        }


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
                Root<CategoryEntity> root = query.from(CategoryEntity.class);
                query = query.where(builder.equal(root.get(CategoryEntity.ATTR_NAME), category.getName()));
                Optional<CategoryEntity> categoryDB = session.createQuery(query).uniqueResultOptional();

                if (categoryDB.isPresent()) {
                    throw new CategoryAlreadyExistsException(category);
                }
                session.persist(category);

                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (CategoryAlreadyExistsException ce) {
            LOGGER.warning(ce.getMessage());
        } catch (Exception e) {
            LOGGER.severe("Exception in persist: " + e.getMessage());
            e.printStackTrace();
        }
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
            try {
                //Begin the transaction
                session.beginTransaction();

                //Create the criteria builder
                CriteriaBuilder builder = session.getCriteriaBuilder();

                //Delete all rows
                CriteriaDelete<CategoryEntity> delete = builder.createCriteriaDelete(CategoryEntity.class);
                MutationQuery query = session.createMutationQuery(delete);
                query.executeUpdate();

                //Get all rows
                CriteriaQuery<CategoryEntity> criteriaQuery = builder.createQuery(CategoryEntity.class);
                Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);
                criteriaQuery = criteriaQuery.select(root);
                Query<CategoryEntity> queryList = session.createQuery(criteriaQuery);
                categories = queryList.list();

                //Rollback the transaction
                session.getTransaction().rollback();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Exception in listAllWithEmptyRows: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given name exists
     */
    @Override
    public Optional<CategoryEntity> findByName(String name) {
        if (name == null) {
            LOGGER.warning("Category name is null");
            return Optional.empty();
        }
        if (name.isEmpty()) {
            LOGGER.warning("Category name is empty");
            return Optional.empty();
        }

        Optional<CategoryEntity> entity = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            // Create the criteria builder
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> criteriaQuery = criteriaBuilder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

            // Create the 'name' = name restriction
            Predicate predicate = criteriaBuilder.equal(root.get(CategoryEntity.ATTR_NAME), name);
            criteriaQuery = criteriaQuery.where(predicate);

            // Create the query and obtain the result
            Query<CategoryEntity> query = session.createQuery(criteriaQuery);
            entity = query.uniqueResultOptional();

        } catch (Exception e) {
            LOGGER.severe("Exception in findByName: " + e.getMessage());
            e.printStackTrace();
        }

        return entity;
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    @Override
    public Optional<CategoryEntity> findById(Long id) {
        if (!isIdValid(id)) LOGGER.warning("Invalid id: " + id);

        Optional<CategoryEntity> entity = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            // Create the criteria builder
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> criteriaQuery = builder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

            // Create the 'id' = id restriction
            Predicate predicate = builder.equal(root.get(CategoryEntity.ATTR_ID), id);
            criteriaQuery = criteriaQuery.where(predicate);

            //get the category
            entity = session
                    .createQuery(criteriaQuery)
                    .uniqueResultOptional();

        } catch (Exception e) {
            LOGGER.severe("Exception in findById: " + e.getMessage());
            e.printStackTrace();
        }

        return entity;
    }
    /**
     * Returns a list of all categories in the database.
     *
     * @return a list of all categories in the database
     */
    @Override
    public List<CategoryEntity> listAll() {
        List<CategoryEntity> categories = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            // Create the criteria builder
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
            query.from(CategoryEntity.class);

            // get result
            Query<CategoryEntity> query2 = session.createQuery(query);
            categories = query2.list();

        } catch (Exception e) {
            LOGGER.severe("Exception in listAll: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }
    /**
     * Returns an Optional of a CategoryEntity with the given id, including its associated products(Collection Initialized).
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    @Override
    public Optional<CategoryEntity> getByIdEager(Long id) {
        if (!isIdValid(id)) {
            LOGGER.warning("Invalid id: " + id);
            return Optional.empty();
        }

        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = query.from(CategoryEntity.class);
            root.fetch(CategoryEntity.ATTR_PRODUCTS, JoinType.LEFT);
            CriteriaQuery<CategoryEntity> criteriaQuery = query.where(builder.equal(root.get(CategoryEntity.ATTR_ID), id));
            category = session.createQuery(criteriaQuery).uniqueResultOptional();

        } catch (Exception e) {
            LOGGER.severe("Exception in getByIdEager: " + e.getMessage());
            e.printStackTrace();
        }

        return category;
    }

    private boolean isIdValid(Long id) {
        return id != null && id > 0;
    }

}
