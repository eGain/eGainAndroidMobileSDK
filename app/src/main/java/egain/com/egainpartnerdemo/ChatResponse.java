package egain.com.egainpartnerdemo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class containing the details of the response received via
 * ChatResponse
 *
 * @author Kraig Paulsen
 *
 */
public class ChatResponse {

    // The response code, eg: 200, 201 etc.
    private int responseCode;
    // Flag if error occurred
    private boolean error;
    // The body of the response
    private String responseBody;

    private String Sid;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public eGainChatResponseData getChatResponseBody() throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        final eGainChatResponseData chatResponse = new eGainChatResponseData(jsonObject);
        return chatResponse;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String Sid)  {
        this.Sid = Sid;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
