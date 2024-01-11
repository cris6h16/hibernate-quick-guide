package org.example.DAOs.OneToMany_Unidirectional.Product;

import org.example.DAOs.OneToMany_Unidirectional.Product.Exceptions.ProductAlreadyExistException;
import org.example.Entities.OneToManyToOne_Unidirectional.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ProductDAOImpl implements ProductDAO {
    public static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());

    @Override
    public void persist(ProductEntity product) {
        if (product == null) {
            LOGGER.warning("Product is null");
            return;
        }
        if (product.getName() == null) {
            LOGGER.warning("Product name is null");
            return;
        }
        if (product.getName().isEmpty()) {
            LOGGER.warning("Product name is empty");
            return;
        }
        if (product.getCategory() != null) {
            LOGGER.warning("First save the product, then set the category");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Integer count = ((Number) session
                        .createQuery("select count(*) from ProductEntity where name = :name")
                        .setParameter("name", product.getName())
                        .uniqueResult()).intValue();

                if (count > 0) throw new ProductAlreadyExistException();

                session.beginTransaction();
                session.persist(product);
                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (ProductAlreadyExistException pe) {
            LOGGER.warning("Product already exist");
        } catch (Exception e) {
            LOGGER.severe("Error saving product");
            e.printStackTrace();
        }
    }

    public void merge(ProductEntity product) {
        if (product == null) {
            LOGGER.warning("Product is null");
            return;
        }
        if (product.getId() == null) {
            LOGGER.warning("Product id is null");
            return;
        }
        if (product.getName() == null) {
            LOGGER.warning("Product name is null");
            return;
        }
        if (product.getName().isEmpty()) {
            LOGGER.warning("Product name is empty");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                session.merge(product);
                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Error merging product");
            e.printStackTrace();
        }
    }

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
                affectedRows = session
                        .createMutationQuery("delete from ProductEntity p where p.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting product");
            e.printStackTrace();
            return false;
        }

        return affectedRows > 0;
    }

    @Override
    public Optional<ProductEntity> findById(Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return Optional.empty();
        }
        Optional<ProductEntity> product;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            product = Optional.ofNullable(
                    session.find(ProductEntity.class, id)
            );
        } catch (Exception e) {
            LOGGER.severe("Error finding product by id");
            e.printStackTrace();
            return Optional.empty();
        }

        return product;
    }

    @Override
    public List<ProductEntity> listAll() {
        List<ProductEntity> products;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            products = session
                    .createQuery("from ProductEntity", ProductEntity.class)
                    .list();
        } catch (Exception e) {
            LOGGER.severe("Error listing all products");
            e.printStackTrace();
            return List.of();
        }

        return products;
    }

    @Override
    public Optional<ProductEntity> getByIdEager(Long id) {
        if (id == null) {
            LOGGER.warning("Id is null");
            return Optional.empty();
        }
        Optional<ProductEntity> product;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            product = Optional.ofNullable(
                    session.createQuery("select p from ProductEntity p join fetch p.category where p.id = :id", ProductEntity.class)
                            .setParameter("id", id)
                            .getSingleResult());

        } catch (Exception e) {
            LOGGER.severe("Error finding product by id Eager");
            e.printStackTrace();
            return Optional.empty();
        }

        return product;
    }
}
