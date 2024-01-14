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
//TODO: rewrite the description of this method, because jus merge the category, not its products

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
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME,
                        CategoryEntity.ATTR_ID);

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
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME);

                session.createNativeMutationQuery(insertSql)
                        .setParameter("name", categoryDTO.getName())
                        .executeUpdate();

                String getNewIdSql = String.format("SELECT %s FROM %s.%s WHERE %s = :name",
                        CategoryEntity.ATTR_ID,
                        CategoryEntity.SCHEMA_NAME,
                        CategoryEntity.TABLE_NAME,
                        CategoryEntity.ATTR_NAME);

                categoryDTO.setId(session
                        .createNativeQuery(getNewIdSql, java.lang.Long.class)
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
}
