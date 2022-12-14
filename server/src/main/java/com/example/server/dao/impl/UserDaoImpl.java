package com.example.server.dao.impl;

import com.example.server.dao.repository.UserRepository;
import com.example.server.exception.ProjectException;
import com.example.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements com.example.server.dao.UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(long id) throws ProjectException {
        try {
            return userRepository.findById(id);
        } catch (RuntimeException e) {
            throw new ProjectException("Internal Server Error");
        }
    }

    @Override
    public User save(User user) throws ProjectException {
        try {
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new ProjectException("Internal Server Error");
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) throws ProjectException {
        try {
            return userRepository.findAllByPage(pageable);
        } catch (RuntimeException e) {
            throw new ProjectException("Internal Server Error");
        }
    }


}
