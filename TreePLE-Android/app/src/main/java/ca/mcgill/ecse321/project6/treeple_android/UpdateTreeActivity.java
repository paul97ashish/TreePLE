package ca.mcgill.ecse321.project6.treeple_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/*
Update state and update height/canopy are two separate REST API calls
so they're separated out here so we don't generate multiple calls
 */

public class UpdateTreeActivity extends LocationActivity {
    private final List<String> treeList = new ArrayList<>();
    private final HashMap<String, String[]> existingStatuses = new HashMap<>();
    private ArrayAdapter<String> treeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_update_tree);

        Spinner spinner = findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(statusAdapter);

        spinner = findViewById(R.id.health_spinner);
        ArrayAdapter<CharSequence> healthAdapter = ArrayAdapter.createFromResource(this, R.array.health_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(healthAdapter);

        spinner = findViewById(R.id.marking_spinner);
        ArrayAdapter<CharSequence> markAdapter = ArrayAdapter.createFromResource(this, R.array.marking_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(markAdapter);

        spinner = findViewById(R.id.update_tree_selection);
        treeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, treeList);
        spinner.setAdapter(treeAdapter);

        // Sets starting selections to the current state of the tree
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] selectionState = existingStatuses.get(parent.getItemAtPosition(position));
                if(selectionState != null) {
                    List<String> statusVals = Arrays.asList(getResources().getStringArray(R.array.status_array));
                    ((Spinner) findViewById(R.id.status_spinner)).setSelection(statusVals.indexOf(selectionState[0]));

                    List<String> healthVals = Arrays.asList(getResources().getStringArray(R.array.health_array));
                    ((Spinner) findViewById(R.id.health_spinner)).setSelection(healthVals.indexOf(selectionState[1]));

                    List<String> markVals = Arrays.asList(getResources().getStringArray(R.array.marking_array));
                    ((Spinner) findViewById(R.id.marking_spinner)).setSelection(markVals.indexOf(selectionState[2]));

                    ((EditText) findViewById(R.id.height_entry)).setText(selectionState[3]);
                    ((EditText) findViewById(R.id.canopy_entry)).setText(selectionState[4]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void submitUpdateState(View view) {
        Spinner treeSelection = findViewById(R.id.update_tree_selection);
        String treeId;
        try {
            String[] tokens = treeSelection.getSelectedItem().toString().split(" ");
            treeId = tokens[tokens.length - 1];
        } catch (NullPointerException e) {
            Toast.makeText(getBaseContext(), "Select a tree", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams rp = new RequestParams();
        rp.add("sessionGuid", SessionInfo.getGuid());
        rp.add("id", treeId);
        rp.add("newStatus", ((Spinner) findViewById(R.id.status_spinner)).getSelectedItem().toString());
        rp.add("newHealth", ((Spinner) findViewById(R.id.health_spinner)).getSelectedItem().toString());
        rp.add("newMarking", ((Spinner) findViewById(R.id.marking_spinner)).getSelectedItem().toString());

        HttpUtils.post("update-tree-state", rp, new UpdateResponseHandler());
        refreshTreeList(null);
    }

    public void submitUpdateHeightCanopy(View view) {
        Spinner treeSelection = findViewById(R.id.update_tree_selection);
        String treeId;
        try {
            String[] tokens = treeSelection.getSelectedItem().toString().split(" ");
            treeId = tokens[tokens.length - 1];
        } catch (NullPointerException e) {
            Toast.makeText(getBaseContext(), "Select a tree", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams rp = new RequestParams();
        rp.add("sessionGuid", SessionInfo.getGuid());
        rp.add("id", treeId);
        try {
            rp.add("newHeight", ((EditText) findViewById(R.id.height_entry)).getText().toString());
            rp.add("newCanopy", ((EditText) findViewById(R.id.canopy_entry)).getText().toString());
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtils.post("update-height-canopy", rp, new UpdateResponseHandler());
        refreshTreeList(null);
    }

    public void refreshTreeList(View view) {
        if(this.latitude == Double.NaN) {
            return;
        }
        RequestParams rp = new RequestParams();
        rp.add("latitude", String.valueOf(latitude));
        rp.add("longitude", String.valueOf(longitude));
        rp.add("distance", String.valueOf(20.0));
        HttpUtils.get("trees-distance", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                treeList.clear();
                for(int i = 0; i < response.length(); i++) {
                    try {
                        String label = response.getJSONObject(i).getString("species") + ", " + response.getJSONObject(i).getString("id");
                        String[] state = new String[5];
                        JSONObject stateObject = response.getJSONObject(i).getJSONObject("state");
                        state[0] = stateObject.getString("status");
                        state[1] = stateObject.getString("health");
                        state[2] = stateObject.getString("mark");

                        state[3] = response.getJSONObject(i).getString("heightMeters");
                        state[4] = response.getJSONObject(i).getString("canopyDiameterMeters");

                        treeList.add(label);
                        existingStatuses.put(label, state);
                    } catch (Exception e) {
                        refreshError(e.getMessage());
                    }
                }
                treeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String error = "";
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                } catch (NullPointerException e) {
                    error += "Unable to connect to "+HttpUtils.DEFAULT_BASE_URL;
                }
                refreshError(error);
            }
        });
    }

    private void refreshError(String error) {
        ((TextView) findViewById(R.id.update_error)).setText(error);
    }
    private void clearError() { refreshError(""); }

    class UpdateResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            clearError();
            Toast.makeText(getBaseContext(), "Updated Tree!", Toast.LENGTH_SHORT).show();
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
    }

}
