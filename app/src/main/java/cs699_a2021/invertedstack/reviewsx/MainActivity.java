package cs699_a2021.invertedstack.reviewsx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private AccountHeader header;
    private Drawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(MainActivity.this);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("ReviewsX").withEmail("CS699 Final Project").withIcon(R.mipmap.ic_launcher_round)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        ExpandableDrawerItem collections = new ExpandableDrawerItem().withName("Collections").withIdentifier(2);
        ArrayList<String> collection_names = new ArrayList<>();
        int collections_start = 100, i = 0;
        Cursor cursor = db.getAllCollections();
        while(cursor.moveToNext()) {
            String name = cursor.getString(0);
            boolean is_deletable = i>=4;
            collections = collections.withSubItems(
                    new DrawerCollectionsItem().withCollectionName(name).withDeletable(is_deletable).withIdentifier(collections_start + i)
            );
            collection_names.add(name);
            System.out.println(name + collections_start + i);
            i += 1;
        }
        collections = collections.withSubItems(
                new SecondaryDrawerItem().withName("New Collection").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(3)
        );
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_baseline_home_24).withIdentifier(1),
                        collections
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        System.out.println(drawerItem.getIdentifier());
                        if(drawerItem.getIdentifier() >= collections_start) {
                            int idx = (int) (drawerItem.getIdentifier() - collections_start);
                            Intent intent = new Intent(MainActivity.this, ViewCollectionActivity.class);
                            intent.putExtra("collection_name", collection_names.get(idx));
                            startActivity(intent);
                        }
                        return false;
                    }
                })
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