package ca.mcgill.ecse321.project6.treeple_android;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_signup: {
                DialogFragment fragment = new SignUpFragment();
                fragment.show(getFragmentManager(), "signup");
                break;
            }
            case R.id.action_login: {
                DialogFragment fragment = new LoginFragment();
                fragment.show(getFragmentManager(), "login");
                break;
            }
            case R.id.action_logout: {
                logout();
                break;
            }
            case R.id.action_address: {
                if (checkActiveSession()) {
                    DialogFragment fragment = new AddressFragment();
                    fragment.show(getFragmentManager(), "address");
                    break;
                } else {
                    Toast.makeText(getBaseContext(), "Please log in to access this", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void plantTreeMenu(View view) {
        if(checkActiveSession()) {
            Intent plantIntent = new Intent(this, PlantTreeActivity.class);
            startActivity(plantIntent);
        } else {
            Toast.makeText(getBaseContext(), R.string.log_in_request, Toast.LENGTH_SHORT).show();
        }
    }

    public void cutDownTreeMenu(View view) {
        if(checkActiveSession()) {
            Intent cutDownIntent = new Intent(this, CutDownTreeActivity.class);
            startActivity(cutDownIntent);
        } else {
            Toast.makeText(getBaseContext(), R.string.log_in_request, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTree(View view) {
        if(checkActiveSession()) {
            Intent updateTreeIntent = new Intent(this, UpdateTreeActivity.class);
            startActivity(updateTreeIntent);
        } else {
            Toast.makeText(getBaseContext(), R.string.log_in_request, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if a user session is currently open
     * @return if a session exists
     */
    private boolean checkActiveSession() {
        return !SessionInfo.getGuid().equals("");
    }

    private void logout() {
        if(!checkActiveSession()) {
            Toast.makeText(getBaseContext(), "You weren't logged in!", Toast.LENGTH_SHORT).show();
        } else {
            RequestParams rp = new RequestParams();
            rp.add("sessionGuid", SessionInfo.getGuid());
            HttpUtils.post("logout", rp, new SessionResponse(getBaseContext(), "Logged Out"));
        }
    }
}
