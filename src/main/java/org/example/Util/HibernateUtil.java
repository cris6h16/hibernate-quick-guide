package org.example.Util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    /**
     * Get the Hibernate SessionFactory
     *
     * @return SessionFactory object
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();
                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);
                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder()
//                        .applyInterceptor(new CategoryInterceptor())
                        .build();

            } catch (Exception e) {
                handleException(e);
                closeRegistry();
            }
        }

        return sessionFactory;
    }

    public static Session getCurrentSession() {
        Session session = null;
        try {
            session = getSessionFactory().getCurrentSession();
        }catch (Exception e){
            handleException(e);
        }

        return (session == null) ? (getSessionFactory().openSession()) : session;
    }


    /**
     * Shutdown the SessionFactory
     */
    public static void shutdown() {
        closeRegistry();
    }

    /**
     * Handle the exceptions during the creation of the SessionFactory.
     *
     * @param e exception to handle
     */
    private static void handleException(Exception e) {
        e.printStackTrace();
        // Do something with the Exception
    }

    /**
     * Close the registry
     */
    private static void closeRegistry() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
