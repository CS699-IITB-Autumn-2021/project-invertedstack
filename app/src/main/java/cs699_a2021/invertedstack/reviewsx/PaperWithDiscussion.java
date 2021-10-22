package cs699_a2021.invertedstack.reviewsx;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;

public class PaperWithDiscussion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_with_discussion);

        RecyclerView recyclerView = findViewById(R.id.discussion_recyclerview);

        ItemAdapter itemAdapter = new ItemAdapter();
        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);
        ArrayList<DiscussionItem> items = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            DiscussionItem item = new DiscussionItem();
            item.title = "Title" + i;
            System.out.println(item.title);
            item.body = "Body" + i;
            items.add(item);
        }
        itemAdapter.add(items);
    }
}