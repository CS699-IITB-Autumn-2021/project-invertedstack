package cs699_a2021.invertedstack.reviewsx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

public class MainActivity extends AppCompatActivity {
    private AccountHeader header;
    private Drawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("ReviewsX").withEmail("CS699 Final Project").withIcon(R.mipmap.ic_launcher_round)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIdentifier(1).withIcon(R.drawable.ic_baseline_home_24),
                        new SectionDrawerItem().withName("Collections"),
                        new SecondaryDrawerItem().withName("Currenly reading").withIdentifier(2).withIcon(R.drawable.ic_baseline_collections_bookmark_24),
                        new SecondaryDrawerItem().withName("Wishlist").withIdentifier(3).withIcon(R.drawable.ic_baseline_collections_bookmark_24),
                        new SecondaryDrawerItem().withName("Already read").withIdentifier(4).withIcon(R.drawable.ic_baseline_collections_bookmark_24)
                )
                .withSavedInstance(savedInstanceState)
                .build();

        Button papers_list = findViewById(R.id.main_button_papers_list);
        Button discussions = findViewById(R.id.main_button_discussion);
        Button notetaking = findViewById(R.id.main_button_notetaking);
        Button collection = findViewById(R.id.main_button_collection);

        papers_list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaperList.class);
            startActivity(intent);
        });

        discussions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaperWithDiscussion.class);
            startActivity(intent);
        });

        notetaking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteTakingActivity.class);
            startActivity(intent);
        });

        collection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewCollectionActivity.class);
            startActivity(intent);
        });
    }
}