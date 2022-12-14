package com.example.server.service;

import com.example.server.exception.ProjectException;

import java.util.Optional;


public interface BalanceService {

    Optional<Long> getBalance(Long id) throws ProjectException;

    Optional<Long> changeBalance(Long id, Long amount) throws ProjectException;
}
