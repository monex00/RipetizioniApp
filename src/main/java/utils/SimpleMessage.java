package utils;

import com.google.gson.Gson;

public class SimpleMessage extends Message {

    public SimpleMessage(String status, String message) {
        super(status, message);
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}
