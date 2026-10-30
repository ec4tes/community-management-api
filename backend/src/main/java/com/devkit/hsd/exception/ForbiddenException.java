package com.devkit.hsd.exception;

import com.devkit.hsd.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(ErrorCode errorCode, String message) {
        super(HttpStatus.FORBIDDEN,message, errorCode);
    }
}
