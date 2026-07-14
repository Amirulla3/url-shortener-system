package com.shorter_url.shorter_service.exception;

public class LinkNotFoundException extends RuntimeException{
    public LinkNotFoundException(String message){
        super(message);
    }
}
