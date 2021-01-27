package egain.com.egainpartnerdemo;

import android.os.AsyncTask;


public class VARequestTask extends AsyncTask<eGainVA, Integer, VAResponse> {
    private VAListener listener;

    public VARequestTask(VAListener listener) {
        this.listener = listener;
    }

    @Override
    protected VAResponse doInBackground(eGainVA... params) {

        eGainVA assistant = params[0];
        return assistant.solve();
    }

    @Override
    protected void onPostExecute(VAResponse result) {
        super.onPostExecute(result);
        listener.afterReceivingeGainVAResponse(result);
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        listener.beforeSendingeGainVARequest();
    }
}
