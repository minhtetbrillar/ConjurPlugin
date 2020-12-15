package com.cyberark.common;

public class HttpResponse {
    public String body;
    public int statusCode ;

    public HttpResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }
}
