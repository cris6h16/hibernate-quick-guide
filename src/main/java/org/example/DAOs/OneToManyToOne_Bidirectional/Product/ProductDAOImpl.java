package org.example.DAOs.OneToManyToOne_Bidirectional.Product;

import org.example.DAOs.OneToManyToOne_Bidirectional.Product.Exceptions.ProductAlreadyExistException;
import org.example.Entities.OneToManyToOne_Bidirectional.ProductEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

import java.util.logging.Logger;

public class ProductDAOImpl implements ProductDAO {
    public static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());


    public void save(ProductEntity product) {
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
                Integer count = ((Number) session.createQuery("select count(*) from ProductEntity where name = :name")
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
}
