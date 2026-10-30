package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException{

    public NotFoundException(ErrorCode errorCode, String message) {
        super(HttpStatus.NOT_FOUND,message,errorCode);
    }
}
