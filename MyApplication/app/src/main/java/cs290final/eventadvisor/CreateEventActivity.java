package cs290final.eventadvisor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Locale;

import cs290final.eventadvisor.backend.CreateEvents;

/**
 * Created by emilymeng on 4/16/17.
 */

public class CreateEventActivity extends AppCompatActivity {
    static EditText mStartTime;
    static EditText mEndTime;
    static EditText mDate;
    static Calendar myCalendar = Calendar.getInstance();
    private static final String TAG = "CreateEventActivity";


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
            return new TimePickerDialog(getActivity(), this, hour, minute,
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
                mStartTime.setText("Start Time :"+hourOfDay+":"+fMinute+":00");
            }
            if(startOrEnd.equals("end")){
                mEndTime.setText("End Time: "+hourOfDay+":"+fMinute+":00");
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

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            mDate.setText(sdf.format(myCalendar.getTime()));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mDate=(EditText) findViewById(R.id.editDate);
        mStartTime=(EditText) findViewById(R.id.editStartTime);
        mEndTime=(EditText) findViewById(R.id.editEndTime);

    }
        private void setupSearchBar() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                place.getLatLng().longitude;

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                System.out.println("searchbar error");
            }
        });
    }
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
    public void createEventAction(View view){
        System.out.println("Create Event Activity");
        new CreateEvents().execute();
    }



}
