package ca.mcgill.ecse321.project6.treeple_android;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

class SessionResponse extends TextHttpResponseHandler {
    private final Context context;
    private final String successMessage;

    public SessionResponse(Context context, String successMessage) {
        this.context = context;
        this.successMessage = successMessage;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        SessionInfo.setGuid(response);
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String er, Throwable throwable) {
        String error = "";
        JSONObject errorResponse = null;
        try {
            errorResponse = new JSONObject(er);
            error += errorResponse.get("message").toString();
        } catch (JSONException e) {
            if (errorResponse == null) {
                error = String.valueOf(statusCode);
            } else {
                error += e.getMessage();
            }
        } catch (NullPointerException e) {
            error = "Could not connect to "+HttpUtils.getBaseUrl();
        }
        SessionInfo.unsetGuid();
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }
}
