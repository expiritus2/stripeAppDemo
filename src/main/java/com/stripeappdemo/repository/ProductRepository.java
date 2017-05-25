package com.stripeappdemo.repository;


import com.stripeappdemo.models.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long>{

    Product findByName(String name);
}
