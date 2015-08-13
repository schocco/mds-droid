package info.muni_scale.mdsdroid.gpx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import info.muni_scale.mdsdroid.R;
import info.muni_scale.mdsdroid.auth.LoginActivity;
import info.muni_scale.mdsdroid.tracks.Track;

/**
 * Upload activity which needs the path to the file as context.
 * Lets the user sign in via google and upload the file to the muni scale webservice.
 */
public class UploadGpxActivity extends AppCompatActivity  {

    private EditText descriptionInput;
    private EditText nameInput;
    private CheckBox privateChk;
    private static final int AUTH_REQUEST = 0;
    private static final String TAG = UploadGpxActivity.class.getSimpleName();
    private Track track;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gpx);
        ensureAuthenticated();
        descriptionInput = (EditText) findViewById(R.id.description_input);
        nameInput = (EditText) findViewById(R.id.name_input);
        privateChk = (CheckBox) findViewById(R.id.chk_private);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(AUTH_REQUEST == requestCode) {
            Log.d(TAG, "Result code is " + resultCode);
            if(resultCode == Activity.RESULT_OK) {
                // things are ok :D
            } else {
                // nothings ok!
            }
        }
    }

    private void saveTrail() {
        //TODO: receive track as bundle
        GpxFileWriter fileWriter = new GpxFileWriter(track, this);
        //TODO: make sure track has a name and description
        track.setName(nameInput.getText().toString()); //TODO: disable text input after saving
        track.setDescription(descriptionInput.getText().toString());
        Toast toast;
        try {
            File written = fileWriter.writeFile();
            toast = Toast.makeText(this, "Track saved to " + written.getAbsolutePath(), Toast.LENGTH_SHORT);
        } catch (GpxFileWriter.GpxWriterException e) {
            toast = Toast.makeText(this, "Could not save track :´( ", Toast.LENGTH_SHORT);
            Log.e(TAG, "Could not save track", e);
        }
        toast.show();
    }

    private void uploadTrail() {
        //TODO: use access token and request queue to upload the trail to the webservice
    }

    private void ensureAuthenticated() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, AUTH_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

}
