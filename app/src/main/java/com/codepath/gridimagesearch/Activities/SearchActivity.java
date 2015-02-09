package com.codepath.gridimagesearch.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.gridimagesearch.Adapters.ImageResultsAdapter;
import com.codepath.gridimagesearch.Models.ImageResult;
import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.Utils.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                onSearchAPICall(page, false);
            }
        });
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("result", result);
                startActivity(i);
            }
        });
    }

    public boolean onClickedSettings (MenuItem item) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    public void onImageSearch (View v) {
        query = etQuery.getText().toString();
        onSearchAPICall(0, true);
    }

    public void onSearchAPICall(int page, final boolean shouldClearAdapter) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String imageSizePref = sharedPref.getString("image_size", "");
        String imageColorPref = sharedPref.getString("image_color", "");
        String imageTypePref = sharedPref.getString("image_type", "");
        String searchSitePref = sharedPref.getString("search_site", "");

        AsyncHttpClient client = new AsyncHttpClient();
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images";
        Uri.Builder builder = Uri.parse(searchUrl).buildUpon();
        builder.appendQueryParameter("v", "1.0");
        builder.appendQueryParameter("rsz", "8");
        builder.appendQueryParameter("q", query);
        builder.appendQueryParameter("start", Integer.toString(page));
        builder.appendQueryParameter("imgsz", imageSizePref);
        builder.appendQueryParameter("imgcolor", imageColorPref);
        builder.appendQueryParameter("imgtype", imageTypePref);
        builder.appendQueryParameter("as_sitesearch", searchSitePref);
        String finalUrl = builder.build().toString();
        client.get(finalUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    if (shouldClearAdapter) {
                        aImageResults.clear();
                    }
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJSON));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
