package com.example.abhishek.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import org.json.*;

public class MainActivity extends AppCompatActivity{
    public ArrayList<ArrayList<String>> eventsList = new ArrayList<>();
    private LocationManager locmgr;
    Location location_net,location_gps,location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
                new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, "dtstart ASC");
        cursor.moveToFirst();
        // fetching calendars name
        Date currTime = Calendar.getInstance().getTime();
        Log.d("Today date", String.valueOf(currTime));
        //Long currEpoch = System.currentTimeMillis();
        Long currEpoch = System.currentTimeMillis()/1000;
        Long delta = Long.valueOf(86400);
        Long min = currEpoch + 2 * delta;
        Long max = currEpoch + 6 * delta;
        Log.d("Today date currtime", String.valueOf(currEpoch));
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            ArrayList<String> currEvent = new ArrayList<>();
            String eventTitle = cursor.getString(1);
            String eventStartDate = String.valueOf(Long.parseLong(cursor.getString(3))/1000);
//            String eventEndDate = cursor.getString(4);
//            String eventDescription = cursor.getString(2);
            String eventLocation = cursor.getString(5);
           /* Log.d("ASRI1",eventStartDate);
            Log.d("ASRI2",String.valueOf(max));
            Log.d("ASRI3",String.valueOf(min));*/
            if (Long.parseLong(eventStartDate) < max && Long.parseLong(eventStartDate) > min && eventLocation.length() != 0) {
                String loc = eventLocation.replace(' ', '+');
                currEvent.add(eventTitle);
                currEvent.add(eventStartDate);
                currEvent.add(loc);
                eventsList.add(currEvent);
            }
            cursor.moveToNext();
        }
        cursor.close();

        /*********************************************/

        HttpURLConnection urlConnection = null;

        if(eventsList.size() == 0){
         Toast.makeText(this, "No events found to schedule the travel.",
                 Toast.LENGTH_LONG).show();
//            Log.d("ANALWAY","123");
         }
         else if(eventsList.get(0).get(2).length() == 0){
             Toast.makeText(this, "No location saved for this event.",
                     Toast.LENGTH_LONG).show();
//           Log.d("ANALWAY","123");
         }
         else{

    //        Log.d("ASRI","Count: "+eventsList.size());
    //        Log.d("ASRI","Title: "+eventsList.get(0).get(0));
    //        Log.d("ASRI","Time: "+eventsList.get(0).get(1));
    //        Log.d("ASRI","Location: "+eventsList.get(2).get(0));
    //        Log.d("ASRI","Location: "+eventsList.get(0).get(2));
    //        int tot_len = 0;
            String curloc=null;
            try {
                /*try {
                    locmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
                    location_net = locmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    location_gps = locmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location_gps == null) {
                        if (location_net == null) {
                            Toast.makeText(this, "Location not Available", Toast.LENGTH_SHORT).show();
                        } else {
                            location = location_net;
                        }
                    } else {
                        location = location_gps;
                    }
                    curloc = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                }
                catch(SecurityException e){e.printStackTrace();}*/
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String Url = "https://maps.googleapis.com/maps/api/directions/json?origin=Kings+Court+Raleigh&destination=" + eventsList.get(0).get(2) + "&key=AIzaSyBkLli9Te539Uob6HodeOgR-bD83JCrRcg&mode=transit&arrival_time=" + eventsList.get(0).get(1);
                //String Url = "https://maps.googleapis.com/maps/api/directions/json?origin=Kings+Court+Raleigh&destination=" + eventsList.get(0).get(2) + "&key=AIzaSyBkLli9Te539Uob6HodeOgR-bD83JCrRcg&mode=transit&arrival_time=";

                URL url = new URL(Url);
                Log.d("URL ", String.valueOf(Url));
                urlConnection = (HttpURLConnection) url.openConnection();
                //Log.d("URL ", String.valueOf(urlConnection));
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bR = new BufferedReader(new InputStreamReader(in));
                String line = "";

                StringBuilder responseStrBuilder = new StringBuilder();
                while ((line = bR.readLine()) != null) {

                    responseStrBuilder.append(line);
                }
                in.close();

                JSONObject result = new JSONObject(responseStrBuilder.toString());

                List<JSONObject> lst = getTravelDetails(result);
                Map<String,List<String>> map = processingJSON(lst);

                Log.d("ASRI", String.valueOf(lst));
                TextView showTitle = findViewById(R.id.showTitle);
                String temp1 = eventsList.get(0).get(2);
                temp1 = temp1.replace("+"," ");
                showTitle.setText(eventsList.get(0).get(0));
                TextView showTime = findViewById(R.id.showTime);
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(eventsList.get(0).get(1)) * 1000L);
                android.text.format.DateFormat df = new android.text.format.DateFormat();
                showTime.setText(df.format("dd/MM/yyyy hh:mm:ss", cal).toString());
                TextView showLoc = findViewById(R.id.showLoc);
                showLoc.setText(temp1);


                TextView showHops = findViewById(R.id.showHops);
                showHops.setText("# Hops: "+map.get("arrivalStop").size());

                TextView startTime = findViewById(R.id.startTime);
                startTime.setText(map.get("arrivalTime").get(0));


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }





    }

    public void showDetails(View view) {
        TextView deptStop = findViewById(R.id.deptStop);


    }

    public static Map<String,List<String>> processingJSON(List<JSONObject> jsonObjectList){
        Map<String,List<String>> map = new HashMap<>();
        String destinationName,destinationArrivalTime,sourceName,sourceDepartureTime,totalTime,headSign;
        String arrivalStop,arrivalTime, busName, busNumber, busDetails;
        for(int i=0;i<jsonObjectList.size();i++){
            JSONObject object = jsonObjectList.get(i);
            try {
                arrivalStop = object.getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name");
                arrivalTime = object.getJSONObject("transit_details").getJSONObject("arrival_time").getString("text");
                totalTime = object.getJSONObject("duration").getString("text");
                destinationName = object.getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");
                destinationArrivalTime = object.getJSONObject("transit_details").getJSONObject("departure_time").getString("text");
                headSign = object.getJSONObject("transit_details").getString("headsign");
                busName = object.getJSONObject("transit_details").getJSONObject("line").getJSONObject("vehicle").getString("name");
                busNumber = object.getJSONObject("transit_details").getJSONObject("line").getString("short_name");
                busDetails = busName+busNumber;

                if(!map.containsKey("arrivalStop")){
                    map.put("arrivalStop",new ArrayList<String>());
                }
                map.get("arrivalStop").add(arrivalStop);

                if(!map.containsKey("arrivalTime")){
                    map.put("arrivalTime",new ArrayList<String>());
                }
                map.get("arrivalTime").add(arrivalTime);

                if(!map.containsKey("totalTime")){
                    map.put("totalTime",new ArrayList<String>());
                }
                map.get("totalTime").add(totalTime);

                if(!map.containsKey("destinationName")){
                    map.put("destinationName",new ArrayList<String>());
                }
                map.get("destinationName").add(destinationName);

                if(!map.containsKey("destinationArrivalTime")){
                    map.put("destinationArrivalTime",new ArrayList<String>());
                }
                map.get("destinationArrivalTime").add(destinationArrivalTime);

                if(!map.containsKey("headSign")){
                    map.put("headSign",new ArrayList<String>());
                }
                map.get("headSign").add(headSign);

                if(!map.containsKey("busDetails")){
                    map.put("busDetails",new ArrayList<String>());
                }
                map.get("busDetails").add(busDetails);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    public static List<JSONObject> getTravelDetails(JSONObject jsonObject){
        List<JSONObject> result = new ArrayList<>();
        try{
            Iterator<String> keys = (Iterator<String>) jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if(key.equals("routes")){
                    JSONArray levelOne = (JSONArray)jsonObject.get(key);
                    JSONObject jsonObject1 = levelOne.getJSONObject(0);
                    JSONArray legsArray = (JSONArray) jsonObject1.get("legs");
                    JSONObject legsObject = legsArray.getJSONObject(0);
                    JSONArray stepsArray = (JSONArray) legsObject.get("steps");
                    for(int j=0;j<stepsArray.length();j++){
                        JSONObject stepsObject2 = stepsArray.getJSONObject(j);
                        if(stepsObject2.get("travel_mode").equals("TRANSIT")){
                            result.add(stepsObject2);
                        }
                    }
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /*private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyBkLli9Te539Uob6HodeOgR-bD83JCrRcg")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }*/
}
