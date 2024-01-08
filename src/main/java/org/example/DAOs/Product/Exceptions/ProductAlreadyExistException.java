package org.example.DAOs.Product.Exceptions;

public class ProductAlreadyExistException extends Exception{
    public ProductAlreadyExistException() {
        super("Product already exist");
    }
}
