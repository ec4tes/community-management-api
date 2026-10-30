package com.devkit.hsd.dto.response;

public record RestResponse<T> (

        boolean success,
        String message,
        T data

) {

    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(true, message, data);
    }


    public static <T> RestResponse<T> success(String message) {
        return new RestResponse<>(true, message, null);
    }

}
