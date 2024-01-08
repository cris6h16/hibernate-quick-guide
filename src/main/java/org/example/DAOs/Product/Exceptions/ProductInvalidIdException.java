package org.example.DAOs.Product.Exceptions;

public class ProductInvalidIdException extends Exception {
    public ProductInvalidIdException() {
        super("Invalid product id");
    }
}
