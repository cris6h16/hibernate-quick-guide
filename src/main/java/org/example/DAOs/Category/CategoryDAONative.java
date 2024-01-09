package org.example.DAOs.Category;

import org.example.DAOs.Product.ProductDAO;
import org.example.Entities.CategoryEntity;
import org.example.Entities.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CategoryDAONative implements CategoryDAO {
    public static final Logger LOGGER = Logger.getLogger(CategoryDAONative.class.getName());
    private final String ID_FIELD = "c_id";
    private final String NAME_FIELD = "c_name";
    private final String ATTRIBUTE_PRODUCTS = "c_products";
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
        if (id == null) {
            LOGGER.warning("Id is null");
            return false;
        }

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
            LOGGER.severe("Exception in deleteById: " + e.getMessage());
            e.printStackTrace();
        }

        return affectedRows > 0;
    }
//TODO: rewrite the description of this method, because jus merge the category, not its products

    /**
     * @param categoryDTO if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */
    @Override
    public boolean merge(CategoryEntity categoryDTO) {
        if (categoryDTO == null) {
            LOGGER.warning("Category is null");
            return false;
        }
        if (categoryDTO.getId() == null) {
            LOGGER.warning("Category id is null");
            return false;
        }
        if (categoryDTO.getName() == null) {
            LOGGER.warning("Category name is null");
            return false;
        }
        if (categoryDTO.getName().isEmpty()) {
            LOGGER.warning("Category name is empty");
            return false;
        }

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
                        .setParameter("name", categoryDTO.getName())
                        .setParameter("id", categoryDTO.getId())
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
     * Saves a CategoryEntity object to the database. if was saved successfully
     * the category will have id assigned
     *
     * @param categoryDTO the CategoryEntity object to be saved
     */
    @Override
    public void persist(CategoryEntity categoryDTO) {
        if (categoryDTO == null) {
            LOGGER.warning("Category is null");
            return;
        }
        if (categoryDTO.getName() == null) {
            LOGGER.warning("Category name is null");
            return;
        }
        if (categoryDTO.getName().isEmpty()) {
            LOGGER.warning("Category name is empty");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {

                String insertSql = String.format("INSERT INTO %s.%s (%s) VALUES (:name)",
                        SCHEMA_NAME,
                        TABLE_NAME,
                        NAME_FIELD);

                session.createNativeMutationQuery(insertSql)
                        .setParameter("name", categoryDTO.getName())
                        .executeUpdate();

                String getNewIdSql = String.format("SELECT %s FROM %s.%s WHERE %s = :name",
                        ID_FIELD,
                        SCHEMA_NAME,
                        TABLE_NAME,
                        NAME_FIELD);

                categoryDTO.setId(session
                        .createNativeQuery(getNewIdSql, Long.class)
                        .setParameter("name", categoryDTO.getName())
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
                    SCHEMA_NAME,
                    TABLE_NAME,
                    NAME_FIELD);

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
    public Optional<CategoryEntity> findById(Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return Optional.empty();
        }

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
            String sql = String.format("SELECT * FROM %s.%s", SCHEMA_NAME, TABLE_NAME);
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
     * Returns an Optional of a CategoryEntity with the given id, including its associated products.
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    @Override
    public Optional<CategoryEntity> getByIdEager(Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return Optional.empty();
        }

        Optional<CategoryEntity> categoryEntity = Optional.empty();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String sql = String.format("SELECT * FROM %s.%s c JOIN %s.%s p ON c.%s=p.%s WHERE c.%s = :id",
                    SCHEMA_NAME, TABLE_NAME,
                    SCHEMA_NAME,  ProductDAO.TABLE_NAME,
                    NAME_FIELD, ProductDAO.FIELD_CATEGORY,
                    ID_FIELD);

            categoryEntity = session
                    .createNativeQuery(sql, CategoryEntity.class)
                    .setParameter("id", id)
                    .addEntity(CategoryEntity.class)
                    .addEntity("c", CategoryEntity.class)
                    .addJoin("p", "c.c_products")
                    .uniqueResultOptional();

            System.out.println();
        } catch (Exception e) {
            LOGGER.severe("Exception in getByIdEager: " + e.getMessage());
            e.printStackTrace();
        }
        return categoryEntity;
    }
}
