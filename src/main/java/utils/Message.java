package utils;


public abstract class Message {
     protected String status;
     protected String message;

     public Message(String status, String message) {
         this.status = status;
         this.message = message;
     }

     public String getStatus() {
         return status;
     }

     public String getMessage() {
         return message;
     }

     public abstract String toJson();
}
