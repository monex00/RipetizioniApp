package utils;

import com.google.gson.Gson;

public class RedirectMessage extends Message {
    public String redirectPath;

    public RedirectMessage(String status, String message, String redirectPath) {
        super(status, message);
        this.redirectPath = redirectPath;
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
