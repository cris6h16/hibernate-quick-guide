package org.example.DAOs;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.example.Entities.CategoryEntity;
import org.example.Util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

//TODO: Exception handling for sessionFactory.openSession()
public class CategoryDAO {
    private SessionFactory sessionFactory;

    public CategoryDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<CategoryEntity> listAll() {
        List<CategoryEntity> categories;

        try (Session session = sessionFactory.openSession()) {
            Query<CategoryEntity> query = session.createQuery(
                    "from CategoryEntity", CategoryEntity.class);
            categories = query.list();
        }

        return categories;
    }

    public Optional<CategoryEntity> findById(Long id) {

        if (id == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            CategoryEntity category = session.get(CategoryEntity.class, id);
            return Optional.ofNullable(category);
        }

    }

    public Optional<CategoryEntity> findByName(String name) {

        if (name == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            Query<CategoryEntity> query = session.createQuery(
                    "FROM CategoryEntity ce WHERE ce.name = :name", CategoryEntity.class);
            query.setParameter("name", name);

            //Only one result, CategoryEntity.name is UNIQUE
            CategoryEntity category = query.getSingleResultOrNull();

            return Optional.ofNullable(category);
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
    public List<CategoryEntity> listAllWithEmptyRows() {
        List<CategoryEntity> categories;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // It deletes rows only if we commit the transaction
            session.createQuery("delete from CategoryEntity").executeUpdate();

            Query<CategoryEntity> query = session.createQuery(
                    "from CategoryEntity", CategoryEntity.class);
            categories = query.list();

            session.getTransaction().rollback();
        }

        return categories;
    }

    public void save(CategoryEntity category) {
        if (category == null) return;
        if (category.getName() == null) return;
        if (category.getName().isEmpty()) return;


        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(category);
            session.getTransaction().commit();
        }
    }


    public boolean update(CategoryEntity category) {

        if (category == null) return false;
        if (category.getId() == null) return false;
        if (category.getName() == null) return false;
        if (category.getName().isEmpty()) return false;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(category);
            session.getTransaction().commit();
        }
        return true;
    }

    public boolean deleteById(Long id) {

        if (id == null) return false;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<CategoryEntity> query = session.createQuery("DELETE FROM CategoryEntity c WHERE c.id = :id");
            query.setParameter("id", id).executeUpdate();

            session.getTransaction().commit();
        }
        return true;
    }
}
