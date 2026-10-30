package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, message,errorCode);
    }
}
