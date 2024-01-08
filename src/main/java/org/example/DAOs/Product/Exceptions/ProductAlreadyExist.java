package org.example.DAOs.Product.Exceptions;

public class ProductAlreadyExist extends RuntimeException{
    public ProductAlreadyExist() {
        super("Product already exist");
    }
}
