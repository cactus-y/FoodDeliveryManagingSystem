package com.example.food_delivery_managing_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(Long id) {
        super("Menu not found with id " + id);
    }
}