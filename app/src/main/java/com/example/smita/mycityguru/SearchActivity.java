package com.example.smita.mycityguru;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCJXaLByfHosmNLxbPaHhM3tTQgyiKzd7E";
    private static final String location="18.498072,73.8328396";
    private static final String radius= "2000";

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.searchToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);   // removes title from toolbar

        AutoCompleteTextView userInput = findViewById(R.id.searchEditText);




        userInput.setAdapter(new GooglePlaceAutocompleteAdapter(this,R.layout.search_list,R.id.placeText));


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }



    @SuppressLint("LongLogTag")
    public static ArrayList autocomplete(String input){
        ArrayList resultList = null;

        HttpsURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?location="+location);
        sb.append("&radius="+radius);
        sb.append("&types=establishment&strictbounds");
        sb.append("&key="+ API_KEY);                                 //change ? - &
        //sb.append("&components=country:in");
        /*StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?location=18.498072,73.8328396&radius=2000&types=establishment&strictbounds&key=AIzaSyA78PaOND-Yoshet42MzYFN7yGt6YWE-x4");*/
        try {

            sb.append("&input="+ URLEncoder.encode(input,"utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpsURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff))!= -1){
                jsonResults.append(buff,0,read);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0 ; i < predsJsonArray.length() ; i++){
                String placeDetails = predsJsonArray.getJSONObject(i).getString("description");
                String placeInfo[] = placeDetails.split(",");
                resultList.add(placeInfo[0]+","+placeInfo[1]);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"Cannot Process Json Results",e);
        }

        return resultList;

    }


    //Adapter Class

    class GooglePlaceAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlaceAutocompleteAdapter(Context context,int layoutid ,int textViewResourceId) {
            super(context, layoutid ,textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int index) {
            return resultList.get(index);
        }


        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }

                }
            };
            return filter;
        }
    }




}
