package com.example.server;

import com.example.server.dao.UserDao;
import com.example.server.exception.ProjectException;
import com.example.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class InitUsers implements CommandLineRunner {

    private final UserDao userDao;

    @Autowired
    public InitUsers(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void run(String... args) throws ProjectException {
        if (!userDao.findAll(PageRequest.of(0, 2)).isEmpty()) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            User user = new User();
            userDao.save(user);
        }

    }
}
