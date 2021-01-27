package egain.com.egainpartnerdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kraig Paulsen
 */
public class eGainGHResponseData {

    public String TAG = eGainGHResponseData.class.getSimpleName();

    private String sid, question, questionID, answer, answerID, type, format;

    public eGainGHResponseData(String sid, String question, String answer)
    {
        this.sid = sid;
        this.question = question;
        this.answer = answer;
    }

    public eGainGHResponseData(JSONObject jsonObject) throws JSONException {
        boolean start = true;
        if (jsonObject.has("startupQuestion")) {
           JSONArray uaq = jsonObject.getJSONArray("startupQuestion");
           if (uaq.length() > 0) {
               JSONObject objq = uaq.getJSONObject(0);
               JSONArray va = objq.getJSONArray("validAnswer");
               JSONObject obja = va.getJSONObject(0);
               this.question = objq.getString("title");
               this.questionID = objq.getString("id");
               this.type = objq.getString("type");
               this.format = objq.getString("format");
               this.answer = obja.getString("text");
               this.answerID = obja.getString("id");
           } else start = false;
        }
        if (!start && jsonObject.has("unansweredQuestion")) {
            JSONArray uaq = jsonObject.getJSONArray("unansweredQuestion");
            if (uaq.length() > 0) {
                JSONObject objq = uaq.getJSONObject(0);
                JSONArray va = objq.getJSONArray("validAnswer");
                JSONObject obja = va.getJSONObject(0);
                this.question = objq.getString("title");
                this.questionID = objq.getString("id");
                this.type = objq.getString("type");
                this.format = objq.getString("format");
                this.answer = obja.getString("text");
                this.answerID = obja.getString("id");
            }
        }

    }

    public String getSid() {
        return sid;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionId() {
        return questionID;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnswerId() {
        return answerID;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "eGainGHResponseData [TAG=" + TAG + ", sid=" + sid + ", question="
                + question + "answer=" +answer +"]";
    }
}
