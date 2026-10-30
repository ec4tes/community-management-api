package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException{
    public BadRequestException( ErrorCode errorCode ,String message) {
        super(HttpStatus.BAD_REQUEST, message,errorCode);
    }
}
