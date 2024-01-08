package org.example.DAOs.Product;

import org.example.DAOs.Product.Exceptions.ProductAlreadyExist;
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
    public static final String TABLE_NAME = "products";

    public void save(ProductEntity product) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Integer count = ((Number) session.createQuery("select count(*) from ProductEntity where p_name = :name")
                        .setParameter("name", product.getP_name())
                        .uniqueResult()).intValue();

                if (count > 0) throw new ProductAlreadyExist();

                session.beginTransaction();
                session.persist(product);
                session.getTransaction().commit();

            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }catch (ProductAlreadyExist pe){
            handleSevereException(pe, "save", ExceptionHandler.WARNING, product.toString());
        }catch (Exception e) {
            handleSevereException(e, "save", ExceptionHandler.SEVERE, product.toString());
        }
    }

    public void merge(ProductEntity product) {
        if (product == null) return;
        if (product.getP_id() == null) return;
        if (product.getP_name() == null) return;
        if (product.getP_name().isEmpty()) return;

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
