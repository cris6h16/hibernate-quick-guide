package org.example.DAOs.Category;

import org.example.Entities.CategoryEntity;
import org.example.Exceptions.ExceptionHandler;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CategoryDAONative implements CategoryDAO {

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
                MutationQuery query = session.createNativeMutationQuery
                        ("DELETE FROM categories c WHERE c.id = :id");
                affectedRows = query.setParameter("id", id).executeUpdate();
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

    /**
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */
    @Override
    public boolean merge(CategoryEntity category) {
        if (category == null) return false;
        if (category.getId() == null) return false;
        if (category.getName() == null) return false;
        if (category.getName().isEmpty()) return false;

        int affectedRows = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            try {
                MutationQuery query = session.createNativeMutationQuery
                        ("UPDATE category SET name = :name");
                query = query.setParameter("name", category.getName());
                affectedRows = query.executeUpdate();

                // if category passed hasn't products
                if (category.getProducts() == null || category.getProducts().isEmpty()) {
                    // delete all products from category in DB
                    MutationQuery mutationQuery = session.createNativeMutationQuery
                            ("DELETE FROM products p WHERE p.category_id = :idCat");
                    mutationQuery = mutationQuery.setParameter("idCat", category.getId());
                    mutationQuery.executeUpdate();

                    return affectedRows > 0; //TODO: see if return here the session is closed correctly
                }

                // foreach product in category passed
                category.getProducts().forEach(product -> {
                    //if product has id, update it
                    if (product.getId() != null) {
                        MutationQuery mutationQuery = session.createNativeMutationQuery(
                                "UPDATE products SET name= :name AND SET " +
                                        "description= :description AND SET price= :price WHERE id = :id");
                        mutationQuery.setParameter("name", product.getName());
                        mutationQuery.setParameter("description", product.getDescription());
                        mutationQuery.setParameter("price", product.getPrice());
                        mutationQuery.setParameter("id", product.getId());
                        mutationQuery.executeUpdate();
                    } else {
                        //if product hasn't id, save it
                        MutationQuery mutationQuery = session.createNativeMutationQuery(
                                "INSERT INTO products (name, description, price, category_id) " +
                                        "VALUES (:name, :description, :price, :idCat)");
                        mutationQuery.setParameter("name", product.getName());
                        mutationQuery.setParameter("description", product.getDescription());
                        mutationQuery.setParameter("price", product.getPrice());
                        mutationQuery.setParameter("idCat", category.getId());
                        mutationQuery.executeUpdate();
                    }
                });

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e) {
            handleException(e, "update", category.toString());
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
    public void save(CategoryEntity category) {
        if (category != null) return;
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
        return null;
    }

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given name exists
     */
    @Override
    public Optional<CategoryEntity> findByName(String name) {
        return Optional.empty();
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    @Override
    public Optional<CategoryEntity> findById(Long id) {
        return Optional.empty();
    }

    /**
     * Returns a list of all categories in the database.
     *
     * @return a list of all categories in the database
     */
    @Override
    public List<CategoryEntity> listAll() {
        return null;
    }

    /**
     * Returns an Optional of a CategoryEntity with the given id, including its associated products.
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    @Override
    public Optional<CategoryEntity> getByIdEager(Long id) {
        return Optional.empty();
    }

    private void handleException(Exception e, String method, String... params) {
        ExceptionHandler.handleException(this.getClass().getName(), e, method, Arrays.toString(params));
    }
}
