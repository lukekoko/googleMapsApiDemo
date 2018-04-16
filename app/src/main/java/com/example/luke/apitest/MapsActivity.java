package com.example.luke.apitest;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap map;
    private Polyline polyLine;
    private TextView distanceTxt;
    private Button clearBtn;
    private Button resetBtn;
    private static final LatLng UTS = new LatLng(-33.884196, 151.201009);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //searches for the map and initialises
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialising textview and buttons
        distanceTxt = findViewById(R.id.textView1);
        clearBtn = findViewById(R.id.button1);
        resetBtn = findViewById(R.id.button2);
    }

    //when the map is ready this code runs
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true); //enable zoom controls on map

        polyLine = googleMap.addPolyline(new PolylineOptions().add(UTS).width(6).color(Color.RED)); //initialising polyline with first point
        map.addMarker(new MarkerOptions().position(UTS).title("University of Technology Sydney")); //adds a map marker at specified position
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(UTS, 18.0f)); //sets initial camera position and boom
        googleMap.setOnMapClickListener(this);

        //a listener for the "clear line" button
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polyLine.remove(); //removes all points from the line
                polyLine = googleMap.addPolyline(new PolylineOptions().add(UTS).width(6).color(Color.RED)); //initialises the line again
                calculateDistance(polyLine.getPoints()); //updates the distance
            }});
        //a listener for the "reset" button
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(UTS, 18.0f)); //moves the camera to the starting position
            }});
    }

     @Override
     public void onMapClick(LatLng latLng) {
            //When the user touches a point on the map that point is added to the line
            List<LatLng> newPath = polyLine.getPoints(); //a list of the current points in the line
            newPath.add(latLng); //adds a new point at the end of the list
            polyLine.setPoints(newPath); //sets the line to the new path
            calculateDistance(polyLine.getPoints()); //updates the distance of the line
        }

     private void calculateDistance(List<LatLng> points) {
        //calculates the distance of the line in metres
        Location previousPoint = new Location("");
        Location point = new Location("");
        float sum = 0;
        for (int i=0; i<points.size(); ++i) {
            point.setLatitude(points.get(i).latitude);
            point.setLongitude(points.get(i).longitude);
            if(i==0) {
                previousPoint.setLatitude(point.getLatitude());
                previousPoint.setLongitude(point.getLongitude());
                continue;
            }
            sum += point.distanceTo(previousPoint);
            previousPoint.setLatitude(point.getLatitude());
            previousPoint.setLongitude(point.getLongitude());
            }
            distanceTxt.setText(getString(R.string.text, sum));
        }
     }