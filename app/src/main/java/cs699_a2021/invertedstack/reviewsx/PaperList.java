package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import java.util.ArrayList;

public class PaperList extends AppCompatActivity {
    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        RecyclerView recyclerView = findViewById(R.id.paperlist_recyclerview);

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        ArrayList<IItem> items = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            DiscussionItemExpandable item = new DiscussionItemExpandable();
            item.title = "Paper Title " + i;
            item.body = "Paper info " + i;
            items.add(item);
        }
        itemAdapter.add(items);
    }
    // ref - https://www.youtube.com/watch?v=CTvzoVtKoJ8
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<DiscussionItemExpandable>() {
                    @Override
                    public boolean filter(DiscussionItemExpandable item, @Nullable CharSequence constraint) {
                        String title = item.title.toLowerCase();
                        String body = item.body.toLowerCase();
                        String key = constraint.toString().toLowerCase();
                        return (title.contains(key) || body.contains(key));
                    }
                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}