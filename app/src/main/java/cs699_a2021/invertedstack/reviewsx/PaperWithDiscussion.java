package cs699_a2021.invertedstack.reviewsx;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PaperWithDiscussion extends AppCompatActivity {
    private FastAdapter fastAdapter;
    private ExpandableExtension expandableExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_with_discussion);

        RecyclerView recyclerView = findViewById(R.id.discussion_recyclerview);

        ItemAdapter itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);

        expandableExtension = new ExpandableExtension();
        fastAdapter.addExtension(expandableExtension);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);
        ArrayList<IItem> items = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            if(i % 3 != 0) {
                DiscussionItem item = new DiscussionItem();
                item.title = "Title" + i;
                System.out.println(item.title);
                item.body = "Body" + i;
                items.add(item);
                continue;
            }
            DiscussionItemExpandable item = new DiscussionItemExpandable();
            item.title = "Title" + i;
            System.out.println(item.title);
            item.body = "Body" + i;

            List<IItem> subItems = new LinkedList<>();
            for(int ii = 0; ii < 5; ii++) {
                DiscussionItemExpandable subItem = new DiscussionItemExpandable();
                subItem.title = "SubTitle" + i + "." + ii;
                subItem.body = "SubBody" + i + "." + ii;
                if(ii % 2 == 0) {
                    subItems.add(subItem);
                    continue;
                }
                List<IItem> subsubItems = new LinkedList<>();
                for(int iii = 0; iii < 3; iii++) {
                    DiscussionItemExpandable subsubItem = new DiscussionItemExpandable();
                    subsubItem.title = "SubSubTitle" + i + "." + ii + "," + iii;
                    subsubItem.body = "SubSubBody" + i + "." + ii + "," + iii;
                    // Also have a padding parameter that adds like 2 dp larger padding-left than parent to a maximum padding
                    subsubItems.add(subsubItem);
                }
                subItem.withSubItems(subsubItems);
                subItems.add(subItem);
            }
            item.withSubItems(subItems);
            items.add(item);
        }
        itemAdapter.add(items);
    }
}