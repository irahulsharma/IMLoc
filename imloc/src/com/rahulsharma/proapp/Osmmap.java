package com.rahulsharma.proapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
//import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;

public class Osmmap extends Activity {

    private MapView m_mapView;
    private TextView m_textView;
    private int MAP_DEFAULT_ZOOM = 12;
    private double MAP_DEFAULT_LATITUDE = 12.966667;
    private double MAP_DEFAULT_LONGITUDE = 77.566667;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osmmap);

        m_mapView = (MapView) findViewById(R.id.mapview);
        m_textView = (TextView) findViewById(R.id.textView1);
        
        MAP_DEFAULT_LATITUDE = getIntent().getExtras().getDouble("latitude");
        MAP_DEFAULT_LONGITUDE = getIntent().getExtras().getDouble("longitude");
        
        m_textView.setText("Latitude = " + MAP_DEFAULT_LATITUDE + "," + "Longitude = " + MAP_DEFAULT_LONGITUDE);
        
        m_mapView.setBuiltInZoomControls(true);
        m_mapView.setMultiTouchControls(true);
        m_mapView.setClickable(true);
        m_mapView.setUseDataConnection(false);
        m_mapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        m_mapView.getController().setCenter(
        			new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
        m_mapView.setTileSource(TileSourceFactory.MAPNIK);
    }
}