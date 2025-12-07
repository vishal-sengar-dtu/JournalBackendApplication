package com.sengar.journal.service;

import com.sengar.journal.entity.User;
import com.sengar.journal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public void saveEntry(User user) { userRepository.save(user); }
    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }
}
