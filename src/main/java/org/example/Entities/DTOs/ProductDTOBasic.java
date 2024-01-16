package org.example.Entities.DTOs;

import java.math.BigDecimal;

public class ProductDTOBasic {

        private String name;
        private BigDecimal price;

    //  private String description;
    //  private CategoryEntity category;
        private ProductDTOBasic() {
        }

        public ProductDTOBasic(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }

    @Override
    public String toString() {
        return "ProductDTOBasic{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
