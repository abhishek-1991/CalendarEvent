package com.example.sanjana.calendarevents;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getCalenderEvents(View view) {

        Context context = this;
        Cursor cursor =context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
                new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);
        cursor.moveToFirst();
        // fetching calendars name
        Date currTime = Calendar.getInstance().getTime();
        Log.d("Today date", String.valueOf(currTime));
        Long currEpoch = System.currentTimeMillis();
        Long delta = Long.valueOf(86400000);
        Long min = currEpoch - delta;
        Long max = currEpoch + delta;
        Log.d("Today date currtime", String.valueOf(currEpoch));
        String CNames[] = new String[cursor.getCount()];
        ArrayList<ArrayList<String>> eventsList = new ArrayList<>();
        for (int i = 0; i < CNames.length; i++) {
            ArrayList<String> currEvent = new ArrayList<>();
            String eventTitle = cursor.getString(1);
            String eventStartDate = cursor.getString(3);
            String eventEndDate = cursor.getString(4);
            String eventDescription = cursor.getString(2);
            String eventLocation = cursor.getString(5);
//            Log.d("event_title",eventTitle);
//            Log.d("eventStartDate", eventStartDate);
//            Log.d("eventLocation",eventLocation);
            if(Long.parseLong(eventStartDate) < max && Long.parseLong(eventStartDate) > min) {
                Log.d("Today's event", "found an event");
                Log.d("Today's event title",eventTitle);
                currEvent.add(eventTitle);
                currEvent.add(eventStartDate);
                currEvent.add(eventLocation);
                String loc = eventLocation.replace(' ','+');
                Log.d("New location",loc);
                eventsList.add(currEvent);
            }
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("Events list size", String.valueOf(eventsList.size()));

        for(List<String> str1 : eventsList){
            Log.d("Size of inner lists", String.valueOf(str1));
        }
    }

    public void getDistance(View view) {
        var directions = new GDirections();



    }
}
