package org.example.DAOs.Product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.Entities.ProductEntity;
import org.example.Exceptions.ExceptionHandler;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;

public class ProductDAO {
    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String PRICE_FIELD = "price";
    public static final String CATEGORY_FIELD = "category_id";
    public static final String DESCRIPTION_FIELD = "description";

    public void save(ProductEntity product) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                session.beginTransaction();
                session.persist(product);
                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        } catch (Exception e) {
                handleSevereException(e, "save", ExceptionHandler.SEVERE, product.toString());
        }
    }

    public void merge(ProductEntity product) {
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
                handleSevereException(e, "merge", ExceptionHandler.SEVERE, product.toString());
        }
    }


    private void handleSevereException(Exception e, String method, String type, String... params) {
        ExceptionHandler.handleException(this.getClass().getName(), e, method, type, params);
    }
}
