package ca.mcgill.ecse321.project6.treeple_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/*
This shows the add/edit address dialog
It also associates an address with a latitude and longitude
 */

public class AddressFragment extends DialogFragment {
    private static String streetNumber = "Street Number", streetName = "Street Name", postalCode = "Postal Code";
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        getUserAddress();
        final Context context = getActivity().getBaseContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_address, null);
        //These should set the dialog fields to any existing address associated with the user
        ((EditText)layout.findViewById(R.id.street_number_field)).setHint(streetNumber);
        ((EditText)layout.findViewById(R.id.street_name_field)).setHint(streetName);
        ((EditText)layout.findViewById(R.id.postal_code_field)).setHint(postalCode);
        builder.setView(layout)
                .setPositiveButton("Set Address", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String streetNumber, streetName, postalCode;
                       double latitude, longitude;
                       // Errors here shouldn't happen but it's best to not crash
                       try {
                           streetNumber = ((EditText) getDialog().findViewById(R.id.street_number_field)).getText().toString();
                           streetName = ((EditText) getDialog().findViewById(R.id.street_name_field)).getText().toString();
                           postalCode = ((EditText) getDialog().findViewById(R.id.postal_code_field)).getText().toString();
                       } catch (NullPointerException e) {
                           Toast.makeText(context, "Error: Was everything filled in?", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       // This should only happen if the address entered is a valid format, but isn't a real location
                       // For example, "123 Fake Street X1X 1X1" meets formatting requirements but is, shockingly, fake.
                       try {
                            Geocoder geocoder = new Geocoder(context, Locale.CANADA);
                           List<Address> addresses = geocoder.getFromLocationName(
                                   streetNumber + " " + streetName + " " + postalCode,
                                   1);
                           Address address = addresses.get(0);
                           latitude = address.getLatitude();
                           longitude = address.getLongitude();

                       } catch (Exception e) {
                           Toast.makeText(context, "Error: This does not appear to be an actual address", Toast.LENGTH_LONG).show();
                           return;
                       }

                       RequestParams rp = new RequestParams();
                       rp.add("sessionGuid", SessionInfo.getGuid());
                       rp.add("streetNumber", streetNumber);
                       rp.add("streetName", streetName);
                       rp.add("postalCode", postalCode);
                       rp.add("latitude", String.valueOf(latitude));
                       rp.add("longitude", String.valueOf(longitude));

                       setUserAddress(context, rp);
                   }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddressFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /**
     *
     * @return response for user
     */
    private void setUserAddress(final Context context, RequestParams rp) {
        HttpUtils.post("address", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String error = "";
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                } catch (NullPointerException e) {
                    error = "Could not connect to "+HttpUtils.getBaseUrl();
                }
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserAddress() {
        final JSONObject Response;
        final Context context = getActivity().getBaseContext();
        RequestParams rp = new RequestParams();
        rp.add("sessionGuid", SessionInfo.getGuid());
        HttpUtils.get("address", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.err.println("Success "+response.toString());
                try {
                    streetNumber = String.valueOf((int)response.get("streetNumber"));
                    streetName = String.valueOf(response.get("streetName"));
                    postalCode = String.valueOf(response.get("postalCode"));
                    System.err.println(streetNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.err.println("Error "+errorResponse.toString());
            }
        });
    }
}
