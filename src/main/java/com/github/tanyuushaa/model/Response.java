package com.github.tanyuushaa.model;

import java.util.List;

public class Response<T> {

    private List<String> errorMessage;
    private T object;

    public Response(T object, List<String> errorMessage) {
        this.object = object;
        this.errorMessage = errorMessage;
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isOkay() {
        return errorMessage.size() == 0;
    }
}
