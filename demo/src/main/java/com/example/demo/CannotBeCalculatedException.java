package com.example.demo;

public class CannotBeCalculatedException extends Exception {
    public CannotBeCalculatedException(String s) {
        super(s);
    }

    public CannotBeCalculatedException() {

    }
}
