package com.example.surrme;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

@SuppressLint("NewApi")
public class MainClass extends Activity implements OnCheckedChangeListener{
EditText et;
RadioGroup select;  //radio group which has near or specific radio button
Intent a;
Bundle basket;
	Spinner spinner;
	String selected;
	String[] placetypes = {"airport","amusement_park","atm","bakery","bank","bar","beauty_salon","book_store","bus_station","cafe","car_rental","car_dealer","car_repair","car_wash","church","clothing_store","dentist","department_store","doctor","electrician","electronics_store","finance","fire_station","food","furniture_store","gas_station","grocery_or_supermarket","gym","hair_care","hardware_store","health","hindu_temple","home_goods_store","hospital","insurance_agency","jewelry_store","laundry","lawyer","library","liquor_store","local_government_office","mosque","movie_theater","museum","night_club","painter","park","parking","pharmacy","place_of_worship","physiotherapist","plumber","police","post_office","restaurant","school","shoe_store","shopping_mall","spa","stadium","taxi_stand","train_station","travel_agency","university","zoo"};
	String[] actions = new String[] {
			"Bookmark",
			"Subscribe",
			"Share"
		};
	  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mainxml);
	        
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, actions);
	        
	        
	        /** Enabling dropdown list navigation for the action bar */
	        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        
	        /** Defining Navigation listener */
	        ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
				
				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					Toast.makeText(getBaseContext(), "You selected : " + actions[itemPosition]  , Toast.LENGTH_SHORT).show();
					return false;
				}
			};
		
			/** Setting dropdown items and item navigation listener for the actionbar */
			getActionBar().setListNavigationCallbacks(adapter, navigationListener);        
	        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainClass.this,android.R.layout.simple_spinner_item,placetypes);
	       initialize();
	       et = (EditText)findViewById(R.id.etSpecific);
	       select = (RadioGroup)findViewById(R.id.radioGroup1);
	       select.setOnCheckedChangeListener(this);
	
	        spinner = (Spinner)findViewById(R.id.spinner1);
	        spinner.setAdapter(adapter1);
	     
	        spinner.setSelection(0);

	        // Post to avoid initial invocation
	        spinner.post(new Runnable() {
	          @Override public void run() {
	            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	              @Override
	              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	                // Only called when the user changes the selection
	            		selected = spinner.getSelectedItem().toString();
	            	 
	  	    		basket.putString("key",selected);
	  	    		a.putExtras(basket);
	  	    		startActivity(a);
	              }

	              @Override
	              public void onNothingSelected(AdapterView<?> parent) {
	              }
	            });
	          }
	        });
	 
}

	private void initialize() {
		// TODO Auto-generated method stub
		 a = new Intent(MainClass.this,MainActivity.class);
		 basket = new Bundle();
		 basket.putString("key1","near");
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId)
		{
		case R.id.near:
			et.setVisibility(View.INVISIBLE);
			basket.putString("key1","near");
			break;
		case R.id.specific:
			et.setVisibility(View.VISIBLE);
			basket.putString("key1","specific");
			readEditText();
			break;
		}
		
	}

	private void readEditText() {
		// TODO Auto-generated method stub
		basket.putString("key2",et.getText().toString());
	}
}