package info.muni_scale.mdsdroid.gpx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.muni_scale.mdsdroid.R;

/**
 * Upload activity which needs the path to the file as context.
 * Lets the user sign in via google and upload the file to the muni scale webservice.
 */
public class UploadGpxActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    /**
     * Is there a ConnectionResult resolution in progress?
     */
    private boolean mIsResolving = false;

    /**
     * Should we automatically resolve ConnectionResults when possible?
     */
    private boolean mShouldResolve = false;

    private static final String TAG = UploadGpxActivity.class.getSimpleName();

    private TextView statusTxtView;

    private static final String URL_HOST = "https://muni-scale.info";
    private static final String URL_AUTH_BACKENDS = URL_HOST + "/api/v1/socialauth_backends/";

    /**
     * The server client id which is allowed to access google oauth.
     */
    private static final String SERVER_CLIENT_ID = "1098325359555-4641mspm0k5nov4rbdu3d7ijbphmtivf.apps.googleusercontent.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gpx);
        statusTxtView = (TextView) findViewById(R.id.auth_status_txt);
        sendAuthBackendsRequest();
    }

    /**
     * Adds one login button to the view for each social auth backend that is available from the
     * web service. Adds appropriate clickhandlers to open a browser which is needed for the oauth2
     * flow.
     * @param backends
     */
    private void showLoginButtons(JSONObject backends) {
        try {
            if (backends.getJSONObject("meta").getInt("total_count") >= 1) {
                JSONArray objects = backends.getJSONArray("objects");
                LinearLayout buttonHolder = (LinearLayout) findViewById(R.id.loginButtonHolder);
                for (int i = 0; i < objects.length(); i++) {
                    //create button
                    final JSONObject socialBackend = objects.getJSONObject(i);
                    Button button = new Button(UploadGpxActivity.this);
                    String backendName = socialBackend.getString("name");
                    final String loginUrl = String.format("%s/login/%s/?next=android", URL_HOST, backendName);
                    button.setText(backendName);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // show the login screen in the devices browser,
                            // TODO: when login succeeds, the user should be redirected to an app-specific url
                            // where the app can obtain the access token from
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl));
                            startActivity(browserIntent);
                        }
                    });
                    buttonHolder.addView(button);
                }
            } else {
                //TODO: display error
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendAuthBackendsRequest() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, URL_AUTH_BACKENDS, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        showLoginButtons(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

// Access the RequestQueue through your singleton class.
        WebRequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_gpx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnected(Bundle bundle) {

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == 0) {
            //TODO: differentiate between backends
        }
    }

    public void onConnectionFailed() {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed");

    }

}
