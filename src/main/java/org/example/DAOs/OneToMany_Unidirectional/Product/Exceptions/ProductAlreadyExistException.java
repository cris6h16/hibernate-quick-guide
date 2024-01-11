package org.example.DAOs.OneToMany_Unidirectional.Product.Exceptions;

public class ProductAlreadyExistException extends Exception{
    public ProductAlreadyExistException() {
        super("Product already exist");
    }
}
