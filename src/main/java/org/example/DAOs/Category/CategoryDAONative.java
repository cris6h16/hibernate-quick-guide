package org.example.DAOs.Category;

import org.example.DAOs.Product.ProductDAO;
import org.example.Entities.CategoryEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CategoryDAONative implements CategoryDAO {
    private final String ID_FIELD = "c_id";
    private final String NAME_FIELD = "c_name";
    private final String TABLE_NAME = "categories";
    private final String SCHEMA_NAME = "tienda";

    private Optional<CategoryEntity> categoryEntity;

    /**
     * Deletes a CategoryEntity from the database.
     *
     * @param id the id of the CategoryEntity to delete
     * @return true if the deletion was successful, false otherwise
     */
    @Override
    public boolean deleteById(Long id) {
        if (id == null) return false;

        int affectedRows = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                String deleteSql = String.format("DELETE FROM %s.%s WHERE %s = :id",
                        SCHEMA_NAME,
                        TABLE_NAME,
                        ID_FIELD);
                affectedRows = session
                        .createNativeMutationQuery(deleteSql)
                        .setParameter("id", id).executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            handleException(e, "deleteById", id.toString());
        }

        return affectedRows > 0;
    }
//TODO: rewrite the description of this method, because jus merge the category, not its products

    /**
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */
    @Override
    public boolean merge(CategoryEntity category) {
        if (category == null) return false;
        if (category.getC_id() == null) return false;
        if (category.getC_name() == null) return false;
        if (category.getC_name().isEmpty()) return false;

        int affectedRows = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {

                String sql = String.format("UPDATE %s.%s SET %s = :name WHERE %s = :id",
                        SCHEMA_NAME,
                        TABLE_NAME,
                        NAME_FIELD,
                        ID_FIELD);

                affectedRows = session
                        .createNativeMutationQuery(sql)
                        .setParameter("name", category.getC_name())
                        .setParameter("id", category.getC_id())
                        .executeUpdate();

                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            handleException(e, "merge", category.toString());
        }

        return affectedRows > 0;
    }

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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {

                String insertSql = String.format("INSERT INTO %s.%s (%s) VALUES (:name)",
                        SCHEMA_NAME,
                        TABLE_NAME,
                        NAME_FIELD);

                session.createNativeMutationQuery(insertSql)
                        .setParameter("name", category.getC_name())
                        .executeUpdate();

                String getNewIdSql = String.format("SELECT %s FROM %s.%s WHERE %s = :name",
                        ID_FIELD,
                        SCHEMA_NAME,
                        TABLE_NAME,
                        NAME_FIELD);

                category.setC_id(session
                        .createNativeQuery(getNewIdSql, Long.class)
                        .setParameter("name", category.getC_name())
                        .getSingleResultOrNull());

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
//            handleException(e, "save", ExceptionHandler.SEVERE, category.toString());
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
                String deleteSql = String.format("DELETE FROM %s.%s", SCHEMA_NAME, TABLE_NAME);
                session.createNativeMutationQuery(deleteSql).executeUpdate();

                String selectAllSql = String.format("SELECT * FROM %s.%s", SCHEMA_NAME, TABLE_NAME);
                categoryEntities = session.createNativeQuery(selectAllSql, CategoryEntity.class).list();

                session.getTransaction().rollback();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
//            handleException(e, "listAllWithEmptyRows", ExceptionHandler.SEVERE, "n/a");
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
        if (name == null) return Optional.empty();
        if (name.isEmpty()) return Optional.empty();

        Optional<CategoryEntity> categoryEntity = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = String.format("SELECT * FROM %s.%s WHERE %s = :name",
                    SCHEMA_NAME,
                    TABLE_NAME,
                    NAME_FIELD);

            categoryEntity = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
        } catch (Exception e) {
//            handleException(e, "findByName", ExceptionHandler.SEVERE, name);
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
    public Optional<CategoryEntity> findById(Long id) {
        if (id == null) return Optional.empty();

        Optional<CategoryEntity> categoryEntity = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = String.format("SELECT * FROM %s.%s WHERE %s = :id",
                    SCHEMA_NAME,
                    TABLE_NAME,
                    ID_FIELD);
            categoryEntity = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
//            handleException(e, "findById", ExceptionHandler.SEVERE, id.toString());
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
            String sql = String.format("SELECT * FROM %s.%s", SCHEMA_NAME, TABLE_NAME);
            categoryEntities = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .list();
        } catch (Exception e) {
//            handleException(e, "listAll", ExceptionHandler.SEVERE, "n/a");
        }
        return categoryEntities;
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

        Optional<CategoryEntity> categoryEntity = Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String sql = String.format("SELECT * FROM %s.%s c LEFT JOIN %s.%s p ON p.%s = c.%s WHERE c.%s = :id",
                    SCHEMA_NAME, TABLE_NAME,
                    SCHEMA_NAME, ProductDAO.TABLE_NAME,
                    ProductDAO.CATEGORY_FIELD, ID_FIELD,
                    ID_FIELD);

            categoryEntity = session.createNativeQuery(sql)
                    .setParameter("id", id)
                    .addEntity("c", CategoryEntity.class)
                    .addJoin("p", "c.products")  // Map the joined products to the `products` collection
                    .uniqueResultOptional();


        } catch (Exception e) {
//            handleException(e, "getByIdEager", ExceptionHandler.SEVERE, id.toString());
        }
        return categoryEntity;
    }

    private void handleException(Exception e, String method, String type, String... params) {
//        ExceptionHandler.handleException(this.getClass().getName(), e, method, type, Arrays.toString(params));
    }
}
