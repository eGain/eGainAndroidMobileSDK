package egain.com.egainpartnerdemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    // View to handle the main chat thread communication
    private RecyclerView mMessageRecycler;
    // Adapter to handle the Message List for the View
    private MessageListAdapter mMessageAdapter;
    // Message list to capture all messages transfered for that chat thread
    private List<Message> messageList = new ArrayList<Message>();
    // Message identifier that message was sent from end Client
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    // Message identifier that message was received from Server
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    // Single Message object
    private Message message = null;
    // Async Task handlers to process incoming Client input Request
    private VARequestTask va = null;
    private GHRequestTask gh = null;
    private ChatRequestTask ch = null;
    // Misc control and UI objects
    private String channelstate = "va";
    private String vachatsubject = null;
    private String agentImg = "";
    private String agentName = "";

    //eGain specific variables
    private boolean chatavailable = false;
    private boolean chatinit = false;
    private boolean chatstart = false;
    private boolean chatend = false;
    private String vaSid = null;
    private String ghSid = null;
    private String chatSid = null;
    private String ghprevquestionID = null;
    private String ghprevanswerID = null;
    private String ghprevtype = null;
    private String ghprevformat = null;

    // eGain specific WebSocket objects to handle receiving Chat Call back messages from server
    private WebSocketClient mWebSocketClient;
    private boolean newIncoming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up Android UI objects
        setContentView(R.layout.test);
        Intent intent = getIntent();
        mMessageRecycler = (RecyclerView) findViewById(R.id.recycler_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
        final EditText msgInputText = (EditText) findViewById(R.id.edittext_chatbox);
        Button msgSendButton = (Button) findViewById(R.id.button_chatbox_send);

        // Set up eGain Chat Handler thread to handle processing the WebSocket incoming mesages and
        // the User input messages
        Handler chathandler = new Handler();

        //eGain Specific call to initialize Virtual Assistant
        processVA(chatSid, null);

        // Process Send Button request
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = msgInputText.getText().toString();
                if (!TextUtils.isEmpty(input)) {

                    //Process input message from Client user input
                    message = new Message(input, VIEW_TYPE_MESSAGE_SENT);
                    messageList.add(message);
                    int newMsgPosition = messageList.size() - 1;
                    // Notify recycler view insert one new data.
                    mMessageAdapter.notifyItemInserted(newMsgPosition);
                    // Scroll RecyclerView to the last message.
                    mMessageRecycler.scrollToPosition(newMsgPosition);
                    // Empty the input edit text box.
                    msgInputText.setText("");

                    // Specific business logic for selection and startup for specific
                    // channels VA, GH, Chat
                    if (input.equals("guide me on getting a mortgage")) {
                        ghSid = null;
                        chatSid = null;
                        chatavailable = false;
                        chatinit = false;
                        chatstart = false;
                        chatend = false;
                        ghprevquestionID = null;
                        ghprevanswerID = null;
                        ghprevtype = null;
                        ghprevformat = null;
                        channelstate = "gh";
                    } else if (input.equals("chat with agent")) {
                        ghSid = null;
                        chatSid = null;
                        ghprevquestionID = null;
                        ghprevanswerID = null;
                        ghprevtype = null;
                        ghprevformat = null;
                        channelstate = "chat";
                        if (chatSid == null) chkChatAvailability();

                    } else if (input.equals("chat with Kate")) {
                        ghSid = null;
                        chatSid = null;
                        chatavailable = false;
                        chatinit = false;
                        chatstart = false;
                        chatend = false;
                        ghprevquestionID = null;
                        ghprevanswerID = null;
                        ghprevtype = null;
                        ghprevformat = null;
                        channelstate = "va";
                        vachatsubject = null;
                        input = "Hello Kate";
                    }

                    // Process User Input request passing it onto specific channel
                    if (channelstate.equals("gh")) {
                        processGuidedHelp(ghSid, input);
                    }
                    else if (channelstate.equals("chat")) {
                        // Show user input connecting to Agent
                        // * Could hide and handle after availability check
                        if (chatSid == null){
                            //result text put in message list and present to UI
                            message = new Message("Connecting to next available Agent", VIEW_TYPE_MESSAGE_RECEIVED);
                            message.setImageURL(agentImg);
                            message.setSender(agentName);
                            messageList.add(message);
                            newMsgPosition = messageList.size() - 1;
                            // Notify recycler view insert one new data.
                            mMessageAdapter.notifyItemInserted(newMsgPosition);
                            // Scroll RecyclerView to the last message.
                            mMessageRecycler.scrollToPosition(newMsgPosition);
                        }
                        else {
                            processClientChat(input);
                        }
                    } else {
                        if (input.equals("what is my loan status") || input.equals("88877")) vachatsubject += input+" ";
                        processVA(vaSid, input);
                    }
                }
            }
        });
        // Handle the ongoing check for chat flow of possible incoming messages
        processChatFlow(chathandler);
    }

    // Thread to Handle processing Chat flow and state
    private void processChatFlow(Handler chathandler) {

        class MyRunnable implements Runnable {
            private Handler handler;

            public MyRunnable(Handler handler) {
                this.handler = handler;
            }
            @Override
            public void run() {
                this.handler.postDelayed(this, 500);
                if (channelstate.equals("chat")){
                    ChatResponse response = null;
                    if (chatavailable == true && chatSid == null) {
                        resetChatAvailable();
                        initializeChat();
                    }
                    else if (chatinit == true && chatSid == null) {
                        resetChatInit();
                        if (vachatsubject != null) startChat(vachatsubject);
                        else startChat("");
                    }
                    else if (chatstart == true && newIncoming) {
                        processServerChat();
                        newIncoming = false;
                    }
                    else if (chatend == true) {
                        resetChatStart();
                        chatend = false;
                        processVA(vaSid, "I am back Kate");
                    }
                }
            }
        }
        chathandler.post(new MyRunnable(chathandler));
    }

    // Handle passing the incoming Chat onMessage WebSocket call to the UI
    private void processServerChat() {
        // Called to present WebSocket Chat incoming messages from Agent
        int newMsgPosition = messageList.size() - 1;
        // Notify recycler view insert one new data.
        mMessageAdapter.notifyItemInserted(newMsgPosition);
        // Scroll RecyclerView to the last message.
        mMessageRecycler.scrollToPosition(newMsgPosition);
    }

    // Handle passing the incoming Chat User request to be processed as a Async Task and then process the
    // server response
    private void processClientChat (String input){
        eGainChat chat =new eGainChat();

        ch =new ChatRequestTask(new ChatListener() {
            @Override
            public void beforeSendingeGainChatRequest () {

            }

            @Override
            public void afterReceivingeGainChatResponse (ChatResponse response){
                //function to handle the response from the GH
                //response and pass onto the UI
                processChatResponse(response);
            }
        });
        //Calls to handle receiving server chat messages to user
        if (input.equals("end chat")) {
            chat.setOperation("endChat");
            chatend = true;
        }
        else chat.setOperation("messages");
        chat.setMessage(input);
        chat.setSid(chatSid);
        ch.execute(chat);
    }

    // Handle passing the incoming VA User request to be processed as a Async Task and then process the
    // server response
    private void processVA(String sId, String input)
    {
        eGainVA virtualassistant = new eGainVA();
        va = new VARequestTask(new VAListener() {
            @Override
            public void beforeSendingeGainVARequest() {

            }

            @Override
            public void afterReceivingeGainVAResponse(VAResponse response) {
                //function to handle the response from the Virtual Assistant
                //response and pass onto the UI
                processVAResponse(response);
            }
        });
        //Calls to handle user input to Virtual  and
        // receive Virtual Assistant response
        virtualassistant.setSid(vaSid);
        virtualassistant.setQuestion(input);
        va.execute(virtualassistant);
    }

    // Handle passing the incoming Guided Help User request to be processed as a Async Task and then process the
    // server response
    private void processGuidedHelp(String sId, String input)
    {
        eGainGH guidedhelp = new eGainGH();
        gh = new GHRequestTask(new GHListener() {
            @Override
            public void beforeSendingeGainGHRequest() {

            }

            @Override
            public void afterReceivingeGainGHResponse(GHResponse response) {
                //function to handle the response from the GH
                //response and pass onto the UI
                processGHResponse(response);
            }
        });
        //Calls to handle user input to Guided Help and
        // receive Guided Help response
        guidedhelp.setSid(ghSid);
        // Format input passing on prev question and input answer for GH to do its search
        if (ghSid != null) input = "Q"+ghprevtype+"-"+ghprevformat+"-"+ghprevquestionID+"="+ghprevanswerID;
        guidedhelp.setQuestion(input);
        gh.execute(guidedhelp);
    }

    // Handle processing the VA server response and populating into UI
    private void processVAResponse(VAResponse response) {

        eGainVAResponseData egainResponse = null;

        if (!response.isError()) {
            try {
                egainResponse = response.getVAResponseBody();
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }
        }

        if (egainResponse == null) {
            egainResponse = new eGainVAResponseData(vaSid, "offline", "", "");
        }
        String result = egainResponse.getText();
        //if (vaSid == null || (egainResponse.getSid() != null && egainResponse.getSid() != vaSid)) {
        if (vaSid == null)
        {
            vaSid = egainResponse.getSid();
        }
        //result text put in message list and present to UI
        message = new Message(result, VIEW_TYPE_MESSAGE_RECEIVED);
        agentImg = response.getResponseBaseURL()+egainResponse.getEmotion();
        agentName = "Kate";
        message.setImageURL(agentImg);
        message.setSender(agentName);
        messageList.add(message);
        int newMsgPosition = messageList.size() - 1;
        // Notify recycler view insert one new data.
        mMessageAdapter.notifyItemInserted(newMsgPosition);
        // Scroll RecyclerView to the last message.
        mMessageRecycler.scrollToPosition(newMsgPosition);
    }

    // Handle processing the Guided Help server response and populating into UI
    private void processGHResponse(GHResponse response) {

        eGainGHResponseData egainResponse = null;

        if (!response.isError()) {
            try {
                egainResponse = response.getGHResponseBody();
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }
        }

        if (egainResponse == null) {
            egainResponse = new eGainGHResponseData(ghSid, "offline", "");
        }

        //if (ghSid == null || (response.getSid() != null && response.getSid() != ghSid)) {
        if (ghSid == null)
        {
            ghSid = response.getSid();
        }
        //result text put in message list and present to UI
        ghprevquestionID = egainResponse.getQuestionId();
        ghprevanswerID = egainResponse.getAnswerId();
        ghprevtype = egainResponse.getType();
        ghprevformat = egainResponse.getFormat();
        message = new Message(egainResponse.getQuestion()+"\n"+egainResponse.getAnswer(), VIEW_TYPE_MESSAGE_RECEIVED);
        message.setSender("GH");
        messageList.add(message);
        int newMsgPosition = messageList.size() - 1;
        // Notify recycler view insert one new data.
        mMessageAdapter.notifyItemInserted(newMsgPosition);
        // Scroll RecyclerView to the last message.
        mMessageRecycler.scrollToPosition(newMsgPosition);
    }

    // Handle processing the Chat Client server response and populating into UI
    private void processChatResponse(ChatResponse response) {
        eGainChatResponseData egainResponse = null;

        if (!response.isError()) {
            try {
                egainResponse = response.getChatResponseBody();
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }
        }

        if (egainResponse == null) {
            Log.e("ChatResponse", "processChatResponse egainResponse null");
        }
    }

    // Chat Utility functions
    // eGain Chat methods to handle the Chat availability, initialization and startup
    private void setChatAvailable()
    {
        chatavailable = true;
    }

    private void resetChatAvailable()
    {
        chatavailable = false;
    }

    private void chkChatAvailability (){
        eGainChat chat = new eGainChat();

        ch = new ChatRequestTask(new ChatListener() {

            @Override
            public void beforeSendingeGainChatRequest () {

            }

            @Override
            public void afterReceivingeGainChatResponse (ChatResponse response){
                //function to handle the response from the GH
                //response and pass onto the UI
                eGainChatResponseData egainResponse = null;

                if (!response.isError()) {
                    try {
                        egainResponse = response.getChatResponseBody();
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString(), e);
                    }
                }
                if (egainResponse.getAvailable()) setChatAvailable();
            }

        });
        //Calls to handle receiving server chat messages to user
        chat.setOperation("availability");
        chat.setMessage(null);
        ch.execute(chat);
    }

    private void setChatInit()
    {
        chatinit = true;
    }

    private void resetChatInit()
    {
        chatinit = false;
    }

    private void initializeChat (){
        eGainChat chat =new eGainChat();

        ch =new ChatRequestTask(new ChatListener() {
            @Override
            public void beforeSendingeGainChatRequest () {

            }

            @Override
            public void afterReceivingeGainChatResponse (ChatResponse response){
                //function to handle the response from the GH
                //response and pass onto the UI

                if (!response.isError()) {
                    setChatInit();
                }
            }
        });
        //Calls to handle receiving server chat messages to user
        chat.setOperation("initChat");
        chat.setMessage(null);
        ch.execute(chat);
    }

    private void setChatStart()
    {
        chatstart = true;
    }

    private void resetChatStart()
    {
        chatinit = false;
    }

    private void startChat (String input){
        eGainChat chat =new eGainChat();

        ch =new ChatRequestTask(new ChatListener() {
            @Override
            public void beforeSendingeGainChatRequest () {

            }

            @Override
            public void afterReceivingeGainChatResponse (ChatResponse response){
                //function to handle the response from the GH
                //response and pass onto the UI

                if (!response.isError()) {
                    if (chatSid == null || (response.getSid() != null && response.getSid() != chatSid)) {
                        chatSid = response.getSid();
                    }
                    setChatStart();
                    connectWebSocket();
                }
            }
        });
        //Calls to handle receiving server chat messages to user
        chat.setOperation("startChat");
        chat.setMessage(input);
        ch.execute(chat);
    }

    private void endChat (){
        eGainChat chat =new eGainChat();

        ch =new ChatRequestTask(new ChatListener() {
            @Override
            public void beforeSendingeGainChatRequest () {

            }

            @Override
            public void afterReceivingeGainChatResponse (ChatResponse response){
                //function to handle the response from the GH
                //response and pass onto the UI

                if (!response.isError()) {
                    if (chatSid == null || (response.getSid() != null && response.getSid() != chatSid)) {
                        chatSid = response.getSid();
                    }
                    resetChatStart();
                }
            }
        });
        //Calls to handle receiving server chat messages to user
        chat.setOperation("endChat");
        //chat.setSid(chatSid);
        chat.setMessage(null);
        ch.execute(chat);
    }

    // eGain Chat Websocket to handle Incoming Agent messages from callback set up during the
    // start chat API call
    private void connectWebSocket() {
        String wsprotocal = "ws";
        String wsdomainhost = "10.10.1.240:4040";
        URI uri;
        try {
            uri = new URI(wsprotocal+"://"+wsdomainhost+"/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                Log.i("Websocket", "Open ");
            }

            @Override
            public void onMessage(String input) {
                final String agentmessage = input;
                // Process complex JSON response or for now just simple String returned
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //int newMsgPosition = messageList.size() - 1;
                        message = new Message(agentmessage, VIEW_TYPE_MESSAGE_RECEIVED);
                        //message.setImageURL(agentImg);
                        message.setSender("Agent");
                        messageList.add(message);
                        newIncoming = true;
                        if (agentmessage.contains("ended the chat")) {
                            chatend = true;
                        }
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
