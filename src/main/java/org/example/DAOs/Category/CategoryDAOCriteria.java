package org.example.DAOs.Category;

import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.criteria.*;
import org.example.DAOs.Product.ProductDAO;
import org.example.Entities.CategoryEntity;
import org.example.Entities.ProductEntity;
import org.example.Exceptions.ExceptionHandler;
import org.example.Util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database Access Object of {@link CategoryEntity} using Hibernate Criteria. <br>
 * DAO implementation of {@link CategoryDAO},
 *
 * @author <a href="https://github.com/cris6h16/" rel="Noopener noreferrer" target="_blank">Cristian</a>
 */

public class CategoryDAOCriteria implements CategoryDAO {
    private final SessionFactory sessionFactory;
    private final String ID_FIELD = "c_id";
    private final String NAME_FIELD = "c_name";

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
        if (!isIdValid(id)) return false;

        int affectedRows = 0;

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaDelete<CategoryEntity> delete = criteriaBuilder.createCriteriaDelete(CategoryEntity.class);
                Root<CategoryEntity> root = delete.from(CategoryEntity.class);
                Predicate predicate = criteriaBuilder.equal(root.get(ID_FIELD), id);
                CriteriaDelete<CategoryEntity> deleteFinal = delete.where(predicate);
                MutationQuery query = session.createMutationQuery(deleteFinal);
                affectedRows = query.executeUpdate();

                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            handleSevereException(e, "deleteById", ExceptionHandler.SEVERE, id.toString());
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

        if (category == null) return false;
        if (category.getC_id() == null) return false;
        if (category.getC_name() == null) return false;
        if (category.getC_name().isEmpty()) return false;

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
                                .where(builder.equal(root.get(ID_FIELD), category.getC_id()))
                                .set(NAME_FIELD, category.getC_name()))
                        .executeUpdate();

                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            handleSevereException(e, "update", category.toString());
        }

        return affectedRowsCategory > 0;
    }


    private boolean isNameValid(String name) {
        return name != null && !name.isEmpty();
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
        if (category.getC_name() == null) return;
        if (category.getC_name().isEmpty()) return;


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
                Root<CategoryEntity> root = query.from(CategoryEntity.class);
                query = query.where(builder.equal(root.get(NAME_FIELD), category.getC_name()));
                Optional<CategoryEntity> categoryDB = session.createQuery(query).uniqueResultOptional();

                if (categoryDB.isPresent()) {
                    throw new IllegalArgumentException("Category already exists");
                }

                session.persist(category);
                session.getTransaction().commit();
            } catch (Exception e) {
                handleSevereException(e, "save", category.toString());
                session.getTransaction().rollback();
            }
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
            handleSevereException(e, "listAllWithEmptyRows", "n/a");
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
        CategoryEntity entity = null;

        try (Session session = sessionFactory.openSession()) {
            // Create the criteria builder
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> criteriaQuery = criteriaBuilder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

            // Create the 'name' = name restriction
            Predicate predicate = criteriaBuilder.equal(root.get(NAME_FIELD), name);
            criteriaQuery = criteriaQuery.where(predicate);

            // Create the query and obtain the result
            Query<CategoryEntity> query = session.createQuery(criteriaQuery);
            entity = query.getSingleResultOrNull();

        } catch (Exception e) {
            handleSevereException(e, "findByName", ExceptionHandler.SEVERE, name);
        }

        return Optional.ofNullable(entity);
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    @Override
    public Optional<CategoryEntity> findById(Long id) {
        Optional<CategoryEntity> entity = Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            // Create the criteria builder
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> criteriaQuery = builder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

            // Create the 'id' = id restriction
            Predicate predicate = builder.equal(root.get(ID_FIELD), id);
            criteriaQuery = criteriaQuery.where(predicate);

            //get the category
            entity = session
                    .createQuery(criteriaQuery)
                    .uniqueResultOptional();

        } catch (Exception e) {
            handleSevereException(e, "findById", id.toString());
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
            handleSevereException(e, "listAll", "n/a");
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
        if (!isIdValid(id)) return Optional.empty();

        List<ProductEntity> products = new ArrayList<>();
        Optional<CategoryEntity> category = Optional.empty();

        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
            Root<CategoryEntity> root = query.from(CategoryEntity.class);
            root.fetch("products", JoinType.LEFT);
            CriteriaQuery<CategoryEntity> criteriaQuery = query.where(builder.equal(root.get(ID_FIELD), id));
            category = session.createQuery(criteriaQuery).uniqueResultOptional();

        } catch (HibernateException | IllegalStateException | IllegalArgumentException |
                 NonUniqueResultException e) {
            handleSevereException(e, "getByIdEager", ExceptionHandler.SEVERE, id.toString());
        }

        return category;
    }

    private boolean isIdValid(Long id) {
        return id != null && id > 0;
    }

    private void handleSevereException(Exception e, String method, String type, String... params) {
        ExceptionHandler.handleException(this.getClass().getName(), e, method, type, params);
    }

}
