package org.bpcrs.object;

import java.io.Serializable;

public class ResultChaincode implements Serializable {
    private int code;
    private String message;
    private Object data;

    public ResultChaincode() {
        this.code = 200;
        this.data = "";
    }

    public ResultChaincode(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
