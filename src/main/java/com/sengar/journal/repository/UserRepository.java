package com.sengar.journal.repository;

import com.sengar.journal.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId>{

    public Optional<User> findByUsername(String username);
}
