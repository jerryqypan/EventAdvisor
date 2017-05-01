package cs290final.eventadvisor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import cs290final.eventadvisor.backend.CreateEvents;

/**
 * @author Jerry Pan
 */

public class CreateEventActivity extends AppCompatActivity {
    /**
     * Log output
     */
    private static final String TAG = "CREATE_EVENT_ACTIVITY";
    /**
     *
     */
    protected static final String STATE_SELECTED_LATITUDE = "state_selected_latitude";
    /**
     *
     */
    protected static final String STATE_SELECTED_LONGITUDE = "state_selected_longitude";
    /**
     * Button that adds images to the event
     */
    private Button cameraButton;
    /**
     * Input field for start time
     */
    private EditText mStartTime;
    /**
     * Input field for end time
     */
    private EditText mEndTime;
    /**
     * Input field for date
     */
    private EditText mDate;
    /**
     * Input field for title
     */
    private EditText mTitle;
    /**
     * Input field for description
     */
    private EditText mDescription;
    /**
     * Input field for location
     */
    private EditText mLocation;
    /**
     * The current Firebase User
     */
    private String mUser;
    /**
     * The coordinates of the place selected
     */
    private String mCoordinates;
    /**
     * Request code for selecting location
     */
    private static final int REQUEST_SELECT_PLACE = 1234;
    /**
     * Request code for camera
     */
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * Request code for accessing files
     */
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    /**
     * Request code for opening gallery
     */
    private static final int REQUEST_OPEN_GALLERY = 3;
    /**
     * Calendar object for Datepicker dialog fragment
     */
    private static Calendar myCalendar = Calendar.getInstance();
    /**
     * File path of photo selected by user
     */
    private String mCurrentPhotoPath;


    @Override
    /**
     * Initializes the buttons and input fields as well as app permissions
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_create);
        mDate=(EditText) findViewById(R.id.editDate);
        mStartTime=(EditText) findViewById(R.id.editStartTime);
        mEndTime=(EditText) findViewById(R.id.editEndTime);
        mTitle=(EditText) findViewById(R.id.editEventName);
        mDescription= (EditText) findViewById(R.id.editDescription);
        mLocation= (EditText) findViewById(R.id.editLocation);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        mCoordinates = i.getExtras().getString("latitude")+","+i.getExtras().getString("longitude");
        mUser = i.getExtras().getString("uid");
        mLocation.setText("Current Location");
        checkIfCameraSupported();
    }

    /**
     * Checks camera permissions
     */
    private void checkIfCameraSupported() {
        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if (hasCamera) {
            cameraButton.setEnabled(true);
        } else {
            cameraButton.setEnabled(false);
        }
    }
    /**
     * Checks if writing to storage is allowed
     * @return true if writing to storage is allowed, false ow
     */
    private boolean checkIfWriteToStorageAllowed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    /**
     * Dialog for choosing to select photo from gallery or take a photo from camera
     * @param v the add image button
     */
    public void showSelectPictureDialog(View v) {
        boolean canWriteStorage = checkIfWriteToStorageAllowed();
        if (!canWriteStorage) {
            return;
        }
        final String[] items = {"Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    startCameraButton();
                } else if (items[item].equals("Choose from Gallery")) {
                    selectPictureFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     *  Starts the camera to take a photo
     */
    private void startCameraButton() {
        boolean hasPermission = checkIfWriteToStorageAllowed();
        if (!hasPermission) {
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "Error occurred while creating the File for camera");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cs290final.eventadvisor.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "This device cannot support this feature", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the image file created by the camera to the system
     * @return returns the image file
     * @throws IOException throws exception if writing to system fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Adds all the images into the gallery displayed
     */
    private void addPicToGallery() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, mCurrentPhotoPath);
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Allows user to select the image from the gallery
     */
    private void selectPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Show only images, no videos or anything else
        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_OPEN_GALLERY);
    }

    /**
     * @author Jerry
     * Copied from https://developer.android.com/guide/topics/ui/controls/pickers.html
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        /**
         * Denoting if fragment is for either start or end time
         */
        private String startOrEnd="";

        @Override
        /**
         * Initializes the time picker dialog inside the fragment
         */
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            this.startOrEnd = getArguments().getString("startOrEnd");
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        /**
         *  Actions after user selects the time
         * @param view The TimePicker that is displayed
         * @param hourOfDay Hour selected
         * @param minute Minute selected
         */
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            CreateEventActivity createEventActivity = (CreateEventActivity) getActivity();
            String fMinute = "";
            if(minute<10){
                fMinute="0"+minute;
            }else{
                fMinute=Integer.toString(minute);
            }
            if(startOrEnd.equals("start")){
                createEventActivity.mStartTime.setText(""+hourOfDay+":"+fMinute);
            }
            if(startOrEnd.equals("end")){
                createEventActivity.mEndTime.setText(""+hourOfDay+":"+fMinute);
            }else{
                Log.d(TAG,startOrEnd);
            }
        }
    }
    /**
     * @author Jerry
     * Copied from https://developer.android.com/guide/topics/ui/controls/pickers.html
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        /**
         * Initializes the date picker dialog inside the fragment
         */
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar, this, year, month, day);
            // Create a new instance of DatePickerDialog and return it
            picker.getDatePicker().setMinDate(c.getTime().getTime());
            return picker;
        }
        /**
         *  Actions after user selects the time
         * @param view The TimePicker that is displayed
         * @param day The day selected
         * @param year The year selected
         * @param month The month selected
         */
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            CreateEventActivity createEventActivity = (CreateEventActivity) getActivity();
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            createEventActivity.mDate.setText(sdf.format(myCalendar.getTime()));
        }
    }

    /**
     * Starts the date picker fragment
     * @param v The date input field
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Starts the end time fragment
     * @param v The end time input field
     */
    public void showEndTimePickerDialog(View v) {
        Bundle args = new Bundle();
        args.putString("startOrEnd", "end");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Starts the start time fragment
     * @param v The start time input field
     */
    public void showStartTimePickerDialog(View v) {
        Bundle args = new Bundle();
        args.putString("startOrEnd", "start");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Starts another thread to post information to server
     * @param view Create event button
     */
    public void createEventAction(View view) {
        if (mDate.getText().toString().length() == 0 || mStartTime.getText().toString().length() == 0 || mEndTime.getText().toString().length() == 0 || mTitle.getText().toString().length()==0 || mDescription.getText().toString().length()==0){
            Toast.makeText(this,"Please fill all of the form!",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Create Event Activity");
        String date = mDate.getText().toString();
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String startTime = mStartTime.getText().toString();
        String endTime = mEndTime.getText().toString();
        String lat = mCoordinates.split(",")[0];
        String lon = mCoordinates.split(",")[1];
        String place = mLocation.getText().toString();
        new CreateEvents(CreateEventActivity.this).execute(title,date,description,startTime,endTime,lat,lon,place,mUser,mCurrentPhotoPath);
    }

    /**
     * Starts the location selector fragment
     * @param view The location input field
     */
    public void showLocationSearch(View view){
        try {       //opens google api search bar
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_OVERLAY).build(CreateEventActivity.this);
            startActivityForResult(intent, REQUEST_SELECT_PLACE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback for CreateEvent to use after executing to go back to MapActivity
     * @param latitude latitude to send back to MapActivity
     * @param longitude longitude to send back to MapActivity
     */
    public void createEventsCallBack(String latitude, String longitude) {
        Intent intent = new Intent();
        intent.putExtra(STATE_SELECTED_LATITUDE, Double.parseDouble(latitude));
        intent.putExtra(STATE_SELECTED_LONGITUDE, Double.parseDouble(longitude));
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    /**
     * Processes location, camera actvities
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {  //search bar place result
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mCoordinates = ""+place.getLatLng().latitude+","+place.getLatLng().longitude;
                mLocation.setText(place.getName());
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(mCurrentPhotoPath)));
                addPicToGallery();
                final ImageView imgView = new ImageView(this);
                Picasso.with(this).load(new File(mCurrentPhotoPath)).into(imgView, new com.squareup.picasso.Callback() { //need to find a better solution
                    @Override
                    public void onSuccess() {
                        cameraButton.setBackground(imgView.getDrawable());
                        cameraButton.setText("");
                    }

                    @Override
                    public void onError() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Can't use this image!", Toast.LENGTH_SHORT).show();
            }
            if (data != null) {
                Uri uri = data.getData();
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                mCurrentPhotoPath = picturePath;
                final ImageView imgView = new ImageView(this);
                Picasso.with(this).load(new File(mCurrentPhotoPath)).into(imgView, new com.squareup.picasso.Callback() { //need to find a better solution
                            @Override
                            public void onSuccess() {
                                cameraButton.setBackground(imgView.getDrawable());
                                cameraButton.setText("");
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Log.d(TAG, "cursor picture path " + picturePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    /**
     * Executes showSelectPictureDialog if permissions were granted
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showSelectPictureDialog(null);
            }
        }
    }


}