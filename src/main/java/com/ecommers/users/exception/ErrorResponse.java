package com.ecommers.users.exception;

public record ErrorResponse(int status, String message, String timestamp) {}
