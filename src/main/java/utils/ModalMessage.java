package utils;

import com.google.gson.Gson;

public class ModalMessage extends Message{
    private String title;

    public ModalMessage(String status, String message, String title) {
        super(status, message);
        this.title = title;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}