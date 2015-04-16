package corp.river.hey.hey;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends FragmentActivity {

    private static String imsi;
    private static HeatMap fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLocation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        addMapFragment();
        getImsi();
    }

    private void addMapFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new HeatMap();
        transaction.add(R.id.mapView, fragment);
        transaction.commit();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            sendMsg(params[0]);
            return null;
        }
    }

    private void getLocation() {
        LocationManager mlocManager;
        LocationListener mlocListener = new MyLocationListener();
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mlocManager.getBestProvider(criteria, false);
        Location location = mlocManager.getLastKnownLocation(provider);

        if (location != null) {
            mlocListener.onLocationChanged(location);
        }
    }

    private void getImsi() {
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        imsi = tMgr.getSubscriberId();
    }

    private void sendMsg(String eventType) {
        StringBuilder sb = new StringBuilder();

        String http = "http://requestb.in/17xrlmt1";
        //String http = "http://hey.river.corp:6969";
        HttpURLConnection urlConnection=null;
        try {
            DataOutputStream printout;
            URL url = new URL(http);
            Long now = System.currentTimeMillis() / 1000L;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("eventType", eventType);
            jsonParam.put("timestamp", now);
            jsonParam.put("msisdn", imsi);
            jsonParam.put("latitude", MyLocationListener.latitude);
            jsonParam.put("longitude", MyLocationListener.longitude);
            printout = new DataOutputStream(urlConnection.getOutputStream());
            printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            printout.flush();
            printout.close();

            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println(""+sb.toString());
            }else{
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
    }

    public void leulerte(View view) {
        /* Hey! I should send a leulerte */
        getLocation();
        fragment.addHeatMap();
        new MyAsyncTask().execute("leuleu");

    }

    public void gtfo(View view) {
        /* It is sausage festival! */
        getLocation();
        fragment.addHeatMap();
        new MyAsyncTask().execute("gtfo");
    }

}
