package egain.com.egainpartnerdemo;

public interface VAListener {
    /**
     * This method would be called on the UI Thread from
     * VAListener.onPreExecute()
     */
    public void beforeSendingeGainVARequest();

    /**
     * This method would be called on the UI Thread from
     * VAListener.onPostExecute()
     *
     * @param response
     */
    public void afterReceivingeGainVAResponse(VAResponse response);

}
