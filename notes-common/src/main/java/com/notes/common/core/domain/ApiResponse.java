package com.notes.common.core.domain;

import java.io.Serializable;

/**
 * 通用API响应类
 *
 * @author notes
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null, true);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, true);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, true);
    }

    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "error", null, false);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, false);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    public static <T> ApiResponse<T> of(int code, String message, T data, boolean success) {
        return new ApiResponse<>(code, message, data, success);
    }

    // Getter and Setter methods
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", success=" + success +
                '}';
    }
}