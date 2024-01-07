# Hibernate Quick Guide

Hibernate is a Java-based framework that simplifies the interaction with databases by providing an object-relational
mapping (ORM) solution. It allows developers to map Java objects to database tables, making database access and
manipulation more convenient and abstracted from low-level SQL queries. This helps in building efficient and
maintainable data-driven applications.

## 1. Dependencies _[Maven](https://mvnrepository.com/)_

### 1.1 Hibernate Core

```xml
<!-- https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.1.Final</version>
</dependency>
```

### 1.2 Databases (choose one)

PD: if something fails, try to change from the lastest version to the version installed in your computer.

- PostgreSQL

```xml
<!-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

- MySQL

```xml
<!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.2.0</version>
</dependency> 
```

## 2. Hibernate Configuration

You need to create a file called `hibernate.cfg.xml` in the `src/main/resources` folder.

This file contains the database connection, configuration, Information and the mapping between the Java classes and the
database tables.

**Template**:

```xml
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="connection.url"/>
        <property name="connection.driver_class"/>
        <!-- <property name="connection.username"/> -->
        <!-- <property name="connection.password"/> -->

        <!-- DB schema will be updated if needed -->
        <!-- <property name="hibernate.hbm2ddl.auto">update</property> -->
    </session-factory>
</hibernate-configuration>
```

 PROPERTY                         | DESCRIPTION                                                                                              
----------------------------------|----------------------------------------------------------------------------------------------------------
 `connection.url`                 | The JDBC URL of the database.                                                                            
 `connection.driver_class`        | The JDBC driver class.                                                                                   
 `connection.username`            | The database username.                                                                                   
 `connection.password`            | The database password.                                                                                   
 `dialect`                        | The SQL dialect of the database.                                                                         
 `hibernate.hbm2ddl.auto`         | Automatically validates or exports schema DDL to the database when the SessionFactory is created.        
 `show_sql`                       | Prints all SQL statements to the console.                                                                
 `format_sql`                     | Pretty print the SQL statements.                                                                         
 `current_session_context_class`  | The context of the current session. Default is `thread`, which is the recommended option, see java docs. 
 `hibernate.hbm2ddl.import_files` | The files to import when the SessionFactory is created. (sql queries)                                    

#### Values

- `connection.url`:
    - MySQL: `jdbc:mysql://localhost:3306/DB_NAME`
    - PostgreSQL: `jdbc:postgresql://localhost:5432/DB_NAME`
    - etc_


- `connection.driver_class`:
    - MySQL: `com.mysql.cj.jdbc.Driver`
    - PostgreSQL: `org.postgresql.Driver`
    - etc


- `dialect`:
    - MySQL: `org.hibernate.dialect.MySQL8Dialect`, or other version (example is using 8).
    - PostgreSQL: `org.hibernate.dialect.PostgreSQLDialect`
    - etc


- `hibernate.hbm2ddl.auto`:
    - `create`: Creates the schema, destroying previous data.
    - `create-drop`: Creates the schema and drops it when the SessionFactory is closed explicitly, typically when the
      application is stopped.
    - `update`: Updates the schema.
    - `validate`: Validates the schema, makes no changes to the database.
    - `none`: Does nothing with the schema, makes no changes to the database.


- `show_sql`:
    - `true`: Prints all SQL statements to the console.
    - `false`: Does not print SQL statements to the console.


- `format_sql`:
    - `true`: Pretty print the SQL statements.
    - `false`: Does not pretty print the SQL statements.


- `current_session_context_class`:
    - `thread`: _The recommended option._ The `Session` is opened when getCurrentSession() is called for the first time
      and closed when the
      transaction ends. if you've got 5 concurrent users, you'll have 5 sessions.
  ```java
   // Example of using thread-based session context in Hibernate
    SessionFactory sessionFactory = // obtain SessionFactory
    
  // In each thread:
    try (Session session = sessionFactory.getCurrentSession()) {
        // No need to explicitly open or close the session
        Transaction transaction = session.beginTransaction();

        // Perform database operations

        transaction.commit();
        // The session is automatically closed
    }
    ```
    - `jta`: Involves external transaction management, such as a Java EE container; The `Session` is bound to and
      unbound from the JTA transaction lifecycle.
    ```java
    // Example using JTA with Hibernate
    UserTransaction tx = // obtain UserTransaction from JNDI or other means

    try {
        tx.begin();

        // Perform multiple database operations within a single transaction

        tx.commit();
    } catch (Exception e) {
        if (tx != null) {
            tx.rollback();
        }
        e.printStackTrace();
    }
    ```
    - `managed`: The `Session` is managed by the container and injected into the application.
    ```java
    // Example using managed session context in native Hibernate API
    try (Session session = sessionFactory.openSession()) {
        // Hibernate takes care of opening and closing the session
        Transaction transaction = session.beginTransaction();

        // Perform database operations

        transaction.commit();
    }  
  // Hibernate automatically closes the session at the end of the try-with-resources block
    ```
    - `org.example.<MyCustomSessionContext.java>`: The `Session` is managed by a custom implementation of
      the `org.hibernate.context.spi.CurrentSessionContext`
      interface, you can implement your own `CurrentSessionContext` and use it with this option.


- `hibernate.hbm2ddl.import_files`:
    - `import.sql`: The default file name for import script.
    - `<filename1>.sql, <filename2>.sql, etc`: Execute the multiple SQL scripts.(in order)

### Access to environment variables

You can access to environment variables in the `hibernate.cfg.xml` file using the `${ENV_VAR}` syntax.
Take care the access to environment variables in linux are a common problem, a related problem happened to me.

## 3. Hibernate SessionFactory

You need to create a class for obtain the `SessionFactory` object.
I'll create `org.example.Util.HibernateUtil.java`

```java

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
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                handleException(e);
                closeRegistry();
            }
        }

        return sessionFactory;
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
```

## 4. Hibernate Mapping

### 4.1 Annotations

#### 4.1.1 Entity

The `@Entity` annotation marks the class as an entity. This annotation is applied to the entity class.

```java

@Entity
public class User {
    // ...
}
```

#### 4.1.2 Table

The `@Table` annotation specifies the table name associated with the entity. This annotation is applied to the entity
class.

```java

@Entity
@Table(name = "users")
public class User {
    // ...
}
```

#### 4.1.3 Id

The `@Id` annotation specifies the primary key of an entity. This annotation is applied to the primary key field or

#### 4.1.4 Column

#### 4.1.5 GeneratedValue

 CascadeType | Description                                                                         
-------------|-------------------------------------------------------------------------------------
 `ALL`       | Apply all operations (persist, remove, refresh, merge, detach) to the child entity. 
 `DETACH`    | Detach the child entity when the parent entity is detached.                         
 `MERGE`     | Merge the child entity when the parent entity is merged.                            
 `PERSIST`   | Persist the child entity when the parent entity is persisted.                       
 `REFRESH`   | Refresh the child entity when the parent entity is refreshed.                       
 `REMOVE`    | Remove the child entity when the parent entity is removed.                          

- session.merge(): If there is a persistent instance with the same identifier currently associated with the session,
  copy the state of the given object onto the persistent instance. If there is no persistent instance currently
  associated with the session, try to load it from the database, or create a new persistent instance. The persistent
  instance is returned. The given instance does not become associated with the session. This operation cascades to
  associated instances if the association is mapped with `cascade="merge"`. 

  Hibernate Methods (save, update, delete, etc.): En estas operaciones de Hibernate, se aplicará automáticamente la cascada configurada (CASCADE.ALL) en las relaciones.

  # Criteria API y Native Queries: En estas situaciones, la cascada (CASCADE) no se aplica automáticamente. Al ejecutar consultas nativas o utilizar Criteria API, debes manejar manualmente cualquier operación de cascada necesaria.