package egain.com.egainpartnerdemo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kraig Paulsen
 */
public class eGainChatResponseData {

    public String TAG = eGainChatResponseData.class.getSimpleName();

    private String sid = null, text = null, url = null, emotion = null;
    private boolean available = false;

    public eGainChatResponseData(String sid, String message, String url, String emotion)
    {
        this.sid = sid;
        this.text = message;
        this.url = url;
        this.url = emotion;
    }

    public eGainChatResponseData(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("available")) this.available = jsonObject.getBoolean("available");
        if (jsonObject.has("sid")) this.sid = jsonObject.getString("sid");
        if (jsonObject.has("text")) this.text = jsonObject.getString("text");
        if (jsonObject.has ("url")) this.url = jsonObject.getString("url");
        if (jsonObject.has ("emotion")) this.emotion = jsonObject.getString("emotion");
    }

    public String getSid() {
        return sid;
    }

    public String getText() {
        return text;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "eGainChatResponseData [TAG=" + TAG + ", sid=" + sid + ", text="
                + text + "url=" +url + "emotion=" + emotion +"]";
    }
}
