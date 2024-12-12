package com.mudcode.springboot.message;

public final class ServiceMessage<T> {

    public static final int API_OK = 0;

    public static final int API_ERROR = 1;

    private int code;

    private String message;

    private T data;

    public ServiceMessage() {
        this.code = API_OK;
        this.message = "OK";
        this.data = null;
    }

    public static ServiceMessage<?> success() {
        return new ServiceMessage<>();
    }

    public static <T> ServiceMessage<T> success(T data) {
        ServiceMessage<T> service = new ServiceMessage<>();
        service.setData(data);
        return service;
    }

    public static ServiceMessage<?> error(String message) {
        ServiceMessage<?> service = new ServiceMessage<>();
        service.setCode(API_ERROR);
        service.setMessage(message);
        return service;
    }

    public static ServiceMessage<?> error(int errorCode, String message) {
        ServiceMessage<?> service = new ServiceMessage<>();
        service.setCode(errorCode);
        service.setMessage(message);
        return service;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
