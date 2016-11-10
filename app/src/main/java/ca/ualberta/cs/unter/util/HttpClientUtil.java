package ca.ualberta.cs.unter.util;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import ca.ualberta.cs.unter.UnterConstant;

public class HttpClientUtil {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=" + UnterConstant.GOOGLEMAP_API_KEY;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i("Debug", getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + "&" + relativeUrl;
    }
}
