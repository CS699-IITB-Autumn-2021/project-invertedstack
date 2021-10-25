package cs699_a2021.invertedstack.reviewsx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import java.util.ArrayList;

public class ViewCollectionActivity extends AppCompatActivity {
    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_collection);

        String collection_name = "Currently reading";

        RecyclerView recyclerView = findViewById(R.id.view_collection_recyclerview);

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.withEventHook(new CollectionsPaperItem.ExpandBodyClickEvent());
        fastAdapter.withEventHook(new CollectionsPaperItem.CollectionRemoveEvent());

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
        }
    }
}