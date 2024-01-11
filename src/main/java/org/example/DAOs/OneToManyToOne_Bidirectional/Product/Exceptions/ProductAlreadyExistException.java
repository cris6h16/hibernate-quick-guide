package org.example.DAOs.OneToManyToOne_Bidirectional.Product.Exceptions;

public class ProductAlreadyExistException extends Exception{
    public ProductAlreadyExistException() {
        super("Product already exist");
    }
}
