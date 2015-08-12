package info.muni_scale.mdsdroid.auth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.muni_scale.mdsdroid.R;
import info.muni_scale.mdsdroid.gpx.WebRequestSingleton;

/**
 * The type Login activity.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private WebView webView;
    private static final String URL_HOST = "https://muni-scale.info";
    private static final String URL_AUTH_BACKENDS = URL_HOST + "/api/v1/socialauth_backends/";
    private static final String URL_AUTH_STATUS = URL_HOST + "/api/v1/users/auth-status/";
    private static final String NEXT_VALUE = "/android";
    private static final Pattern URL_TOKEN_PATTERN = Pattern.compile("token=([^#]+)");
    private SharedPreferences preferences;
    private String accessToken;
    private TextView loadingLabel;
    private JSONArray loginProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getTokenFromPreferences();
        setUpWebView();
        loadingLabel = (TextView) findViewById(R.id.get_backends_txt);
        sendAuthStatusRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTokenFromPreferences();
        sendAuthStatusRequest();
    }

    private void getTokenFromPreferences() {
        preferences = getSharedPreferences("token", MODE_PRIVATE);
        if (preferences.contains("token")) {
            accessToken = preferences.getString("token", null);
        }
    }

    /**
     * Initializes the webview. Overrides the webvies url loading.
     */
    private void setUpWebView() {
        webView = (WebView) findViewById(R.id.webview_social_auth);
        webView.getSettings().setJavaScriptEnabled(true);
        // override url loading so that we can extract the access token from the redirect url
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "webview URL change: " + url);
                if (url.contains("?token=")) {
                    storeToken(url);
                    LoginActivity.this.finishSuccessful();
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

    /**
     * Sends a request to the rest api to check if the user is currently authenticated if there is
     * an access token in the preferences.
     *
     * If no token is present or if the token is not valid calls {@link #sendAuthBackendsRequest()}.
     *
     */
    private void sendAuthStatusRequest() {
        Response.Listener<JSONObject> authStatusSuccessListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("loggedin")) {
                        // current token is valid. we can close this view.
                        finishSuccessful();
                    } else {
                        if(!hasLoadedLoginBackends()) {
                            // only request backends if they are not shown yet
                            sendAuthBackendsRequest();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener authStatusErrorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
                LoginActivity.this.finishError(error.getMessage());
            }
        };

        // only check auth status if there is an access token available
        // otherwise user hasn't logged in before
        if (accessToken != null) {
            loadingLabel.setText("checking authentication status...");
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, URL_AUTH_STATUS, null,
                    authStatusSuccessListener, authStatusErrorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.putAll(super.getHeaders());
                    headers.put("Authorization", "Bearer " + accessToken);
                    return headers;
                }
            };
            WebRequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            Log.d(TAG, "no access token stored in preferences.");
            if(!hasLoadedLoginBackends()) {
                // only request backends if they are not shown yet
                sendAuthBackendsRequest();
            }
        }

    }

    /**
     * Extracts the access token from the redirect uri and stores it in the preferences store.
     *
     * @param url url containing the token in a query string
     */
    private void storeToken(String url) {
        Matcher m = URL_TOKEN_PATTERN.matcher(url);
        if (m.find()) {
            String token = m.group(1);
            preferences = getSharedPreferences("token", MODE_PRIVATE);
            preferences.edit().putString("token", token).apply();
        } else {
            Log.e(TAG, "Could not extract token from URL.");
            this.finishError(getString(R.string.error_token_could_not_be_read));
        }
    }

    /**
     * Queries the webservice for a list of oauth backend providers from which the user can choose.
     */
    private void sendAuthBackendsRequest() {
        loadingLabel.setText("Loading login providers...");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, URL_AUTH_BACKENDS, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Got response with login backends..." + response);
                        removeProgressBar();
                        showLoginButtons(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                removeProgressBar();
                Log.e(TAG, error.getMessage(), error);
                LoginActivity.this.finishError(error.getMessage());
            }
        });

// Access the RequestQueue through your singleton class.
        Log.d(TAG, "Sending request for login backends");
        WebRequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }


    /**
     * Ends this activity with a success code to indicate that the user is logged in. The calling
     * activity can proceed to do api calls with the token stored in the preferences.
     */
    private void finishSuccessful() {
        this.setResult(Activity.RESULT_OK);
        Toast.makeText(getApplicationContext(),
                R.string.success_login_notification, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * Ends the activity with the canceled status and displays an error if appropriate.
     */
    private void finishError(String message) {
        this.setResult(Activity.RESULT_CANCELED);
        if (message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        this.finish();
    }


    private void removeProgressBar() {
        View progressBar = findViewById(R.id.get_backends_progress_bar);
        View progressLabel = findViewById(R.id.get_backends_txt);
        if (progressBar != null) {
            ((ViewManager) progressBar.getParent()).removeView(progressBar);
        }
        if (progressLabel != null) {
            ((ViewManager) progressLabel.getParent()).removeView(progressLabel);
        }
    }

    private void showLoginButtons(JSONObject backends) {
        LinearLayout buttonHolder = (LinearLayout) findViewById(R.id.loginButtonHolder);
        try {
            if (backends.getJSONObject("meta").getInt("total_count") >= 1) {
                loginProviders = backends.getJSONArray("objects");
                if(loginProviders.length() > buttonHolder.getChildCount()) {
                    buttonHolder.removeAllViews();
                    for (int i = 0; i < loginProviders.length(); i++) {
                        //create button
                        final JSONObject socialBackend = loginProviders.getJSONObject(i);
                        Button button = new Button(LoginActivity.this);
                        String backendName = socialBackend.getString("name");
                        final String loginUrl = String.format("%s/login/%s/?next=%s", URL_HOST, backendName, URLEncoder.encode(NEXT_VALUE, "utf-8"));
                        button.setText(backendName);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                webView.loadUrl(loginUrl);
                                webView.setVisibility(View.VISIBLE);
                            }
                        });
                        buttonHolder.addView(button);
                    }
                }
            } else {
                this.finishError(getString(R.string.error_no_loginproviders));
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            this.finishError(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    /**
     * Checks if login buttons are shown.
     *
     * @return true if login backends have already been loaded and rendered in the view
     */
    private boolean hasLoadedLoginBackends() {
        LinearLayout buttonHolder = (LinearLayout) findViewById(R.id.loginButtonHolder);
        return buttonHolder != null && buttonHolder.getChildCount() > 0 && loginProviders != null;
    };

}
