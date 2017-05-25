package com.stripeappdemo.repository;


import com.stripeappdemo.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long>{
}
