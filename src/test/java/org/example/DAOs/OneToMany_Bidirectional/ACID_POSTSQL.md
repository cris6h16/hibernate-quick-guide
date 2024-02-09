# ACID GUARANTEES POSTGRESQL
ACID properties are a set of principles that guarantee the safe and correct processing of transactions in a database system.

## 1. Atomicity
principle that ensures all actions within a transaction are either fully completed or not executed at all.
- Transaction is an indivisible unit of work.
- either all or none of the operations are performed.  

it prevents partial updates that can lead to data inconsistencies.

### 1.1 In PostgreSQL
**Atomicity** is achieved by using a transaction log, also known as a write-ahead log (WAL).  
- All changes during a transaction are written to the WAL before being applied to the actual DB.
- If a transaction fails or is interrupted, PostgreSQL can use the WAL to roll back the changes and restore the database to its previous state.
- Upon successful completion of a transaction, PostgreSQL marks the transaction as committed in the WAL, ensuring the changes are permanent.

## 2. Consistency
principle that ensures that only valid data will be written to the database.
if a transaction violates any constraints, must be aborted and the DB must be restored to its previous state.

PostgreSQL enforces consistency by utilizing a variety of mechanisms, such as primary keys, unique constraints, foreign keys, and check constraints. 

Additionally, PostgreSQL employs a multi-version concurrency control (MVCC) system that allows multiple transactions to run concurrently without causing conflicts, further guaranteeing consistency.

### 2.1 Constraints in PostgreSQL
- **Primary key** constraint: ensures that a column or a group of columns have a unique identity which helps to find a particular record in a database more easily and quickly, the PK cannot contain null values.  
Example: 
```sql
CREATE TABLE users (
    id int,
    name varchar(255)
);
``` 
```sql
ALTER TABLE users
ADD PRIMARY KEY (id);
```
We ensure that the id column is unique and not null.

- **Foreign key** constraint: field or collection of fields in one table that refers to the primary key in another table. this constraint is used to prevent actions that would destroy links between tables.
Example:
```sql
CREATE TABLE users (
    id int,
    name varchar(255),
    address_id int
);
```
```sql
ALTER TABLE users 
ADD FOREIGN KEY (address_id)
REFERENCES addresses(id);
```

- **Not null** constraint: ensures that a column cannot have a null value.
Example:
```sql
CREATE TABLE users (
    id int,
    name varchar(255) NOT NULL
);
```

- **Unique** constraint: ensures that all values in a column are different, you can have
multiple unique constraints per table(can be a pair or a group of columns).
Example:
```sql
ALTER TABLE users 
ADD UNIQUE (email);
```

- **Check** constraint: ensures that all values in a column satisfy certain conditions.
Example:
```sql
ALTER TABLE users
ADD CHECK (age >= 18);
```

## 3. Isolation
Ensures the concurrent execution of transactions does not affect each other's outcome.  
In simpler terms, the results of one transaction should not be visible to another transaction until the first transaction is committed. This property is essential to prevent data corruption and other issues that can arise due to simultaneous transactions.

### 3.1 In PostgreSQL

# FAQ
1. **What is the difference between UNIQUE and PRIMARY KEY constraints?**  

PK is a combination of a NOT NULL and UNIQUE constraint. a UNIQUE constraint can be null, but unlike a PK, it allows for null values.  
Extra: If a field is UNIQUE the field can have a lot of null values (null != null)

2. **Can a table have multiple primary keys?**  

No, a table can have only one primary key. However, a primary key can consist of multiple columns (fields).
  
3. **Can FOREIGN KEY be null?**  

Yes, a foreign key can be null, if it's null it means that a record doesn't necessary relate to any other record in referenced table. 

4. **Can I remove a constraint from a table?**  

Yes, you can remove a constraint from a table using:
```sql
ALTER TABLE table_name
DROP CONSTRAINT/CHECK constraint_name;
```

5. **What is the role of constraints in SQL?**  

This helps ensure accuracy, reliability, and integrity of the data.  
Constraints are an essential aspect of maintaining the reliability of data within a database. By mastering PRIMARY KEY, FOREIGN KEY, NOT NULL, UNIQUE, and CHECK constraints, you will be well-equipped to build and manage robust, reliable databases.