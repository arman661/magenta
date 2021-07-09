package com.example.demo;

public class CannotBeCalculatedException extends RuntimeException {
    public CannotBeCalculatedException(String s) {
        super(s);
    }
}
