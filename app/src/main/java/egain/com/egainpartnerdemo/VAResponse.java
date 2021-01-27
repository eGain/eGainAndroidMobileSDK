package egain.com.egainpartnerdemo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class containing the details of the response received via
 * VAResponse
 *
 * @author Kraig Paulsen
 *
 */
public class VAResponse {

    // The response code, eg: 200, 201 etc.
    private int responseCode;
    // Flag if error occurred
    private boolean error;
    // The body of the response
    private String responseBody;
    private String domain;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public eGainVAResponseData getVAResponseBody() throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        final eGainVAResponseData botResponse = new eGainVAResponseData(jsonObject);
        return botResponse;
    }

    public String getSid() throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        final eGainVAResponseData botResponse = new eGainVAResponseData(jsonObject);
        return botResponse.getSid();
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBaseURL() {
        return domain;
    }

    public void setResponseBaseURL(String domain) {this.domain = domain; }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
