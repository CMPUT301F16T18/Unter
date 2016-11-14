package ca.ualberta.cs.unter.util;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import ca.ualberta.cs.unter.UnterConstant;

/**
 * A Utility class provide helper static method
 * to interact with Google Maps API
 */
public class HttpClientUtil {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=" + UnterConstant.GOOGLEMAP_API_KEY;
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * Send a HTTP GET request
     * @param url the url to send
     * @param params parameter
     * @param responseHandler a custom async event handler
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i("Debug", getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Send a HTTP POST request
     * @param url the url to send
     * @param params parameter
     * @param responseHandler a custom async event handler
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Heloper method
     * @param relativeUrl the url
     * @return
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + "&" + relativeUrl;
    }
}
