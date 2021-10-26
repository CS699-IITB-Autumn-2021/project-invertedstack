package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;
import cs699_a2021.invertedstack.reviewsx.items.DrawerCollectionsItem;
import cs699_a2021.invertedstack.reviewsx.items.MainActivityItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * MainActivity for the app
 * This activity will fetch names of the conferences, years and categories from the server and show the options
 * On clicking any of the conference items [name, year, category], `PaperList` activity will start showing the
 * list of papers in that particular category for that year in that conference
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The `header` part of the `MaterialDrawer`
     */
    private AccountHeader header;
    /**
     * The `DrawerBuilder` used for building `MaterialDrawer`
     */
    private DrawerBuilder drawerBuilder;
    /**
     * The actual `MaterialDrawer` object
     */
    private Drawer drawer;
    /**
     * List of collections as a `MaterialDrawer.ExpandableDrawerItem` instance -- this is updated dynamically based on added/deleted/edited collections
     */
    ExpandableDrawerItem collections;
    /**
     * List of collection names (Strings) -- this is also updated dynamically based on added/deleted/edited collections
     */
    ArrayList<String> collection_names;
    /**
     * Instance of database helper
     */
    ReviewsXDatabaseHelper db;
    int collections_start = 100, i = 0;
    /**
     * Click listener for listening to "delete" action on a collection. Note that the 4 default collections cannot be deleted
     */
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

    /**
     * Click listener for listening to "edit" action on a collection. Note that the 4 default collections cannot be edited
     */
    private View.OnClickListener editClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup parent = (ViewGroup) v.getParent();
            TextView textView = parent.findViewById(R.id.material_drawer_collection_name);
            String old_name = (String) textView.getText();
            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Edit collection name")
                    .input("Enter new name of the collection", old_name, (dialog1, input) -> {
                        String new_name = input.toString();
                        if(collection_names.contains(new_name) || new_name.equals("All notes")) {
                            runOnUiThread(() -> {
                                // Safest way to use Toasts
                                Toast.makeText(getApplicationContext(), "The collection already exists!", Toast.LENGTH_LONG).show();
                            });
                        }
                        else {
                            db.renameCollection(old_name, new_name);
                            drawer.getExpandableExtension().collapse(drawer.getPosition(2));
                            List items = drawer.getDrawerItem(2).getSubItems();
                            int idx = collection_names.indexOf(old_name);
                            DrawerCollectionsItem item = (DrawerCollectionsItem) items.get(idx);
                            item.collection_name = new_name;
                            items.set(idx, item);
                            collection_names.set(idx, new_name);
                            drawer.getDrawerItem(2).withSubItems(items);
                            drawer.getAdapter().notifyAdapterDataSetChanged();
                        }
                    })
                    .build();
            dialog.show();

        }
    };

    /**
     * This will update collection names by fetching the names from the SQLite database
     * Note that first 4 collections are special and they cannot be deleted
     */
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
                        new DrawerCollectionsItem().withCollectionName(name).withIdentifier(collections_start + i).withDeleteClickListener(deleteClicked).withEditClickListener(editClicked)
                );
            }
            collection_names.add(name);
            Log.d("MainActivity", name + collections_start + i);
            i += 1;
        }
    }

    /**
     * `onCreate` method for MainActivity. This method is responsible for fetching the conference data,
     * laying it out in recyclerView and handling clicks for drawer items and recyclerView items
     * @param savedInstanceState
     */
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
                        new SecondaryDrawerItem().withName("New Collection").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(3),
                        new SecondaryDrawerItem().withName("All Notes").withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(4),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_github).withIdentifier(5)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Log.d("MainActivity", "drawerItem ID = " + drawerItem.getIdentifier());
                    Log.d("MainActivity", "View " + view.getClass());
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
                                            if(collection_names.contains(new_name) || new_name.equals("All notes")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Safest way to use Toasts
                                                        Toast.makeText(getApplicationContext(), "The collection already exists!", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                            else {
                                                Log.d("MainActivity", "i = " + i);
                                                db.addCollection(new_name);
                                                drawer.getExpandableExtension().collapse(drawer.getPosition(2));
                                                // https://github.com/mikepenz/MaterialDrawer/issues/2154#issuecomment-355746149
                                                drawer.getDrawerItem(2).getSubItems().add(
                                                        //Arrays.asList(
                                                                new DrawerCollectionsItem()
                                                                        .withCollectionName(new_name)
                                                                        .withIdentifier(collections_start + i)
                                                                        .withDeleteClickListener(deleteClicked)
                                                                        .withEditClickListener(editClicked)
                                                        );
                                                drawer.getAdapter().notifyAdapterDataSetChanged();
                                                collection_names.add(new_name);
                                                i += 1;
                                            }
                                        }
                                    })
                                    .build();
                            dialog.show();
                        case 4:
                            // All notes
                            Intent intent = new Intent(MainActivity.this, ViewCollectionActivity.class);
                            intent.putExtra("collection_name", "All notes");
                            startActivity(intent);
                        case 5:
                            // ref - https://github.com/mikepenz/AboutLibraries/tree/v6.2.3
                            new LibsBuilder()
                                    .withActivityStyle(Libs.ActivityStyle.DARK)
                                    .withAboutIconShown(true)
                                    .withVersionShown(true)
                                    .withAboutDescription("This app is our project for CS699 software lab. Following are the open source libraries with their licenses and authors we used in our project")
                                    .start(MainActivity.this);
                    }
                    return true;
                })
                .withSavedInstance(savedInstanceState);
        drawer = drawerBuilder.build();

        RecyclerView recyclerView = findViewById(R.id.main_activity_recyclerview);
        ItemAdapter itemAdapter = new ItemAdapter();
        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withOnClickListener((OnClickListener<MainActivityItem>) (v, adapter, item, position) -> {
            Intent intent = new Intent(MainActivity.this, PaperList.class);
            intent.putExtra("confname", item.confname);
            intent.putExtra("confname_nice", item.confname_nice);
            intent.putExtra("year", item.year);
            intent.putExtra("category", item.category);
            intent.putExtra("category_nice", item.category_nice);
            startActivity(intent);
            return true;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);
        ArrayList<IItem> items = new ArrayList<>();

        // We ALWAYS fetch the data -- regardless of the network state
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        String url = getString(R.string.server_url) + "/get_info";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Network Error. Check your internet connection or contact the server admin", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String rcvd_string = response.body().string();
                    String confname, confname_nice, year, category, category_nice;
                    try {
                        JSONArray array = new JSONArray(rcvd_string);
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject conf = array.getJSONObject(i);
                            confname = conf.getString("name");
                            confname_nice = conf.getString("readable_name");
                            JSONArray data = conf.getJSONArray("data");
                            for(int j = 0; j < data.length(); j++) {
                                JSONObject year_obj = data.getJSONObject(j);
                                year = year_obj.getString("year");
                                JSONObject cats = year_obj.getJSONObject("categories");
                                Iterator<String> keys = cats.keys();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    category = key;
                                    category_nice = cats.getString(key);
                                    MainActivityItem item = new MainActivityItem();
                                    item.confname = confname;
                                    item.confname_nice = confname_nice;
                                    item.year = year;
                                    item.category = category;
                                    item.category_nice = category_nice;
                                    items.add(item);
                                }
                            }
                        }
                        runOnUiThread(() -> itemAdapter.add(items));
                    } catch(JSONException e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "There was an error while parsing JSON. Contact server admin", Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }
}