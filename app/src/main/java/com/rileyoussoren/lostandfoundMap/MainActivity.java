package com.rileyoussoren.lostandfoundMap;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    //setting global variables for use
    Button createButton;
    Button showButton;
    Button saveButton;
    Button removeButton;
    Button mapButton;
    Button getLocationButton;


    EditText nameEntry;
    EditText phoneEntry;
    EditText descriptionEntry;
    EditText dateEntry;
    EditText editText;


    TextView radioText;
    TextView nameTitle;
    TextView phoneTitle;
    TextView descriptionTitle;
    TextView dateTitle;
    TextView locationTitle;
    TextView spinnerTitle;
    TextView itemDetails;

    RadioGroup radioButtons;
    RadioButton radioSelected;
    RadioButton radioLost;
    RadioButton radioFound;

    Spinner itemsSpinner;

    private DBHandler dbHandler;

    private GoogleMap mMap;

    LatLng locationCoords;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;





    //creates the dropdown for the spinner with all items in the database
    private void loadSpinnerData(){

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<ItemModal> labels = db.readItems();

        List<String> itemsList = new ArrayList<>();

        //the list starts blank as the spinner is set to show item details once the selection has anything in it
        itemsList.add("");

        // a for loop iterates through all items taken from the database and they are added to a list
        for (int loop = 0; loop < labels.size(); loop++){

            itemsList.add(labels.get(loop).getItemName());

        }

        //an adapter is set up for the spinner and the list of items is loaded in
        ArrayAdapter itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsList);
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemsSpinner.setAdapter(itemsAdapter);

    }

    //gets the item details once selected in the spinner
    private void getItemSelected(String selection){

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<ItemModal> labels = db.readItems();

        List<String> itemDetailsList = new ArrayList<>();

        //this for loop iterates through the database items and once it has found a match it pulls all of the different labels attached to the row
        for (int i = 0; i < labels.size(); i++){

            if (selection.equals(labels.get(i).getItemName())){

                itemDetailsList.add(labels.get(i).getItemStatus());
                itemDetailsList.add(labels.get(i).getItemName());
                itemDetailsList.add(labels.get(i).getItemPhone());
                itemDetailsList.add(labels.get(i).getItemDescription());
                itemDetailsList.add(labels.get(i).getItemDate());
                itemDetailsList.add(labels.get(i).getItemLocation());

            }

        }

        StringBuilder builder = new StringBuilder();
        for (String item: itemDetailsList){
            builder.append(item);
            builder.append(" \n");
        }

        itemDetails.setText(builder.toString());

    }

    //this takes all of the items in the database and pulls their location and name to setup markers for the map
    private void makeMarkers(GoogleMap map){

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<ItemModal> labels = db.readItems();

        //this iterates through the data pulled form teh database, it first takes the location data which is a string in the form of 'lat/lng:(#####,#####)' and splits into the 2 numbers
        //which are turned into a latLng for the marker, it then makes the marker with the new location data and the name of the database entry
        for (int i = 0; i < labels.size(); i++){

            String toSplit = labels.get(i).getItemLocation();
            String[] split = toSplit.split("[(,)]");
            double lat = Double.parseDouble(split[1]);
            double lon = Double.parseDouble(split[2]);

            LatLng markCoords = new LatLng(lat,lon);



            map.addMarker(new MarkerOptions()
                            .title(labels.get(i).getItemName())
                            .position(markCoords));

        }

    }

    //waits to see the selection form the autocomplete query, if something has been selected it take the various types of data, and in this case it sets a textbox to show the name of the
    //location and sets a global variable to equal the coordinates. if nothing is selected it cancels the autocomplete operation
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i("TAG", "Place: ${place.getName()}, ${place.getId()}");

                        editText.setText(place.getName());
                        //hiddenText.setText(place.getLatLng().toString());
                        locationCoords = place.getLatLng();
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i("TAG", "User canceled autocomplete");
                }
            });






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing the map fragment to later be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapFragment.getView().setVisibility(View.INVISIBLE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        createButton = findViewById(R.id.createButton);
        showButton = findViewById(R.id.showButton);
        saveButton = findViewById(R.id.saveButton);
        removeButton = findViewById(R.id.removeButton);
        mapButton = findViewById(R.id.mapButton);
        getLocationButton = findViewById(R.id.getLocationButton);


        nameEntry = findViewById(R.id.nameEntry);
        phoneEntry = findViewById(R.id.phoneEntry);
        descriptionEntry = findViewById(R.id.descriptionEntry);
        dateEntry = findViewById(R.id.dateEntry);
        editText = findViewById(R.id.editTextText);


        radioText = findViewById(R.id.radioText);
        nameTitle = findViewById(R.id.nameTitle);
        phoneTitle = findViewById(R.id.phoneTitle);
        descriptionTitle = findViewById(R.id.descriptionTitle);
        dateTitle = findViewById(R.id.dateTitle);
        locationTitle = findViewById(R.id.locationTitle);
        spinnerTitle = findViewById(R.id.spinnerTitle);
        itemDetails = findViewById(R.id.itemDetails);

        radioButtons = findViewById(R.id.radioGroup);
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);

        itemsSpinner = findViewById(R.id.itemsSpinner);

        dbHandler = new DBHandler(MainActivity.this);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createButton.setVisibility(View.INVISIBLE);
                showButton.setVisibility(View.INVISIBLE);
                mapButton.setVisibility(View.INVISIBLE);

                radioText.setVisibility(View.VISIBLE);
                radioButtons.setVisibility(View.VISIBLE);
                radioLost.setVisibility(View.VISIBLE);
                radioFound.setVisibility(View.VISIBLE);
                nameTitle.setVisibility(View.VISIBLE);
                nameEntry.setVisibility(View.VISIBLE);
                phoneTitle.setVisibility(View.VISIBLE);
                phoneEntry.setVisibility(View.VISIBLE);
                descriptionTitle.setVisibility(View.VISIBLE);
                descriptionEntry.setVisibility(View.VISIBLE);
                dateTitle.setVisibility(View.VISIBLE);
                dateEntry.setVisibility(View.VISIBLE);
                locationTitle.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                getLocationButton.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);

                //checks if places has been initialized, if it hasn't it does so with the API key
                if(!Places.isInitialized()){
                    Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

                }

                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // sets the data types to be returned after a selection is made
                        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                        // Start the autocomplete
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                                .build(MainActivity.this);
                        startAutocomplete.launch(intent);

                    }
                });

            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createButton.setVisibility(View.INVISIBLE);
                showButton.setVisibility(View.INVISIBLE);
                mapButton.setVisibility(View.INVISIBLE);

                spinnerTitle.setVisibility(View.VISIBLE);
                itemsSpinner.setVisibility(View.VISIBLE);


                //loads the data for the spinner as it becomes visible to the user
                loadSpinnerData();



            }
        });


        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCurrentLocation();

            }
        });




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //first takes in all of the user inputs and transfers them to variables
                int selectedId = radioButtons.getCheckedRadioButtonId();
                radioSelected = findViewById(selectedId);
                String radioIn = radioSelected.getText().toString();

                String nameIn = nameEntry.getText().toString();
                String phoneIn = phoneEntry.getText().toString();
                String descriptionIn = descriptionEntry.getText().toString();
                String dateIn = dateEntry.getText().toString();
                LatLng locationIn = locationCoords;

                //checks to make sure that all fields have been entered, no fields left blank
                if (nameIn.isEmpty() && phoneIn.isEmpty() && descriptionIn.isEmpty() && dateIn.isEmpty() && locationIn.toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                //calls the database handler function to add in the variables into a single row
                dbHandler.addItem(radioIn, nameIn, phoneIn, descriptionIn, dateIn, locationIn);

                //shows a toast that the items have been added and sets the fields back to empty
                Toast.makeText(MainActivity.this, "item has been added.", Toast.LENGTH_SHORT).show();
                nameEntry.setText("");
                phoneEntry.setText("");
                descriptionEntry.setText("");
                dateEntry.setText("");
                editText.setText("");

                createButton.setVisibility(View.VISIBLE);
                showButton.setVisibility(View.VISIBLE);
                mapButton.setVisibility(View.VISIBLE);

                radioText.setVisibility(View.INVISIBLE);
                radioButtons.setVisibility(View.INVISIBLE);
                radioLost.setVisibility(View.INVISIBLE);
                radioFound.setVisibility(View.INVISIBLE);
                nameTitle.setVisibility(View.INVISIBLE);
                nameEntry.setVisibility(View.INVISIBLE);
                phoneTitle.setVisibility(View.INVISIBLE);
                phoneEntry.setVisibility(View.INVISIBLE);
                descriptionTitle.setVisibility(View.INVISIBLE);
                descriptionEntry.setVisibility(View.INVISIBLE);
                dateTitle.setVisibility(View.INVISIBLE);
                dateEntry.setVisibility(View.INVISIBLE);
                locationTitle.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
                getLocationButton.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);

            }
        });



        itemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //checks to see if anything other than the first item is selected, once a selection has been made it calls getItemSelected to grab all of the items details
                if(position>0) {

                    String itemSelected = String.valueOf(itemsSpinner.getSelectedItem());

                    spinnerTitle.setVisibility(View.INVISIBLE);
                    itemsSpinner.setVisibility(View.INVISIBLE);
                    itemDetails.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.VISIBLE);

                    getItemSelected(itemSelected);

                    removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //calls the database handler function to delete the item selected in the spinner.
                            dbHandler.deleteItem(itemSelected);

                            itemDetails.setVisibility(View.INVISIBLE);
                            removeButton.setVisibility(View.INVISIBLE);

                            createButton.setVisibility(View.VISIBLE);
                            showButton.setVisibility(View.VISIBLE);
                            mapButton.setVisibility(View.VISIBLE);

                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createButton.setVisibility(View.INVISIBLE);
                showButton.setVisibility(View.INVISIBLE);
                mapButton.setVisibility(View.INVISIBLE);

                //sets the map to be visible
                mapFragment.getView().setVisibility(View.VISIBLE);

                //calls makeMakers to create the markers based on the items in the database
                makeMarkers(mMap);





            }
        });






    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        //once the map has been made, sets it to a global variable, and sets the default position to be over sydney
        mMap = googleMap;

        LatLng sydney = new LatLng(-37.813629, 144.963058);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }

    private void getCurrentLocation(){

        //first checks for permissions to use the users current location
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        //once permission has been succesfully granted it will grab the users coordinates and set them to a global variable
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                editText.setText(latlng.toString());
                                locationCoords = latlng;
                            }
                        }
                    });
        }

    }

    //runs the check on whether permission was granted and calls getCurrentLocation if it was
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }

    }


}