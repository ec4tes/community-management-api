package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException{

    public ConflictException(ErrorCode errorCode,String message) {
        super(HttpStatus.CONFLICT, message,errorCode);
    }
}
