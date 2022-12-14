package com.example.server.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ProjectException extends Exception implements Serializable {
    private final String message;


    public ProjectException(String message) {
        this.message = message;
    }

    public ProjectException() {
        message = "";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
