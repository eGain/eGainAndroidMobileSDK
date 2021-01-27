package egain.com.egainpartnerdemo;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class eGainGH {
    private String protocal = "http";
    private String domainhost = "v2w75.egeng.info";
    //private String domainhost = "solvev17.eng.na";
    private String anonymousAuth = "/system/ws/v15/ss/portal/{portalId}/authentication/anonymous";
    private String ghStart = "/system/ws/v11/gh/search/start?portalId=201800000001000&casebaseId=201801000000002&usertype=customer";
    private String ghSearch = "/system/ws/v11/gh/search?portalId=201800000001000&casebaseId=201801000000002&usertype=customer";
    private String token = null;
    private String portalID = "201800000001000";
    private String userType = "customer";
    private String casebaseID = "201801000000002";
    private String question = "";
    private String sid = null;

    public eGainGH() {

    }

    public GHResponse initGH(String input) {
        int responseCode = 0;
        GHResponse response = null;
        question = anonymousAuth.replace("{portalId}", portalID);
        response = authenticate(input);
        if (sid == null)
        {
            sid = response.getSid();
        }
        return response;
    }

    public GHResponse startGH(String input) {
        question = ghStart;
        return connectREST("POST");
    }

    public GHResponse searchGH(String input) {
        question = ghSearch+"&"+input;
        return connectREST("POST");
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String input) {
        sid = input;
    }

    public String getQuestion() {
        return (question);
    }
    public void setQuestion(String input) {
            question = input;
    }

    private GHResponse connectREST(String method) {
        int responseCode = 0;
        GHResponse response = new GHResponse();

        try {
            URL url = new URL(protocal+"://"+domainhost+question);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Accept-Language", "en-US");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-type", "application/json");
            if (sid != null) urlConnection.setRequestProperty("X-egain-session",sid);
            responseCode = urlConnection.getResponseCode();
            // Process Request response
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            if (urlConnection.getHeaderField("X-egain-session") != null) response.setSid(urlConnection.getHeaderField("X-egain-session"));
            response.setResponseBody(result.toString());
            response.setResponseCode(responseCode);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error processing request!", e);
        }
        return response;
    }

    private GHResponse authenticate(String input) {
        int responseCode = 0;
        GHResponse response = new GHResponse();

        try {
            URL url = new URL(protocal+"://"+domainhost+question);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Language", "en-US");
            urlConnection.setRequestProperty("Content-type", "application/json");
            responseCode = urlConnection.getResponseCode();
            response.setSid(urlConnection.getHeaderField("X-egain-session"));
            response.setResponseBody("{'sid' : '"+urlConnection.getHeaderField("X-egain-session")+"', 'text' : '"+input+"'}");
            response.setResponseCode(responseCode);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error processing request!", e);
        }
        return response;
    }
}
