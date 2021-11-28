package com.example.authorization.tokens;

public class Token {
    private String token;

    public Token(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
