package com.example.server.dao;

import com.example.server.exception.ProjectException;
import com.example.server.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDao {
    User getUser(long id) throws ProjectException;

    User save(User user) throws ProjectException;

    Page<User> findAll(Pageable pageable) throws ProjectException;
}
