package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import java.util.ArrayList;

import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;
import cs699_a2021.invertedstack.reviewsx.items.CollectionsPaperItem;

/**
 * View a particular collection of papers.
 * The collections MUST be all uniquely named. Also special name "All notes" is reserved for collection of ALL the notes that exist in the
 * database
 */
public class ViewCollectionActivity extends AppCompatActivity {
    /**
     * FastAdapter for showing the list of papers in the RecyclerView
     */
    private FastAdapter fastAdapter;
    /**
     * ItemAdapter for FastAdapter
     */
    private ItemAdapter itemAdapter;

    /**
     * onCreate method for the activity. The lookup is only local. No need to fetch from the server
     * Collections are ALWAYS local in this version
     *
     * In the future versions -- maybe allow an optional "sync to server" feature ?
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_collection);

        Toolbar toolbar = findViewById(R.id.collection_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Collection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        String collection_name;
        if(b == null) {
            collection_name = "All notes";
        }
        else {
            collection_name = b.getString("collection_name");
        }

        RecyclerView recyclerView = findViewById(R.id.view_collection_recyclerview);

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.withEventHook(new CollectionsPaperItem.ExpandBodyClickEvent());
        fastAdapter.withEventHook(new CollectionsPaperItem.CollectionRemoveEvent());
        fastAdapter.withOnClickListener((OnClickListener<CollectionsPaperItem>) (v, adapter, item, position) -> {
            Intent intent = new Intent(ViewCollectionActivity.this, PaperWithDiscussion.class);
            intent.putExtra("conf_name", item.conf);
            intent.putExtra("year", item.year);
            intent.putExtra("category", item.category);
            intent.putExtra("data_id", item.content_id);
            startActivity(intent);;
            return false;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(ViewCollectionActivity.this);
        Cursor allPapers = db.getAllPapersInCollection(collection_name);

        ArrayList<IItem> items = new ArrayList<IItem>();

        if(allPapers.getCount() != 0) {
            while(allPapers.moveToNext()) {
                String id = allPapers.getString(0);
                Cursor paper_data = db.getPaperByDataID(id);
                if(paper_data.getCount() != 0) {
                    paper_data.moveToFirst();
                    CollectionsPaperItem item = new CollectionsPaperItem();
                    item.content_id = paper_data.getString(0);
                    item.title = paper_data.getString(1);
                    item.authors = paper_data.getString(2);
                    item.body = paper_data.getString(3);
                    item.conf = paper_data.getString(4);
                    item.year = paper_data.getString(5);
                    item.category = paper_data.getString(6);
                    item.collection_name = collection_name;
                    items.add(item);
                }
                else {
                    // TODO: Something gone wrong, ignore for now
                }
            }
            itemAdapter.add(items);
            findViewById(R.id.view_collection_progressbar).setVisibility(View.GONE);
        }
        else {
            // TODO: Show Errors
            Toast.makeText(getApplicationContext(), "Empty collection! Contact server administrator if this is an error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Click handler for taking the user back to home activity on clicking the "Back" button in ActionBar
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