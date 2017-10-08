package com.linsh.paa.model.result;

/**
 * Created by Senh Linsh on 17/5/4.
 */

public class Result {

    private boolean success;
    private String message;

    public Result() {
        this(true, null);
    }

    public Result(String message) {
        this(false, message);
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
