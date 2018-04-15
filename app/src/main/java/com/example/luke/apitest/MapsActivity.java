package com.example.luke.apitest;

import android.Manifest;
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distanceTxt = (TextView) findViewById(R.id.textView1);
        clearBtn = (Button) findViewById(R.id.button1);
        resetBtn = (Button) findViewById(R.id.button2);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        polyLine = googleMap.addPolyline(new PolylineOptions().add(UTS).width(6).color(Color.RED));
        map.addMarker(new MarkerOptions().position(UTS).title("University of Technology Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(UTS, 18.0f));

        googleMap.setOnMapClickListener(this);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polyLine.remove();
                polyLine = googleMap.addPolyline(new PolylineOptions().add(UTS).width(6).color(Color.RED));
                calculateDistance(polyLine.getPoints());
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(UTS, 18.0f));
            }
        });
    }

     @Override
     public void onMapClick(LatLng latLng) {
            List<LatLng> newPath = polyLine.getPoints();
            newPath.add(latLng);
            polyLine.setPoints(newPath);
            calculateDistance(polyLine.getPoints());
        }

     private void calculateDistance(List<LatLng> points) {
        Location previousPoint = new Location("");
        Location point = new Location("");
        float sum = 0f;
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
            distanceTxt.setText("Distance: " + String.format("%.2f", sum) + "m");
        }
     }

