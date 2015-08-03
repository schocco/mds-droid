package info.muni_scale.mdsdroid.gpx;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.muni_scale.mdsdroid.R;
import info.muni_scale.mdsdroid.tracks.Track;
import info.muni_scale.mdsdroid.tracks.TrackPoint;
import info.muni_scale.mdsdroid.tracks.TrackSection;

/**
 * The type Gpx file writer.
 */
public class GpxFileWriter {

    /**
     * The constant UTF_8.
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * The constant GPX.
     */
    public static final String GPX = "gpx";
    /**
     * The constant METADATA.
     */
    public static final String METADATA = "metadata";
    /**
     * The constant NAME.
     */
    public static final String NAME = "name";
    /**
     * The constant DESCRIPTION.
     */
    public static final String DESCRIPTION = "description";
    /**
     * The constant AUTHOR.
     */
    public static final String AUTHOR = "author";
    /**
     * The constant GPX_VERSION_STR.
     */
    public static final String GPX_VERSION_STR = "version";
    /**
     * The constant GPX_VERSION.
     */
    public static final String GPX_VERSION = "1.1";
    /**
     * The constant TRKSEG.
     */
    public static final String TRKSEG = "trkseg";
    /**
     * The constant DESC.
     */
    public static final String DESC = "desc";
    /**
     * The constant TYPE.
     */
    public static final String TYPE = "type";
    /**
     * The constant TRKPT.
     */
    public static final String TRKPT = "trkpt";
    /**
     * The constant LAT.
     */
    public static final String LAT = "lat";
    /**
     * The constant LON.
     */
    public static final String LON = "lon";
    /**
     * The constant ELE.
     */
    public static final String ELE = "ele";
    /**
     * Track to be written to filesystem.
     */
    private Track track;
    private static final String TAG = GpxFileWriter.class.getSimpleName();
    private static final String APP_DIR = "muni-scale";
    private Context context;

    /**
     * Instantiates a new Gpx file writer.
     *
     * @param track the track
     * @param context the context
     */
    public GpxFileWriter(Track track, Context context) {
        this.track = track;
        this.context = context;
    }

    /**
     * Indicates an issue during the gpx writing process.
     */
    public static class GpxWriterException extends IOException {

        /**
         * Instantiates a new Gpx writer exception.
         *
         * @param message the message
         * @param e the e
         */
        public GpxWriterException(String message, Throwable e) {
            super(message, e);
        }
    }

    /**
     * Write file to external android storage location..
     *
     * @return the boolean
     * @throws GpxWriterException when access to storage not possible or when serialization fails
     */
    public File writeFile() throws GpxWriterException {
            OutputStream output = null;
            File gpxFile;
            try {
                gpxFile = getStoragePath(track.getName());
                output = new FileOutputStream(gpxFile);
                writeGpx(output);
                return gpxFile;
            } catch (FileNotFoundException e) {
                String msg = context.getString(R.string.error_gpx_file_creation);
                Log.e(TAG, msg, e);
                throw new GpxWriterException(msg, e);
            } catch (IOException e) {
                String msg = context.getString(R.string.error_gpx_file_serialisation);
                Log.e(TAG, msg, e);
                throw new GpxWriterException(msg, e);
            } finally {
                //XXX: try-with-resources requires android SDK >= 19
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }

            }
    }

    /**
     * Uses an XmlSerializer to write segments and points to the provided stream.
     *
     * @param outputStream stream to be filled with xml
     * @throws IOException
     */
    private void writeGpx(OutputStream outputStream) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, UTF_8);
        serializer.startDocument(UTF_8, true);
        serializer.startTag("", GPX).attribute("", GPX_VERSION_STR, GPX_VERSION);
        serializer.startTag("", METADATA);
        serializer.startTag("", NAME).text(track.getName()).endTag("", NAME);
        serializer.startTag("", DESCRIPTION).text(track.getDescription()).endTag("", DESCRIPTION);
        serializer.startTag("", AUTHOR).text("author placeholder").endTag("", AUTHOR); //TODO: get author name from app settings
        serializer.endTag("", METADATA);
        //trail contents
        for (TrackSection section : track.getSections()) {
            serializer.startTag("", TRKSEG);
            if(section.getComment() != null) {
                serializer.startTag("", DESC).text(section.getComment()).endTag("", DESC);
            }
            serializer.startTag("", TYPE).text(String.valueOf(section.getDifficulty())).endTag("", TYPE);
            for (TrackPoint point : section.getPoints()) {
                serializer.startTag("", TRKPT);
                serializer.attribute("", LAT, point.getLat());
                serializer.attribute("", LON, point.getLon());
                serializer.attribute("", ELE, point.getAltitude());
                serializer.endTag("", TRKPT);
            }
            serializer.endTag("", TRKSEG);
        }
        serializer.endTag("", GPX);
        serializer.endDocument();
    }

    /**
     * Checks if an external storage is present, and if there are write permissions for the documents folder.
     *
     * @return the boolean
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File publicDir = Environment.getExternalStorageDirectory();
            return publicDir.canWrite();
        }
        return false;
    }

    /**
     * Gets a handle to a gpx file in the external/internal storage dir.
     * Creates path to file if not present.
     *
     * @param trackName the track name
     * @return the storage file
     */
    public File getStoragePath(String trackName) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_km").format(new Date());
        String fileName = String.format("%s_%s.gpx", timestamp, track.getName());
        File file;
        if(isExternalStorageWritable()) {
            // not working on KitKat and later versions
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), APP_DIR);
            Log.i(TAG, "Writing file to external public storage: " + file.getAbsolutePath());
        } else {
            file = new File(context.getFilesDir(), APP_DIR);
            Log.i(TAG, "Writing file to internal storage: " + file.getAbsolutePath());
        }
        file.mkdirs();
        return new File(file, fileName);

    }

}
