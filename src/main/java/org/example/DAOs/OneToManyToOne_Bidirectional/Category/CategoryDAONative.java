package org.example.DAOs.OneToManyToOne_Bidirectional.Category;

import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;
import org.example.Entities.OneToManyToOne_Bidirectional.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CategoryDAONative implements CategoryDAO {
    public static final Logger LOGGER = Logger.getLogger(CategoryDAONative.class.getName());

    private Optional<CategoryEntity> categoryEntity;

    /**
     * Deletes a CategoryEntity from the database.
     *
     * @param id the id of the CategoryEntity to delete
     * @return true if the deletion was successful, false otherwise
     */
    @Override
    public boolean deleteById(java.lang.Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return false;
        }

        int affectedRows = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                String deleteSql = String.format("DELETE FROM %s.%s WHERE %s = :id",
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_ID);
                affectedRows = session
                        .createNativeMutationQuery(deleteSql)
                        .setParameter("id", id).executeUpdate();

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
     *
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
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

        int affectedRows = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {

                String sql = String.format("UPDATE %s.%s SET %s = :name WHERE %s = :id",
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME,
                        CategoryEntity.ATTR_ID);

                affectedRows = session
                        .createNativeMutationQuery(sql)
                        .setParameter("name", category.getName())
                        .setParameter("id", category.getId())
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

        return affectedRows > 0;
    }

    /**
     * Saves a CategoryEntity object to the database.
     * if was saved successfully
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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {

                String insertSql = String.format("INSERT INTO %s.%s (%s) VALUES (:name)",
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME);

                session.createNativeMutationQuery(insertSql)
                        .setParameter("name", category.getName())
                        .executeUpdate();

                String getNewIdSql = String.format("SELECT %s FROM %s.%s WHERE %s = :name",
                        CategoryEntity.ATTR_ID,
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME);

                category.setId(session
                        .createNativeQuery(getNewIdSql, java.lang.Long.class)
                        .setParameter("name", category.getName())
                        .getSingleResultOrNull());

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
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
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                String deleteSql = String.format("DELETE FROM %s.%s", CategoryEntity.SCHEMA_NAME, CategoryEntity.TABLE_NAME);
                session.createNativeMutationQuery(deleteSql).executeUpdate();

                String selectAllSql = String.format("SELECT * FROM %s.%s", CategoryEntity.SCHEMA_NAME, CategoryEntity.TABLE_NAME);
                categoryEntities = session.createNativeQuery(selectAllSql, CategoryEntity.class).list();

                session.getTransaction().rollback();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Exception in listAllWithEmptyRows: " + e.getMessage());
            e.printStackTrace();
        }

        return categoryEntities;
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
            LOGGER.warning("Name is null");
            return Optional.empty();
        }
        if (name.isEmpty()) {
            LOGGER.warning("Name is empty");
            return Optional.empty();
        }

        Optional<CategoryEntity> categoryEntity = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = String.format("SELECT * FROM %s.%s WHERE %s = :name",
                    CategoryEntity.SCHEMA_NAME,
                    CategoryEntity.TABLE_NAME,
                    CategoryEntity.ATTR_NAME);

            categoryEntity = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.severe("Exception in findByName: " + e.getMessage());
            e.printStackTrace();
        }

        return categoryEntity;
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    @Override
    public Optional<CategoryEntity> findById(java.lang.Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return Optional.empty();
        }

        Optional<CategoryEntity> categoryEntity = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = String.format("SELECT * FROM %s.%s WHERE %s = :id",
                    CategoryEntity.SCHEMA_NAME,
                    CategoryEntity.TABLE_NAME,
                    CategoryEntity.ATTR_ID);
            categoryEntity = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.severe("Exception in findById: " + e.getMessage());
            e.printStackTrace();
        }
        return categoryEntity;
    }

    /**
     * Returns a list of all categories in the database.
     *
     * @return a list of all categories in the database
     */
    @Override
    public List<CategoryEntity> listAll() {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = String.format("SELECT * FROM %s.%s", CategoryEntity.SCHEMA_NAME, CategoryEntity.TABLE_NAME);
            categoryEntities = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .list();
        } catch (Exception e) {
            LOGGER.severe("Exception in listAll: " + e.getMessage());
            e.printStackTrace();
        }

        return categoryEntities;
    }

    /**
     * Returns an Optional of a CategoryEntity with the given id, including its associated products(Collection Initialized).
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    @Override
    public Optional<CategoryEntity> getByIdEager(java.lang.Long id) {

        List<ProductEntity> productEntities = new ArrayList<>();
        Optional<CategoryEntity> categoryEntity = Optional.empty();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Get the category
            String getCategorySql = String.format("SELECT * FROM %s.%s WHERE %s = :id",
                    CategoryEntity.SCHEMA_NAME, CategoryEntity.TABLE_NAME, CategoryEntity.ATTR_ID);
            categoryEntity = session.createNativeQuery(getCategorySql, CategoryEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();

            // Get the products of the category
            String getCategoryProductsSql = String.format("SELECT * FROM %s.%s WHERE %s = :id",
                    CategoryEntity.SCHEMA_NAME, ProductEntity.TABLE_NAME, ProductEntity.FIELD_CATEGORY);
            productEntities = session.createNativeQuery(getCategoryProductsSql, ProductEntity.class)
                    .setParameter("id", id)
                    .list();
        }
        if (categoryEntity.isEmpty()) {
            return Optional.empty();
        }

        // Create a new CategoryEntity with the adding the products (avoid LazyInitializationException)
        Optional<CategoryEntity> finalCategoryEntity = Optional.of(
                new CategoryEntity(
                        categoryEntity.get().getId(),
                        categoryEntity.get().getName(),
                        productEntities));

        return finalCategoryEntity;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public int countPages(int resultsPerPage) {
        return 0;
    }

    @Override
    public List<CategoryEntity> listPagination(int pageNum, int resultsPerPage) {
        return null;
    }
}
