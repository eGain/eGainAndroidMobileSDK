package egain.com.egainpartnerdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class eGainVA {
    private String protocal = "https";
    private String domainhost = "eg5012qa3l.egain.cloud";
    private String askva = "/egain/va/v1/bot/Kate?type=json";
    private String token = "TT29136831";
    private String question = askva;
    private String sid = null;

    public eGainVA() {

    }

    public String getSid() {
        return sid;
    }

    public void setSid(String input) {
        sid = input;
    }

    public VAResponse solve() {
        return connectREST("GET");
    }

    public void setQuestion(String input) {
        if (sid != null) {
            question = askva+"&sid="+sid+"&ask="+input;
        }
        else question = askva;
    }

    private VAResponse connectREST(String method) {
        int responseCode = 0;
        VAResponse response = new VAResponse();

        try {
            URL url = new URL(protocal + "://" + domainhost + question);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("X-tenant-token", token);
            responseCode = urlConnection.getResponseCode();

            // Process Request response
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            response.setResponseBaseURL(protocal+"://"+domainhost);
            response.setResponseBody(result.toString());
            response.setResponseCode(responseCode);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error processing request!", e);
        }
        return response;
    }
}
