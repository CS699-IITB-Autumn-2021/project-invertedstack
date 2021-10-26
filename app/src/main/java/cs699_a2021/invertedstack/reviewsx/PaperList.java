package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;
import cs699_a2021.invertedstack.reviewsx.items.PapersItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This activity is responsible for displaying the list of papers for a particular category for a particular
 * year of a particular conference
 */
public class PaperList extends AppCompatActivity {
    /**
     * Instance of FastAdapter that will be used by RecyclerView
     */
    private FastAdapter fastAdapter;
    /**
     * Instance of ItemAdapter that will be used by FastAdapter
     */
    private ItemAdapter itemAdapter;
    /**
     * Name of the conference for which this activity is being used. This will be used as a parameter in API call
     */
    String conf_name = "iclr";
    /**
     * "Nicer" name of the conference for display
     */
    String conf_name_nice = "ICLR";
    /**
     * Year of the conference
     */
    String year = "2021"; // Currently only for ICLR
    /**
     * Category of the papers being shown in this activity. This will be used as a parameter in API call
     */
    String category = "spotlight_presentations";
    /**
     * "Nicer" name of the category for the display
     */
    String category_nice = "Spotlight Presentations";
    /**
     * The string holding the main JSON response
     */
    private String main_json = null;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        Toolbar toolbar = findViewById(R.id.paperlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List of papers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if(b == null) {
            // TODO: Show error and die
        }
        else {
            conf_name = b.getString("confname");
            conf_name_nice = b.getString("confname_nice");
            year = b.getString("year");
            category = b.getString("category");
            category_nice = b.getString("category_nice");
        }

        TextView header = findViewById(R.id.paperlist_header);
        header.setText(conf_name_nice + " " + year + " " + category_nice);

        RecyclerView recyclerView = findViewById(R.id.paperlist_recyclerview);


        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.withEventHook(new PapersItem.ExpandBodyClickEvent());
        fastAdapter.withEventHook(new PapersItem.CollectionSaveEvent());
        fastAdapter.withOnClickListener((OnClickListener<PapersItem>) (v, adapter, item, position) -> {
            Intent intent = new Intent(PaperList.this, PaperWithDiscussion.class);
            intent.putExtra("conf_name", conf_name);
            intent.putExtra("year", year);
            intent.putExtra("category", category);
            intent.putExtra("data_id", item.content_id);
            startActivity(intent);;
            return false;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(PaperList.this);
        Cursor allPapers = db.getPapersByConfYearCat(conf_name_nice, year, category_nice);
        ArrayList<IItem> items = new ArrayList<>();

        if(allPapers.getCount() != 0) {
            while(allPapers.moveToNext()) {
                PapersItem item = new PapersItem();
                Log.d("PapersList", allPapers.toString());
                Log.d("PapersList", "Column Count = " + allPapers.getColumnCount());
                String[] names = allPapers.getColumnNames();
                for(String name: names)
                    Log.d("PapersList", name);
                try {
                    Log.d("PapersList", allPapers.getString(0));
                    Log.d("PapersList", allPapers.getString(1));
                    Log.d("PapersList", allPapers.getString(2));
                    Log.d("PapersList", allPapers.getString(3));
                    Log.d("PapersList", "-------------------------------");
                    item.content_id = allPapers.getString(0);
                    item.title = allPapers.getString(1);
                    item.authors = allPapers.getString(2);
                    item.body = allPapers.getString(3);
                    items.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                    // ref - https://stackoverflow.com/a/7959379
                    Log.wtf("PapersList", "WTF did just happen ?");
                }
            }
            itemAdapter.add(items);
            findViewById(R.id.paperlist_progressbar).setVisibility(View.GONE);
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        String url = getString(R.string.server_url) + "/get_parameters?conference=" + conf_name + "&year=" + year + "&category=" + category;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Network error. Check your internet connection or contact the server admin", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String rcvd_string = response.body().string();
                    main_json = rcvd_string;
                    try {
                        JSONArray array = new JSONArray(main_json);
                        Log.d("PapersList", "Array length = " + array.length());
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            PapersItem item = new PapersItem();
                            item.content_id = object.getString("data_id");
                            item.title = object.has("paper_title") ? object.getString("paper_title") : "(Not Available)";
                            String authors_string = "";
                            authors_string += "<b>Authors: </b><i>";
                            // Authors
                            if(object.has("authors")) {
                                JSONArray authors = object.getJSONArray("authors");
                                for (int j = 0; j < authors.length(); j++) {
                                    authors_string += authors.getString(j) + (j == authors.length() - 1 ? "</i>" : ", ");
                                    Log.d("PapersList", authors_string);
                                }
                            }
                            else {
                                authors_string += "(Not Available)";
                            }
                            item.authors = authors_string;
                            String body = "";
                            // Abstract -- could be added as a collapsible as a separate entry
                            body += "<br><b>Abstract:</b><br>";
                            body += object.has("abstract") ? object.getString("abstract") : "(Not Available)";
                            body += "<br><b>Keywords:</b><br><i>";
                            body += object.has("keywords") ? object.getString("keywords") : "(Not Available)";
                            item.body = body;
                            items.add(item);
                            boolean result = db.updatePaperData(item.content_id, item.title, item.authors, item.body, conf_name_nice, year, category_nice);
                            Log.d("PapersList", "DB update result = " + result);
                            Log.d("PapersList", "-----------------------------");
                        }
                    } catch (JSONException e) {
                        // TODO: Show proper error message
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "JSON parsing error. Contact the server admin", Toast.LENGTH_LONG).show();
                    }
                    PaperList.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemAdapter.add(items);
                            findViewById(R.id.paperlist_progressbar).setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    /**
     * Menu inflater for the activity. The main job is to initialize and handle search based filtering of the RecyclerView in the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ref - https://www.youtube.com/watch?v=CTvzoVtKoJ8
        getMenuInflater().inflate(R.menu.paperlist_menu, menu);
        MenuItem item = menu.findItem(R.id.paperlist_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.filter(newText);
                itemAdapter.getItemFilter().withFilterPredicate((IItemAdapter.Predicate<PapersItem>) (item1, constraint) -> {
                    String title = item1.title.toLowerCase();
                    String authors = item1.authors.toLowerCase();
                    String body = item1.body.toLowerCase();
                    String key = constraint.toString().toLowerCase();
                    return (title.contains(key) || authors.contains(key) || body.contains(key));
                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Click handler for the "back" key in the ActionBar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}