package org.example.hirehub.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.entity.User;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {

        User user = userRepository.findByEmail(email);

        return user;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return;
        }

        user.setDeleted(true);
        userRepository.save(user);
    }
    public User getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAnonymous = authentication.getPrincipal().equals("anonymousUser");

        if(isAnonymous) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
