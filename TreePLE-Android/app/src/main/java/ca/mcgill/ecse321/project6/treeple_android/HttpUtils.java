package ca.mcgill.ecse321.project6.treeple_android;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
Pretty much the same class as from the Event Registration Assignment
10.0.2.2 is the address associated with the host machine by default
for the Android emulator, and is used for local testing.
 */
class HttpUtils {
    //public static final String DEFAULT_BASE_URL = "http://10.0.2.2:8080/";
    public static final String DEFAULT_BASE_URL="http://ecse321-6.ece.mcgill.ca:8080/";
    private static String baseUrl;
    private static final AsyncHttpClient client = new AsyncHttpClient();

    static {
        baseUrl = DEFAULT_BASE_URL;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        HttpUtils.baseUrl = baseUrl;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return baseUrl + relativeUrl;
    }
}

