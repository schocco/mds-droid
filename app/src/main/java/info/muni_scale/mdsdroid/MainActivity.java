package info.muni_scale.mdsdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import info.muni_scale.mdsdroid.auth.LoginActivity;
import info.muni_scale.mdsdroid.gpx.GpxLoggerActivity;
import info.muni_scale.mdsdroid.mscale.MscaleActivity;


public class MainActivity extends AppCompatActivity {

//    private MenuItemAdapter itemAdapter;
    private Toolbar toolbar;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        this.setSupportActionBar(toolbar);
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

        Intent intent;
        switch(id) {
            case R.id.action_settings:
                return true;
                //TODO: link settings view
            case R.id.action_mscales:
                intent = new Intent(MainActivity.this, MscaleActivity.class);
                break;
            case R.id.action_gpx_logger:
                intent = new Intent(MainActivity.this, GpxLoggerActivity.class);
                break;
            default:
                Log.w(TAG, "Menu entry not linked to any intent! " + item.getTitle());
                return true;
        }
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }
}
