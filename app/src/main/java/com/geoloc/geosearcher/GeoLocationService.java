package com.geoloc.geosearcher;

/**
 * Created by matejpikovnik on 06/04/15.
 */

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.geoloc.geosearcher.com.geoloc.geosearcher.interfaces.OnGeoLocationComplete;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;


public class GeoLocationService extends AsyncTask<Void, Void, String>
{
    private Context context;
    private double lat;
    private double lon;
    private OnGeoLocationComplete onTaskComplete;

    public void setMyTaskCompleteListener(OnGeoLocationComplete onTaskComplete)
    {
        this.onTaskComplete = onTaskComplete;
    }

    public GeoLocationService(Context context, double lat, double lon)
    {
        this.context = context;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected String doInBackground(Void... params)
    {

        Geocoder geocoder = new Geocoder(this.context, Locale.ENGLISH);
        try
        {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 100);
            if (addresses != null)
            {
                if (addresses.size() > 0)
                {
                    Address fetchedAddress = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder();

                    for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++)
                    {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append(", ");
                    }

                    return strAddress.toString();
                }
            }
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        return getAdressActionBackup(lat, lon);
    }

    @Override
    protected void onPostExecute(String message)
    {
        onTaskComplete.setMyTaskComplete(message);
    }

    private String getAdressActionBackup(double lat, double lon)
    {
        String string ="http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=" + lat + "," + lon;
        String responseString = "";
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(string);

        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);

            }
            else
            {
                responseString = "error";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            responseString = e.getLocalizedMessage();
        }

        return responseString;
    }


}
