package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AccountHeader header;
    private DrawerBuilder drawerBuilder;
    private Drawer drawer;
    ExpandableDrawerItem collections;
    ArrayList<String> collection_names;
    ReviewsXDatabaseHelper db;
    int collections_start = 100, i = 0;
    private View.OnClickListener deleteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup parent = (ViewGroup) v.getParent();
            TextView textView = parent.findViewById(R.id.material_drawer_collection_name);
            String name = (String) textView.getText();
            drawer.getExpandableExtension().collapse(drawer.getPosition(2));
            // https://github.com/mikepenz/MaterialDrawer/issues/2154#issuecomment-355746149
            List items = drawer.getDrawerItem(2).getSubItems();
            int idx = collection_names.indexOf(name);
            db.deleteCollection(name);
            items.remove(idx);
            collection_names.remove(idx);
            drawer.getDrawerItem(2).withSubItems(items);
            drawer.getAdapter().notifyAdapterDataSetChanged();
        }
    };

    private void update_collection_names() {
        i = 0;
        collection_names = new ArrayList<>();
        Cursor cursor = db.getAllCollections();
        while(cursor.moveToNext()) {
            String name = cursor.getString(0);
            if(i < 4) {
                collections = collections.withSubItems(
                        new PrimaryDrawerItem().withName(name).withIcon(FontAwesome.Icon.faw_bookmark).withIdentifier(collections_start + i)
                );
            }
            else {
                collections = collections.withSubItems(
                        new DrawerCollectionsItem().withCollectionName(name).withIdentifier(collections_start + i).withClickListener(deleteClicked)
                );
            }
            collection_names.add(name);
            System.out.println(name + collections_start + i);
            i += 1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        db = new ReviewsXDatabaseHelper(MainActivity.this);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("ReviewsX").withEmail("CS699 Final Project").withIcon(R.mipmap.ic_launcher_round)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        collections = new ExpandableDrawerItem().withName("Collections").withIdentifier(2);

        update_collection_names();

        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_baseline_home_24).withIdentifier(1),
                        collections,
                        new SecondaryDrawerItem().withName("New Collection").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(3)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        System.out.println(drawerItem.getIdentifier());
                        System.out.println("View " + view.getClass());
                        int idx = 0;
                        if(drawerItem.getIdentifier() >= collections_start) {
                            if(drawerItem instanceof DrawerCollectionsItem) {
                                idx = collection_names.indexOf(((DrawerCollectionsItem) drawerItem).collection_name);
                            }
                            else {
                                idx = (int) (drawerItem.getIdentifier() - collections_start);
                            }
                            Intent intent = new Intent(MainActivity.this, ViewCollectionActivity.class);
                            intent.putExtra("collection_name", collection_names.get(idx));
                            startActivity(intent);
                        }

                        switch((int) drawerItem.getIdentifier()){
                            case 3:
                                // New Collection
                                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                                        .title("Add a new collection")
                                        .input("Enter the name of new collection", null, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                String new_name = input.toString();
                                                // update collection_names using db once
                                                //update_collection_names();
                                                if(collection_names.contains(new_name)) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Safest way to use Toasts
                                                            Toast.makeText(getApplicationContext(), "The collection already exists!", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                                else {
                                                    System.out.println(i);
                                                    db.addCollection(new_name);
                                                    drawer.getExpandableExtension().collapse(drawer.getPosition(2));
                                                    // https://github.com/mikepenz/MaterialDrawer/issues/2154#issuecomment-355746149
                                                    drawer.getDrawerItem(2).getSubItems().add(
                                                            //Arrays.asList(
                                                                    new DrawerCollectionsItem()
                                                                            .withCollectionName(new_name)
                                                                            .withIdentifier(collections_start + i)
                                                                            .withClickListener(deleteClicked)
                                                            );
                                                    drawer.getAdapter().notifyAdapterDataSetChanged();
                                                    collection_names.add(new_name);
                                                    i += 1;
                                                }
                                            }
                                        })
                                        .build();
                                dialog.show();
                        }
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState);
        drawer = drawerBuilder.build();

        RecyclerView recyclerView = findViewById(R.id.main_activity_recyclerview);
        ItemAdapter itemAdapter = new ItemAdapter();
        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        ArrayList<IItem> items = new ArrayList<IItem>();
        for(int i = 0; i < 10; i++) {
            MainActivityItem item = new MainActivityItem();
            item.confname = "Conf" + i;
            item.confname_nice = "Conf " + i;
            item.year = "201" + i;
            item.category = "cat_" + i;
            item.category_nice = "Cat " + i;
            items.add(item);
        }
        itemAdapter.add(items);

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