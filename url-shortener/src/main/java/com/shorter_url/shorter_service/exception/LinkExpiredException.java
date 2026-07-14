package com.shorter_url.shorter_service.exception;

public class LinkExpiredException extends RuntimeException {
    public LinkExpiredException(String message){
        super(message);
    }
}
