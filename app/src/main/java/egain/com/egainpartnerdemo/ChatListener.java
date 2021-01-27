package egain.com.egainpartnerdemo;

public interface ChatListener {
    /**
     * This method would be called on the UI Thread from
     * VAListener.onPreExecute()
     */
    public void beforeSendingeGainChatRequest();

    /**
     * This method would be called on the UI Thread from
     * VAListener.onPostExecute()
     *
     * @param response
     */
    public void afterReceivingeGainChatResponse(ChatResponse response);

}
