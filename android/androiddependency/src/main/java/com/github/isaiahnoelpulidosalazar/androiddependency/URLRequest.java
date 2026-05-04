package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple HTTP request utility that performs network calls off the main thread
 * using {@link AsyncTask}, and delivers the response string back to the main thread
 * via a configured {@link Actions} callback.
 *
 * <p>The response string is stored as the argument of the associated {@link Actions}
 * instance, which is then executed on the main thread once the request completes.
 * Retrieve it inside the callback using {@link #getDefaultActionArg()}.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * URLRequest request = new URLRequest();
 * request.build(() -> {
 *     String response = (String) request.getDefaultActionArg();
 *     Log.d("TAG", response);
 * });
 * request.execute("https://api.example.com/data", "GET");
 * }</pre>
 *
 * @deprecated {@link AsyncTask} is deprecated as of API 30. Consider replacing with
 *             {@link java.util.concurrent.ExecutorService} +
 *             {@link android.os.Handler} or Kotlin coroutines for new code.
 */
@Deprecated
public class URLRequest {

    Request request = new Request();
    Actions action = new Actions();

    /**
     * Sets the {@link Runnable} that will be executed on the main thread after the HTTP
     * response is received.
     *
     * <p>Inside the runnable, call {@link #getDefaultActionArg()} to retrieve the
     * response string (or an error message prefixed with {@code "Error:"}).</p>
     *
     * @param runnable the callback to run after the request completes; must not be {@code null}
     * @return this {@code URLRequest} instance, for chaining
     */
    public URLRequest build(Runnable runnable){
        action.build(runnable);
        return this;
    }

    /**
     * Returns the argument stored in the underlying {@link Actions} instance after a
     * completed request.
     *
     * <p>This will be the HTTP response body string on success, or a string of the
     * form {@code "Error: <message>"} on failure.</p>
     *
     * @return the response string as an {@link Object}, cast to {@link String} before use;
     *         {@code null} if the request has not yet completed
     */
    public Object getDefaultActionArg(){
        return action.getArg();
    }

    /**
     * Executes the HTTP request asynchronously using the given URL and HTTP method.
     *
     * <p>The request runs on a background thread. When it completes, the response string
     * is set as the argument of the associated {@link Actions} instance, which is then
     * posted to the main thread for execution.</p>
     *
     * @param URL    the full URL to send the request to (e.g., {@code "https://api.example.com/data"})
     * @param method the HTTP method to use (e.g., {@code "GET"}, {@code "POST"})
     */
    public void execute(String URL, String method){
        request.execute(URL, method);
    }

    /**
     * Internal {@link AsyncTask} that performs the HTTP request on a background thread
     * and delivers the result to the main thread via {@link Actions}.
     */
    class Request extends AsyncTask<String, Void, String> {

        /**
         * Performs the HTTP request on a background thread.
         *
         * @param strings {@code strings[0]} is the URL; {@code strings[1]} is the HTTP method
         * @return the response body as a string, or {@code "Error: <message>"} on failure
         */
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

        /**
         * Called on the main thread after {@link #doInBackground} completes.
         *
         * <p>Stores the response string into the {@link Actions} argument and
         * executes the configured callback.</p>
         *
         * @param s the response string returned by {@link #doInBackground}
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            action.setArg(s).execute();
        }
    }
}