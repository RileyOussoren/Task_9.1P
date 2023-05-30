package com.rileyoussoren.lostandfoundMap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
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

import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button createButton;
    Button showButton;
    Button saveButton;
    Button removeButton;
    Button mapButton;
    Button getLocationButton;
    Button selectLocationButton;

    EditText nameEntry;
    EditText phoneEntry;
    EditText descriptionEntry;
    EditText dateEntry;
    //EditText locationEntry;

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







    private void loadSpinnerData(){

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<ItemModal> labels = db.readItems();

        List<String> itemsList = new ArrayList<>();

        itemsList.add("");

        for (int loop = 0; loop < labels.size(); loop++){

            itemsList.add(labels.get(loop).getItemName());

        }

        ArrayAdapter itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsList);
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemsSpinner.setAdapter(itemsAdapter);

    }

    private void getItemSelected(String selection){

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<ItemModal> labels = db.readItems();

        List<String> itemDetailsList = new ArrayList<>();

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, AutocompleteActivity.class));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapFragment.getView().setVisibility(View.INVISIBLE);



        createButton = findViewById(R.id.createButton);
        showButton = findViewById(R.id.showButton);
        saveButton = findViewById(R.id.saveButton);
        removeButton = findViewById(R.id.removeButton);
        mapButton = findViewById(R.id.mapButton);
        getLocationButton = findViewById(R.id.getLocationButton);
        selectLocationButton = findViewById(R.id.selectLocationButton);

        nameEntry = findViewById(R.id.nameEntry);
        phoneEntry = findViewById(R.id.phoneEntry);
        descriptionEntry = findViewById(R.id.descriptionEntry);
        dateEntry = findViewById(R.id.dateEntry);
        //locationEntry = findViewById(R.id.locationEntry);

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
                selectLocationButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                getLocationButton.setVisibility(View.VISIBLE);

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



                loadSpinnerData();



            }
        });


    /*
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioButtons.getCheckedRadioButtonId();
                radioSelected = findViewById(selectedId);
                String radioIn = radioSelected.getText().toString();

                String nameIn = nameEntry.getText().toString();
                String phoneIn = phoneEntry.getText().toString();
                String descriptionIn = descriptionEntry.getText().toString();
                String dateIn = dateEntry.getText().toString();
                String locationIn = locationEntry.getText().toString();

                if (nameIn.isEmpty() && phoneIn.isEmpty() && descriptionIn.isEmpty() && dateIn.isEmpty() && locationIn.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.addItem(radioIn, nameIn, phoneIn, descriptionIn, dateIn, locationIn);

                Toast.makeText(MainActivity.this, "item has been added.", Toast.LENGTH_SHORT).show();
                nameEntry.setText("");
                phoneEntry.setText("");
                descriptionEntry.setText("");
                dateEntry.setText("");
                //locationEntry.setText("");

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
                selectLocationButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
                getLocationButton.setVisibility(View.INVISIBLE);

            }
        });
    */
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                selectLocationButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
                getLocationButton.setVisibility(View.INVISIBLE);

                mapFragment.getView().setVisibility(View.VISIBLE);

            }
        });

        itemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

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

                mapFragment.getView().setVisibility(View.VISIBLE);

                LatLng sydney = new LatLng(-37.813629, 144.963058);

                mMap.addMarker(new MarkerOptions()
                        .position(sydney));





            }
        });






    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng sydney = new LatLng(-37.813629, 144.963058);

        LatLng sydney1 = new LatLng(-40.813629, 160.963058);

        //googleMap.addMarker(new MarkerOptions()
        //        .title("Melbourne Marker")
        //        .position(sydney));

        //googleMap.addMarker(new MarkerOptions()
        //        .title("Melbourne Marker1")
        //        .position(sydney1));

        //Polyline polyline = googleMap.addPolyline(new PolylineOptions()
        //        .clickable(true)
        //        .add(sydney, sydney1));

        //polyline.setColor(Color.BLUE);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 100));


    }


}