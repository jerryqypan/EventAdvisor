package cs290final.eventadvisor;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import cs290final.eventadvisor.backend.CreateEvents;

/**
 * Created by emilymeng on 4/16/17.
 */

public class CreateEventActivity extends AppCompatActivity {
    protected static final String STATE_SELECTED_LATITUDE = "state_selected_latitude";
    protected static final String STATE_SELECTED_LONGITUDE = "state_selected_longitude";
    private static EditText mStartTime;
    private static EditText mEndTime;
    private static EditText mDate;
    private static EditText mTitle;
    private static EditText mDescription;
    private static EditText mLocation;
    private Button cameraButton;
    private String mUser;
  
  public String getCoordinates() {
        return mCoordinates;
    }

    private String mCoordinates;

    private static final int REQUEST_SELECT_PLACE = 1234;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Calendar myCalendar = Calendar.getInstance();
    private static final String TAG = "CreateEventActivity";
    private String mCurrentPhotoPath;

    @Override
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
        mLocation.setText(mCoordinates);

    }

    private void checkIfCameraSupported() {
        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if (hasCamera) {
            cameraButton.setEnabled(true);
        } else {
            cameraButton.setEnabled(false);
        }
    }

    private void checkIfWriteToStoreAllowed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
    }

    public void startCameraButtonAction(View view) {
        checkIfCameraSupported();
        checkIfWriteToStoreAllowed();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Error occurred while creating the File for camera");
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

    private void addPicToGallery() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, mCurrentPhotoPath);
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private String startOrEnd="";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            this.startOrEnd = getArguments().getString("startOrEnd");
            System.out.println("STARTOREND"+startOrEnd);
            System.out.println("TEST"+ getArguments().getString("startorEnd"));
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String fMinute = "";
            if(minute<10){
                fMinute="0"+minute;
            }else{
                fMinute=Integer.toString(minute);
            }
            if(startOrEnd.equals("start")){
                mStartTime.setText(""+hourOfDay+":"+fMinute+":00");
            }
            if(startOrEnd.equals("end")){
                mEndTime.setText(""+hourOfDay+":"+fMinute+":00");
            }else{
                Log.d(TAG,startOrEnd);
            }
            // Do something with the time chosen by the user
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            mDate.setText(sdf.format(myCalendar.getTime()));

        }
    }



//        private void setupSearchBar() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                //place.getLatLng().longitude;
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                System.out.println("searchbar error");
//            }
//        });
//    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public void showEndTimePickerDialog(View v) {
        Bundle args = new Bundle();
        args.putString("startOrEnd", "end");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public void showStartTimePickerDialog(View v) {
        Bundle args = new Bundle();
        args.putString("startOrEnd", "start");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public void createEventAction(View view) {
        if (mDate.getText().toString().length() == 0 || mStartTime.getText().toString().length() == 0 || mEndTime.getText().toString().length() == 0 || mTitle.getText().toString().length()==0 || mDescription.getText().toString().length()==0){
            Toast.makeText(this,"Please fill all of the form!",Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("Create Event Activity");
        String date = mDate.getText().toString();
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String startTime = mStartTime.getText().toString();
        String endTime = mEndTime.getText().toString();
        String location = mLocation.getText().toString();
        String lat = location.split(",")[0];
        String lon = location.split(",")[1];
        new CreateEvents(CreateEventActivity.this).execute(title,date,description,startTime,endTime,lat,lon,mUser);
    }
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

    public void createEventsCallBack(String latitude, String longitude) {
        Intent intent = new Intent();
        intent.putExtra(STATE_SELECTED_LATITUDE, Double.parseDouble(latitude));
        intent.putExtra(STATE_SELECTED_LONGITUDE, Double.parseDouble(longitude));
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {  //search bar place result
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String coordinates = ""+place.getLatLng().latitude+","+place.getLatLng().longitude;
                mLocation.setText(coordinates);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(mCurrentPhotoPath)));
//                MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "tes" , "sdf");
                addPicToGallery();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
