package egain.com.egainpartnerdemo;

import android.content.Context;

import java.util.List;

public class Message {
    String message = null;
    String sender = null;
    String imageURL = null;

    //User sender;
    long createdAt;
    int viewtype;

    public Message () {
    }

    public Message (String input, int type) {
        message = input;
        viewtype = type;
    }

    public String getMessage() {
       return(message);
    }
    public void setMessage(String input) { message = input; }

    public int getType() {
       return(viewtype);
    }
    public void setType(int input) { viewtype = input; }

    public String getSender() {
        return(sender);
    }
    public void setSender(String input) { sender = input; }

    public String getImageURL() {
        return(imageURL);
    }
    public void setImageURL(String input) { imageURL = input; }
}
