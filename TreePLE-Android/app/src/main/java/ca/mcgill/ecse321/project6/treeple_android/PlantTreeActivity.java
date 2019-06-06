package ca.mcgill.ecse321.project6.treeple_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class PlantTreeActivity extends LocationActivity {
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_plant_tree);

        // Set up spinners
        Spinner municipalitySpinner = findViewById(R.id.municipality_spinner);
        Spinner landTypeSpinner = findViewById(R.id.landtype_spinner);

        List<String> municipalityNames = Arrays.asList(
                "Ahuntsic-Cartierville", "Anjou", "Côte-des-Neiges--Notre-Dame-de-Grâce",
                "Lachine", "LaSalle", "Le Plateau-Mont-Royal", "L'Île-Bizard--Saint-Geneviève",
                "Mercier--Hochelaga-Maisonneuve", "Montréal-Nord", "Outremont", "Pierrefonds-Roxboro",
                "Rivière-des-Prairies--Pointe-aux-Trembles", "Rosemont-La Petite-Patrie",
                "Saint-Laurent", "Saint-Léonard", "Verdun", "Ville-Marie", "Villeray--Saint-Michel--Parc-Extension",
                "Baie-d'Urfé", "Beaconsfield", "Côte-Saint-Luc", "Dollard-Des Ormeaux",
                "Dorval", "Hampstead", "Kirkland", "Mont-Royal", "Montréal-Est",
                "Montréal-Ouest", "Pointe-Claire", "Sainte-Anne-de-Bellevue", "Senneville", "Westmount"
        );
        List<String> landtypeNames = Arrays.asList("Residential", "Institutional", "Park", "Municipal");

        ArrayAdapter<String> municipalities = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                municipalityNames);
        ArrayAdapter<String> landtypes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                landtypeNames);
        municipalitySpinner.setAdapter(municipalities);
        landTypeSpinner.setAdapter(landtypes);
        if(timer == null) {
            timer = new Timer();
        }
        // Set up location coordinate display
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    // Handler has to be used since Android does not allow
    // non-main threads to change views.
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView tv = findViewById(R.id.location_text);
            if(!Double.isNaN(latitude)) {
                tv.setText(String.format("%.3f, %.3f", latitude, longitude));
            } else {
                tv.setText("Waiting for location...");
            }
        }
    };

    public void plantTree(View view) {
        // Ensure location has been fetched
        if (latitude == Double.NaN) {
            Toast.makeText(getBaseContext(), "Please wait for location.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show a message so the user knows something is happening
        Toast.makeText(getBaseContext(), "Working...", Toast.LENGTH_SHORT).show();
        String sessionGuid = SessionInfo.getGuid();
        String error = "";
        // Get tree info
        TextView tv = findViewById(R.id.species_text);
        String species = tv.getText().toString();
        tv = findViewById(R.id.height_text);
        String height = tv.getText().toString();
        tv = findViewById(R.id.canopy_text);
        String canopy = tv.getText().toString();
        Spinner municipality = findViewById(R.id.municipality_spinner);
        Spinner landtype = findViewById(R.id.landtype_spinner);
        // Set up the HTTP request
        RequestParams rp = new RequestParams();
        rp.add("latitude", String.valueOf(latitude));
        rp.add("longitude", String.valueOf(longitude));
        rp.add("species", species);
        rp.add("height", height);
        rp.add("canopy", canopy);
        rp.add("mun", enumizeMunicipality(municipality.getSelectedItem().toString()));
        rp.add("landType", landtype.getSelectedItem().toString());
        rp.add("sessionGuid", sessionGuid);
        HttpUtils.post("plant", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // We don't get anything useful in response
                clearError();
                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
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

    @Override
    public void finish() {
        super.finish();
        try {
            timer.cancel();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    // Strips formatting and special characters so our string is identical to the names of
    // municipality enums in the backend.
    private String enumizeMunicipality(String regularName) {
        String normalized = Normalizer.normalize(regularName, Normalizer.Form.NFD);
        return normalized.replaceAll("[^a-zA-Z]", "");
    }

    private void refreshError(String error) {
        ((TextView) findViewById(R.id.plant_error)).setText(error);
    }

    private void clearError() {
        refreshError("");
    }

    abstract class DisplayTimerTask extends TimerTask {
        final TextView tv;
        public DisplayTimerTask(TextView tv) {
            this.tv = tv;
        }
    }
}
