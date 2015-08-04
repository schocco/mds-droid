package info.muni_scale.mdsdroid.gpx;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class WebRequestSingleton {
    private static WebRequestSingleton instance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private WebRequestSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized WebRequestSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new WebRequestSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
