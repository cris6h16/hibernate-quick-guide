package org.example.DAOs.OneToManyToOne_Bidirectional.Category;

import org.example.Entities.OneToManyToOne_Bidirectional.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    /**
     * Deletes a CategoryEntity from the database.
     *
     * @param id the id of the CategoryEntity to delete
     * @return true if the deletion was successful, false otherwise
     */
    boolean deleteById(Long id);

    /**
     *
     * @param category if update is manually(Hibernate Criteria) must be Eagerly
     * @return true if category was updated
     */
    boolean merge(CategoryEntity category);

    /**
     * Saves a CategoryEntity object to the database.
     * if was saved successfully
     * the category will have id assigned
     *
     * @param category the CategoryEntity object to be saved
     */
    void persist(CategoryEntity category);

    /**
     * <b>For testing purposes.</b>
     * First, delete all rows from CategoryEntity table.
     * Second, retrieve the rows from the empty table.
     * Third, rollback the deletion.
     *
     * @return List<CategoryEntity> with all the rows from CategoryEntity table.
     */
    List<CategoryEntity> listAllWithEmptyRows();

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given name exists
     */
    Optional<CategoryEntity> findByName(String name);

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an optional containing the category if it exists, or an empty optional if no category with the given ID exists
     */
    Optional<CategoryEntity> findById(Long id);

    /**
     * Returns a list of all categories in the database.
     *
     * @return a list of all categories in the database
     */
    List<CategoryEntity> listAll();

    /**
     * Returns an Optional of a CategoryEntity with the given id, including its associated products(Collection Initialized).
     *
     * @param id the id of the CategoryEntity to retrieve
     * @return an Optional of a CategoryEntity with the given id, including its associated products
     */
    Optional<CategoryEntity> getByIdEager(Long id);

    // ---------------------- Pagination ------------------------\\
    int count();
    int countPages(int resultsPerPage);

    List<CategoryEntity> listPagination(int pageNum, int resultsPerPage);


}
