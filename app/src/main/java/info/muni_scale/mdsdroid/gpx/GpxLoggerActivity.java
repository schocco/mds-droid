package info.muni_scale.mdsdroid.gpx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import info.muni_scale.mdsdroid.R;
import info.muni_scale.mdsdroid.tracks.Track;
import info.muni_scale.mdsdroid.tracks.TrackSectionAdapter;

public class GpxLoggerActivity extends AppCompatActivity implements GpxServiceListener {

    private static final String TAG = GpxLoggerActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Intent serviceIntent;
    private GpxLogger boundService;
    private Track track;
    private Menu menu;
    private boolean isRecording;
    private ListView sectionListView;
    private TrackSectionAdapter sectionAdapter;

    /**
     * Provides a connection to the GPS Logging Service
     */
    private final ServiceConnection gpsServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //loggingService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            boundService = ((GpxService.GpsBinder) service).getService();
            boundService.addServiceListener(GpxLoggerActivity.this);
            setRecordingState(boundService.isRecording()); // make sure UI elements match state
            //

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();
        startAndBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUi();
        startAndBindService();
    }

    private void initUi() {
        setContentView(R.layout.activity_gpx_logger);
        toolbar = (Toolbar) findViewById(R.id.logger_toolbar);
        this.setSupportActionBar(toolbar);
        setUpFloatingButton();
        sectionListView = (ListView) findViewById(R.id.lvToDoList);
        sectionAdapter = new TrackSectionAdapter(GpxLoggerActivity.this);
        sectionListView.setAdapter(sectionAdapter);
    }

    /**
     * Adds a listener to the floating action button.
     */
    private void setUpFloatingButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (track == null) {
                    startRecording();
                } else {
                    startNewSection();
                }
                Snackbar.make(findViewById(R.id.fab), "Starting new section", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Starts the service and binds the activity to it.
     */
    private void startAndBindService() {
        serviceIntent = new Intent(this, GpxService.class);
        // Start the service in case it isn't already running
        startService(serviceIntent);
        // Now bind to service
        bindService(serviceIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(gpsServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_gpx_logger, menu);
        // set icons
        toolbar.setNavigationIcon(R.drawable.ic_sattellite_hdpi);
        // display items according to current recording state
        setRecordingState(isRecording); // make sure UI elements match state
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_start:
                startRecording();
                break;
            case R.id.action_stop:
                stopRecording();
                break;
            case R.id.action_photo:
                takePhoto();
                break;
            case R.id.action_poi:
                addPointOfInterest();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPointOfInterest() {
        //TODO: add current position with comment to track
    }

    private void takePhoto() {
        //TODO: open photointent and store file with geotag
        
    }

    private void stopRecording() {
        // stop recording
        boundService.stopRecording();
        setRecordingState(false);
        Intent intent = new Intent(this, UploadGpxActivity.class);
        startActivity(intent);
        //TODO: send track as bundle to upload activity
        //TODO: allow user to finalize rating, replace + button with save icon
        //TODO: on save icon click, open gpx uploadview
    }


    private void setRecordingState(boolean isRecording) {
        this.isRecording = isRecording;
        if(menu != null) {
            if (isRecording) {
                // make sure the adapter has a track
                track = boundService.getTrack();
                sectionAdapter.setTrack(track);
                // display previously hidden elements
                menu.findItem(R.id.action_photo).setVisible(true);
                menu.findItem(R.id.action_stop).setVisible(true);
                menu.findItem(R.id.action_poi).setVisible(true);
                // hide start recording item
                menu.findItem(R.id.action_start).setVisible(false);
            } else {
                // hide previously visible elements
                menu.findItem(R.id.action_photo).setVisible(false);
                menu.findItem(R.id.action_stop).setVisible(false);
                menu.findItem(R.id.action_poi).setVisible(false);
                // hide start recording item
                menu.findItem(R.id.action_start).setVisible(true);
            }
        }
    }

    private void startRecording() {
        // get track from service
        Log.d(TAG, "Start recording a new track.");
        track = boundService.recordTrack();
        setRecordingState(true);
        sectionAdapter.notifyDataSetChanged();
        // TODO: show some kind of confirmation or fancy animation
    }

    private void startNewSection() {
        track.startNewSection();
        sectionAdapter.notifyDataSetChanged();
    }


    @Override
    public void updateLocation(Location location) {
        if(sectionAdapter != null) {
            sectionAdapter.notifyDataSetChanged();
        }
    }
}
