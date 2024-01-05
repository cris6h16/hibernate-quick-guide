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
import java.util.Objects;
import java.util.Optional;

/**
 * Database Access Object of {@link CategoryEntity} using Hibernate Criteria. <br>
 * DAO implementation of {@link CategoryDAO},
 *
 * @author <a href="https://github.com/cris6h16/" rel="Noopener noreferrer" target="_blank">Cristian</a>
 */

public class CategoryDAOCriteria implements CategoryDAO {
    private final SessionFactory sessionFactory;
    private final String ID_FIELD = "id";
    private final String NAME_FIELD = "name";
    private final String CATEGORY_FIELD = "category_id";

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
            affectedRows = deleteById(session, id);
        } catch (Exception e) {
            handleSevereException(e, "deleteById", id.toString());
        }
        return affectedRows > 0;
    }

    private int deleteById(Session session, Long id) {
        int affectedRows = 0;
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
        return affectedRows;
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
     * @param categoryEagerly CategoryEntity must be eagerly fetched. we'll do .isEmpty(), here it fails: <br><br>
     */
    @Override
    public boolean merge(CategoryEntity categoryEagerly) {

        if (categoryEagerly == null) return false;
        if (categoryEagerly.getId() == null) return false;
        if (categoryEagerly.getName() == null) return false;
        if (categoryEagerly.getName().isEmpty()) return false;

        boolean updated = false;

        try (Session session = sessionFactory.openSession()) {
            updated = update(session, categoryEagerly);

        } catch (Exception e) {
            handleSevereException(e, "update", categoryEagerly.toString());
        }
        return updated;
    }

    private boolean update(Session session, CategoryEntity categoryEagerly) {
        int affectedRowsCategory = 0; //mutated rows
        int affectedRowsProduct = 0; //mutated rows
        try {
            session.beginTransaction();
            //Obtain the criteria builder
            CriteriaBuilder builder = session.getCriteriaBuilder();

            //Update the category
            CriteriaUpdate<CategoryEntity> update = builder.createCriteriaUpdate(CategoryEntity.class);
            Root<CategoryEntity> root = update.from(CategoryEntity.class);

            update = update
                    .where(builder.equal(root.get(ID_FIELD), categoryEagerly.getId()))
                    .set(NAME_FIELD, categoryEagerly.getName());
            MutationQuery cQuery = session.createMutationQuery(update);
            affectedRowsCategory = cQuery.executeUpdate();

            //if the categoryFromParameter hasn't passed products, simply delete its products in DB
            if (categoryEagerly.getProducts().isEmpty()) {
                CriteriaDelete<ProductEntity> delete = builder.createCriteriaDelete(ProductEntity.class);
                Root<ProductEntity> mutationRoot = delete.from(ProductEntity.class);
                delete = delete.where(builder.equal(mutationRoot.get("category").get("id"), categoryEagerly.getId()));
                affectedRowsProduct = affectedRowsProduct + session
                        .createMutationQuery(delete)
                        .executeUpdate();
            }


            //for each product in the category passed as parameter
            for (ProductEntity productFromParameter : categoryEagerly.getProducts()) {
                // - if it's got a valid id
                // THEN UPDATE IT
                if (isIdValid(productFromParameter.getId())) {
                    CriteriaUpdate<ProductEntity> updateFor = builder.createCriteriaUpdate(ProductEntity.class);
                    Root<ProductEntity> productFor = updateFor.from(ProductEntity.class);
                    updateFor = updateFor
                            .where(builder.equal(productFor.get(ProductDAO.ID_FIELD), productFromParameter.getId()))
                            .set(ProductDAO.NAME_FIELD, productFromParameter.getName())
                            .set(ProductDAO.DESCRIPTION_FIELD, productFromParameter.getDescription())
                            .set(ProductDAO.PRICE_FIELD, productFromParameter.getPrice());
                    MutationQuery queryFor = session.createMutationQuery(updateFor);
                    affectedRowsProduct = affectedRowsProduct + queryFor.executeUpdate();
                }
                //this method is UPDATE, if the productFromParameter hasn't a valid id, we throw an exception
                if (!isIdValid(productFromParameter.getId()) ) {
                    throw new IllegalArgumentException("Product with name: " + productFromParameter.getName() +
                            " hasn't a valid id, so it can't be updated");
                }
                if (!isNameValid(productFromParameter.getName())) {
                    throw new IllegalArgumentException("Product with name: " + productFromParameter.getName() +
                            " hasn't a valid name, so it can't be updated");
                }

            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
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
        if (category.getName() == null) return;
        if (category.getName().isEmpty()) return;


        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.persist(category);
                session.refresh(category);
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
            Predicate predicate = criteriaBuilder.equal(root.get("name"), name);
            criteriaQuery = criteriaQuery.where(predicate);

            // Create the query and return the result
            Query<CategoryEntity> query = session.createQuery(criteriaQuery);

            //obtain the result
            entity = query.getSingleResultOrNull();

        } catch (Exception e) {
            handleSevereException(e, "findByName", name);
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
        CategoryEntity entity = null;

        try (Session session = sessionFactory.openSession()) {
            entity = getCategoryEntityById(session, id).orElse(null);

        } catch (Exception e) {
            handleSevereException(e, "findById", id.toString());
        }

        return Optional.ofNullable(entity);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<CategoryEntity> criteriaQuery = criteriaBuilder.createQuery(CategoryEntity.class);

            Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

            // get result
            Query<CategoryEntity> query = session.createQuery(criteriaQuery);
            categories = query.list();

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
            Fetch<CategoryEntity, ProductEntity> fetch = root.fetch("products", JoinType.LEFT);
            CriteriaQuery<CategoryEntity> criteriaQuery = query.where(builder.equal(root.get(ID_FIELD), id));
            category = Optional.ofNullable(session.createQuery(criteriaQuery).getSingleResultOrNull());

        } catch (HibernateException | IllegalStateException | IllegalArgumentException |
                 NonUniqueResultException e) {
            handleSevereException(e, "getByIdEager", ExceptionHandler.SEVERE, id.toString());
        }

        if (category.isEmpty()) return Optional.empty();
//
        return category;
    }

    private Optional<CategoryEntity> getCategoryEntityById(Session session, Long id) {
        // Create the criteria builder
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<CategoryEntity> criteriaQuery = criteriaBuilder.createQuery(CategoryEntity.class);
        Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

        // Create the 'id' = id restriction
        Predicate predicate = criteriaBuilder.equal(root.get(ID_FIELD), id);
        criteriaQuery = criteriaQuery.where(predicate);

        //get the category
        Query<CategoryEntity> query = session.createQuery(criteriaQuery);

        return Optional.ofNullable(query.getSingleResultOrNull());
    }


    private boolean isIdValid(Long id) {
        return id != null && id > 0;
    }

    private void handleSevereException(Exception e, String method, String type, String... params) {
        ExceptionHandler.handleException(this.getClass().getName(), e, method, type, params);
    }

}
