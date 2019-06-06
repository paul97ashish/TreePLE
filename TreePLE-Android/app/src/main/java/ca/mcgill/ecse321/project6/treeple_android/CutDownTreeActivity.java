package ca.mcgill.ecse321.project6.treeple_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CutDownTreeActivity extends LocationActivity {
    private List<String> availableTrees = null;
    private ArrayAdapter<String> availableTreeAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cut_down_tree);

        // Set up spinner and list
        Spinner availableTreeSpinner = findViewById(R.id.spinner_tree_list);
        availableTrees = new ArrayList<>();
        availableTreeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                availableTrees
        );
        availableTreeSpinner.setAdapter(availableTreeAdapter);
    }

    public void cutDownTree(View view) {
        Spinner treeSpinner = findViewById(R.id.spinner_tree_list);
        String[] treeTokens = treeSpinner.getSelectedItem().toString().split(" ");
        // this should get the tree ID correctly
        String treeId = treeTokens[treeTokens.length - 1];
        String sessionGuid = SessionInfo.getGuid();
        RequestParams rp = new RequestParams();
        rp.add("sessionGuid", sessionGuid);
        HttpUtils.post("cutdown/"+treeId, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                clearError();
                String message;
                try {
                    message = response.get("message").toString();
                } catch (JSONException e) {
                    message = response.toString();
                }
                Toast.makeText(getBaseContext(), "Cut Down Tree!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String error = "";
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                } catch (NullPointerException e) {
                    error = "Unable to connect to "+HttpUtils.DEFAULT_BASE_URL;
                }
                refreshError(error);
            }
        });
        refreshTreeList(null);
    }

    public void refreshTreeList(View view) {
        if (latitude == Double.NaN) {   // checking longitude as well is redundant since they are set/unset together
            return;
        }
        RequestParams rp = new RequestParams();
        rp.add("latitude", String.valueOf(latitude));
        rp.add("longitude", String.valueOf(longitude));
        rp.add("distance", String.valueOf(20.0)); // hard code 20 meters. this is arbitrary
        HttpUtils.get("trees-distance", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                clearError();
                availableTrees.clear();
                for(int i = 0; i < response.length(); i++) {
                    try {
                        availableTrees.add(response.getJSONObject(i).getString("species") + ", "+response.getJSONObject(i).getString("id"));
                    } catch (Exception e) {
                        refreshError(e.getMessage());
                    }
                }
                availableTreeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String error = "";
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                } catch (NullPointerException e) {
                    error = "Unable to connect to "+HttpUtils.DEFAULT_BASE_URL;
                }
                refreshError(error);
            }
        });
    }

    private void refreshError(String error) {
        ((TextView) findViewById(R.id.cut_down_error)).setText(error);
    }

    private void clearError() {
        refreshError("");
    }
}
