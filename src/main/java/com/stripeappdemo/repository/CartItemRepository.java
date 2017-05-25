package com.stripeappdemo.repository;


import com.stripeappdemo.models.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepository extends CrudRepository<CartItem, Long>{
}
