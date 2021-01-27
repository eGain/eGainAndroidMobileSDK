package egain.com.egainpartnerdemo;

import android.os.AsyncTask;

import java.util.List;


public class ChatRequestTask extends AsyncTask<eGainChat, Integer, ChatResponse> {
    private ChatListener listener;

    public ChatRequestTask(ChatListener listener) {
        this.listener = listener;
    }

    @Override
    protected ChatResponse doInBackground(eGainChat... params) {

        eGainChat chat = params[0];
        if (chat.getSid() != null) {
            if (chat.getOperation().equals("endChat"))
            {
                return chat.endChat();
            }
            else if (chat.getOperation().equals("messages") && chat.getMessage() != null)
            {
                return chat.sendMessage(chat.getMessage());
            }
            else {
                return null;
            }
        }
        else
        {
            if (chat.getOperation().equals("initChat")) {
                return chat.initChat();
            }
            else if (chat.getOperation().equals("startChat")) {
                return chat.startChat(chat.getMessage());
            }
            else if (chat.getOperation().equals("availability")) {
                return chat.availiablity();
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(ChatResponse result) {
        super.onPostExecute(result);
        listener.afterReceivingeGainChatResponse(result);
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        listener.beforeSendingeGainChatRequest();
    }
}
