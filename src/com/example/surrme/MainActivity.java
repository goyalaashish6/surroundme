package com.example.surrme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class MainActivity extends Activity {
 
	// String GotPlaceType gets the type of place from MainClass through Intents
	String GotPlaceType;
	String radioButtonPressed;
	String specificLocation;
	double lati,longi;
	// flag for Internet connection status
    Boolean isInternetPresent = false;
 
    // Connection detector class
    Connection cd;
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Google Places
    GooglePlaces googlePlaces;
 
    // Places List
    PlacesList nearPlaces;
 
    // GPS Location
    GPSTracker gps;
 
    // Button
    Button btnShowOnMap;
 
    // Progress dialog
    ProgressDialog pDialog;
 
    // Places Listview
    ListView lv;
 
    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
 
    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        Bundle gotBasket = getIntent().getExtras();
        GotPlaceType = gotBasket.getString("key");
        radioButtonPressed = gotBasket.getString("key1");
        specificLocation = gotBasket.getString("key2");
        
        cd = new Connection(getApplicationContext());
 
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
 
        // creating GPS Class object
        gps = new GPSTracker(this);
 
        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            alert.showAlertDialog(MainActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }
 
        // Getting listview
        lv = (ListView) findViewById(R.id.list);
 
      
        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        new LoadPlaces().execute();
 

        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);
 
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
    }
 
    /**
     * Background Async Task to Load Google places
     * */
    class LoadPlaces extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();
 
            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                //
                String types = GotPlaceType;  // Listing places 
 
                // Radius in meters - increase this value if you don't find any places
                double radius = 1000; // 1000 meters 
 
                // get nearest places
                if(radioButtonPressed.compareTo("near")==0)
                {
                	lati = gps.getLatitude();
                	longi = gps.getLongitude();
                }
                else if (radioButtonPressed.compareTo("specific")==0)
                {
                	Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
            		List<Address> address =new ArrayList<Address>();
            		try {
            			address = geocoder.getFromLocationName(specificLocation, 1);
            		} catch (IOException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
            		if (address.size()>0)
            		{
            			lati = address.get(0).getLatitude();
            			
            			longi = address.get(0).getLongitude();
            			
            		}
            	}
                
                nearPlaces = googlePlaces.search(lati,
                        longi, radius, types);
 
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;
 
                    // Check for all possible status
                    if(status.equals("OK")){
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Place p : nearPlaces.results) {
                                HashMap<String, String> map = new HashMap<String, String>();
 
                                // Place reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.reference);
 
                                // Place name
                                map.put(KEY_NAME, p.name);
 
                                // adding HashMap to ArrayList
                                placesListItems.add(map);
                            }
                            // list adapter
                            ListAdapter adapter = new SimpleAdapter(MainActivity.this, placesListItems,
                                    R.layout.list_item,
                                    new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
                                            R.id.reference, R.id.name });
 
                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                        // Zero results found
                        alert.showAlertDialog(MainActivity.this, "Near Places",
                                "Sorry no places found. Try to change the types of places",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });
 
        }
 
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
 
}
