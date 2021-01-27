package egain.com.egainpartnerdemo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class containing the details of the request to be sent via
 * ChatRequest
 *
 * @author Kraig Paulsen
 *
 */
public class ChatRequest {

    public enum Type {
        GET, POST
    };

    // The type of request, GET or POST
    private Type type;

    // The URL of the request
    private String url;

    // The content type in case of a POST request
    private String contentType;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

