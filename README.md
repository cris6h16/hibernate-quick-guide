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

 PROPERTY                        | DESCRIPTION                                                                                              
---------------------------------|----------------------------------------------------------------------------------------------------------
 `connection.url`                | The JDBC URL of the database.                                                                            
 `connection.driver_class`       | The JDBC driver class.                                                                                   
 `connection.username`           | The database username.                                                                                   
 `connection.password`           | The database password.                                                                                   
 `dialect`                       | The SQL dialect of the database.                                                                         
 `hibernate.hbm2ddl.auto`        | Automatically validates or exports schema DDL to the database when the SessionFactory is created.        
 `show_sql`                      | Prints all SQL statements to the console.                                                                
 `format_sql`                    | Pretty print the SQL statements.                                                                         
 `current_session_context_class` | The context of the current session. Default is `thread`, which is the recommended option, see java docs. 
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
    - `org.example.<MyCustomSessionContext.java>`: The `Session` is managed by a custom implementation of the `org.hibernate.context.spi.CurrentSessionContext`
      interface, you can implement your own `CurrentSessionContext` and use it with this option.



- `hibernate.hbm2ddl.import_files`:
  - `import.sql`: The default file name for import script.
  - `<filename1>.sql, <filename2>.sql, etc`: Execute the multiple SQL scripts.(in order)
  
### Access to environment variables

You can access to environment variables in the `hibernate.cfg.xml` file using the `${ENV_VAR}` syntax.

