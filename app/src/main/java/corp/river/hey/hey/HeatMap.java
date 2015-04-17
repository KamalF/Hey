package corp.river.hey.hey;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * A fragment that launches other parts of the demo application.
 */
public class HeatMap extends Fragment {

    MapView mMapView;
    static ArrayList<LatLng> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.activity_heat_map, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        GoogleMap googleMap = mMapView.getMap();
        // latitude and longitude
        // default is Intersec
        double latitude = 48.889260;
        double longitude = 2.239001;

        if (MyLocationListener.latitude > 0) {
            latitude = MyLocationListener.latitude;
            longitude = MyLocationListener.longitude;
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        return v;
    }

    private class AsyncRead extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            try {
                readItems("http://192.168.255.33/get/");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void addHeatMap() {
        HeatmapTileProvider mProvider;
        GoogleMap googleMap = mMapView.getMap();
        TileOverlay mOverlay;

        new AsyncRead().execute();

        if (list != null && !list.isEmpty()) {
            mProvider = new HeatmapTileProvider.Builder()
                    .data(list) // weightedData
                    .build();

            mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            System.out.println(list.size());
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private void readItems(String url) throws IOException, JSONException {
        list = new ArrayList<LatLng>();
        InputStream is = new URL(url).openStream();
        String json;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            json = readAll(rd);
        } finally {
            is.close();
        }
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("longitude");
            double lng = object.getDouble("latitude");
            LatLng latLng = new LatLng(lat, lng);
            list.add(latLng);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}