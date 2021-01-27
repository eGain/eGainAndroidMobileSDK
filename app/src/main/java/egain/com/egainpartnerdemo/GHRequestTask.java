package egain.com.egainpartnerdemo;

import android.os.AsyncTask;


public class GHRequestTask extends AsyncTask<eGainGH, Integer, GHResponse> {
    private GHListener listener;

    public GHRequestTask(GHListener listener) {
        this.listener = listener;
    }

    @Override
    protected GHResponse doInBackground(eGainGH... params) {

        eGainGH gh = params[0];
        if (gh.getSid() == null) {
            if (gh.initGH(gh.getQuestion()).isError()) return null;
            return gh.startGH(gh.getQuestion());
        }
        else {
            return gh.searchGH(gh.getQuestion());
        }
    }

    @Override
    protected void onPostExecute(GHResponse result) {
        super.onPostExecute(result);
        listener.afterReceivingeGainGHResponse(result);
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        listener.beforeSendingeGainGHRequest();
    }
}
