package egain.com.egainpartnerdemo;

public interface GHListener {
    /**
     * This method would be called on the UI Thread from
     * VAListener.onPreExecute()
     */
    public void beforeSendingeGainGHRequest();

    /**
     * This method would be called on the UI Thread from
     * VAListener.onPostExecute()
     *
     * @param response
     */
    public void afterReceivingeGainGHResponse(GHResponse response);

}
