package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLRequest {

    Request request = new Request();
    Actions action = new Actions();

    public URLRequest build(Runnable runnable){
        action.build(runnable);
        return this;
    }

    public Object getDefaultActionArg(){
        return action.getArg();
    }

    public void execute(String URL, String method){
        request.execute(URL, method);
    }

    class Request extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(strings[1]);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) connection.getContent()));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    br.close();
                    return result.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            action.setArg(s).execute();
        }
    }
}
