package com.example.server.service.impl;

import com.example.server.dao.UserDao;
import com.example.server.exception.ProjectException;
import com.example.server.model.Balance;
import com.example.server.model.User;
import com.example.server.scheduled.CounterGet;
import com.example.server.scheduled.CounterPost;
import com.example.server.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(rollbackFor = ProjectException.class, isolation = Isolation.READ_COMMITTED)
public class BalanceServiceImpl implements BalanceService {
    private final UserDao userDao;

    @Autowired
    public BalanceServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @CounterGet
    @Cacheable(value = "balance", key = "#id")
    public Optional<Long> getBalance(Long id) throws ProjectException {
        User user = userDao.getUser(id);
        if (isUserNull(user)) {
            throw new ProjectException("User not found");
        }
        Balance balance = user.getBalance();
        return Optional.of(balance.getValue());
    }

    @Override
    @CounterPost
    @CachePut(value = "balance", key = "#id")
    public Optional<Long> changeBalance(Long id, Long amount) throws ProjectException {
        User user = userDao.getUser(id);
        if (isUserNull(user)) {
            throw new ProjectException("User not found");
        }
        Balance balance = user.getBalance();
        Long result = balance.getValue() + amount;
        balance.setValue(result);
        return Optional.of(balance.getValue());
    }

    private boolean isUserNull(User user) {
        return user == null;
    }

}
