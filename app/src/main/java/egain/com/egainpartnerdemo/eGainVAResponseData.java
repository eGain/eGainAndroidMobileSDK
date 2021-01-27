package egain.com.egainpartnerdemo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kraig Paulsen
 */
public class eGainVAResponseData {

    public String TAG = eGainVAResponseData.class.getSimpleName();

    private String sid, text, url, emotion;

    public eGainVAResponseData(String sid, String message,String url, String emotion)
    {
        this.sid = sid;
        this.text = message;
        this.url = url;
        this.url = emotion;
    }

    public eGainVAResponseData(JSONObject jsonObject) throws JSONException {
        this.sid = jsonObject.getString("sid");
        this.text = jsonObject.getString("text");
        String rawURL = jsonObject.getString("url");
        this.url = rawURL;
        this.emotion = jsonObject.getString("emotion");
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

    @Override
    public String toString() {
        return "eGainVAResponseData [TAG=" + TAG + ", sid=" + sid + ", text="
                + text + ", url=" + url + ", emotion=" + emotion + "]";
    }
}
