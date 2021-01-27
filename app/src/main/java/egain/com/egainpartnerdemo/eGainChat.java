package egain.com.egainpartnerdemo;

import android.os.Build;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONArray;
import org.json.JSONObject;

public class eGainChat {
    private String protocal = "http";
    private String domainhost = "v2w75.egeng.info";
    String wsdomainhost = "10.10.1.240:4040";
    private String chatinit = "/system/egain/chat/entrypoint/initialize/{entryId}";
    private String chatstart = "/system/egain/ws/v1/chat/entrypoint/start";
    private String chatavailability = "/system/egain/chat/entrypoint/agentAvailability/{entryId}";
    private String chatsend = "/system/egain/ws/v1/chat/entrypoint/sendMessage";
    private String chatend = "/system/egain/ws/v1/chat/entrypoint/end";
    private String token = null;
    private String entryPointID = "1002";
    private String params = null;
    private String operation = null;
    private String sid = null;

    public eGainChat() {

    }

    public ChatResponse initChat() {
        params = chatinit.replace("{entryId}", entryPointID);
        return connectREST(null,"GET", null);
    }

    public ChatResponse startChat(String input) {
        ChatResponse response = null;

        params = chatstart;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("entryPoint", entryPointID);
            jsonObject.put("languageCode", "en");
            jsonObject.put("countryCode", "US");
            JSONArray loginparams = new JSONArray();
            JSONObject fullnamekeyvalue = new JSONObject();
            fullnamekeyvalue.put("paramName","full_name");
            fullnamekeyvalue.put("paramValue","Jon Snow");
            loginparams.put(fullnamekeyvalue);
            JSONObject emailkeyvalue = new JSONObject();
            emailkeyvalue.put("paramName","email_address");
            emailkeyvalue.put("paramValue","test1@test.com");
            emailkeyvalue.put("isPrimary",true);
            loginparams.put(emailkeyvalue);
            JSONObject phonekeyvalue = new JSONObject();
            phonekeyvalue.put("paramName","phone_number");
            phonekeyvalue.put("paramValue","9960667401");
            loginparams.put(phonekeyvalue);
            JSONObject subjectkeyvalue = new JSONObject();
            subjectkeyvalue.put("paramName","subject");
            subjectkeyvalue.put("paramValue",input);
            loginparams.put(subjectkeyvalue);
            JSONObject callback = new JSONObject();
            callback.put("messageFormat","json");
            callback.put("callbackURL", protocal+"://"+wsdomainhost+"/");
            JSONObject clientInfo = new JSONObject();
            clientInfo.put("clientCallback",callback);
            jsonObject.put("loginParams", loginparams);
            jsonObject.put("clientInfo", clientInfo);
        } catch (org.json.JSONException e) {

        }
        response = connectREST(null, "POST", jsonObject);
        return response;
    }

    public ChatResponse availiablity() {
        params = chatavailability.replace("{entryId}", entryPointID);
        return connectREST(null,"GET", null);
    }

    public ChatResponse sendMessage(String input) {
        ChatResponse response = null;

        params = chatsend;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", "Jon");
            jsonObject.put("body", input);
            jsonObject.put("messageType", "chat");
        } catch (org.json.JSONException e) {

        }
        response = connectREST(sid, "POST", jsonObject);
        return response;
    }

    public ChatResponse endChat() {
        params = chatend;
        return connectREST(sid, "POST", null);
    }

    public String getMessage() {
        return params;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String input) {
        operation = input;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String input) {
        sid = input;
    }

    public void setMessage(String input) {
        params = input;
    }

    private ChatResponse connectREST(String sid, String method, JSONObject payload) {
        int responseCode = 0;
        ChatResponse response = new ChatResponse();

        try {
            URL url = new URL(protocal + "://" + domainhost + params);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept-Language", "en-US");
            if (method.equals("POST")) {
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                if (sid != null) urlConnection.setRequestProperty("X-egain-chat-session",sid);
                if (payload != null) {
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                    wr.write(payload.toString());
                    wr.flush();
                }
            }
            else {
                urlConnection.setRequestProperty("Accept", "application/json");
            }
            urlConnection.setRequestMethod(method);
            responseCode = urlConnection.getResponseCode();

            // Process Request response
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            if (urlConnection.getHeaderField("X-egain-chat-session") != null) response.setSid(urlConnection.getHeaderField("X-egain-chat-session"));
            if (!result.toString().equals("")) response.setResponseBody(result.toString());
            else response.setResponseBody("{}");
            response.setResponseCode(responseCode);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error processing request!", e);
        }
        return response;
    }
}
