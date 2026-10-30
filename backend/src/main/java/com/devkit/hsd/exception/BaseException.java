package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public BaseException(HttpStatus httpStatus, String message, ErrorCode errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

}
