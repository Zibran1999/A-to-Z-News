package com.atoz.atoznewsadmin.models;

public class MessageModel {
    String message,error;

    public MessageModel(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
