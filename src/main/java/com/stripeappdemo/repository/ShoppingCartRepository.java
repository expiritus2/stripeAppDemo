package com.stripeappdemo.repository;


import com.stripeappdemo.models.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long>{
}
