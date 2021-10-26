package cs699_a2021.invertedstack.reviewsx;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;
import cs699_a2021.invertedstack.reviewsx.items.DiscussionItemExpandable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This activity shows a particular paper info along with it's discussion (i.e. comments from the forum).
 * The comments are nested so the JSON parsing and RecyclerView updates need to be done very carefully.
 * This activity also has button to view the PDF and button to view the notes for this paper.
 */
public class PaperWithDiscussion extends AppCompatActivity {
    /**
     * Instance of FastAdapter for the comments
     */
    private FastAdapter fastAdapter;
    /**
     * Instance of ExpandableExtension for FastAdapter -- we technically don't need this as a member but it's
     * nice to have it as a member for future extensions with "auto" collapse and "conditioned" collapse
     */
    private ExpandableExtension expandableExtension;
    /**
     * Name of the conference the paper belongs to
     */
    String conf_name = null;
    /**
     * Year of the conference the paper belongs to
     */
    String year = null;
    /**
     * Category within the conference the paper belongs to
     */
    String category = null;
    /**
     * Paper ID of the paper
     */
    String data_id = null;
    /**
     * Title of the paper
     */
    String paper_title = null;
    /**
     * String hosting the main response JSON on fetching the comments
     */
    private String main_json = null;

    /**
     * Recursive function building a `DiscussionItemExpandable` item from the given JSON
     * The JSON is nested hence the recursion
     * @param obj - The actual JSON object
     * @param padding - Controls how far right the generated element should be. Higher the depth of comment, higher the padding
     * @return - A `DiscussionItemExpandable` item corresponding to `obj`
     */
    DiscussionItemExpandable get_item_from_json(JSONObject obj, int padding) {
        DiscussionItemExpandable ret = new DiscussionItemExpandable();
        try {
            JSONObject content = obj.getJSONObject("content");
            if(content.has("title")) {
                ret.title = content.getString("title");
                content.remove("title");
            }
            else {
                ret.title = "(Not Available)";
            }
            String body = "";
            Iterator<String> keys = content.keys();
            // ref - https://stackoverflow.com/a/10593838
            while(keys.hasNext()) {
                String key = keys.next();
                body += "<b>" + key + "</b><br>" + content.getString(key).replaceAll("\n", "<br>") + "<br>";
            }
            ret.body = body;
            ret.padding_left = padding;
            if(obj.has("reply")) {
                JSONArray reply = obj.getJSONArray("reply");
                if (reply != null && reply.length() > 0) {
                    ArrayList<IItem> subItems = new ArrayList<>();
                    for (int i = 0; i < reply.length(); i++) {
                        // TODO: Make the increase controllable elsewhere
                        subItems.add(get_item_from_json(reply.getJSONObject(i), padding + 16));
                    }
                    ret.withSubItems(subItems);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "JSON parsing error. Contact server admin", Toast.LENGTH_LONG).show());
        }

        return ret;
    }

    /**
     * onCreate method for the activity. This method will look up the DB for discussion data
     * If the data exists locally, show it directly else fetch it from the server
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_with_discussion);

        Toolbar toolbar = findViewById(R.id.paper_discussion_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Discussion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if(b == null) {
            // TODO: Sow proper error screen
            Toast.makeText(PaperWithDiscussion.this, "This intent is illegal", Toast.LENGTH_LONG);
            return;
        }
        else {
            conf_name = b.getString("conf_name");
            year = b.getString("year");
            category = b.getString("category");
            data_id = b.getString("data_id");
        }

        TextView title = findViewById(R.id.paper_discussion_title);
        TextView authors = findViewById(R.id.paper_discussion_authors);
        TextView body = findViewById(R.id.paper_discussion_body);

        RecyclerView recyclerView = findViewById(R.id.discussion_recyclerview);

        ItemAdapter itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);

        expandableExtension = new ExpandableExtension();
        fastAdapter.addExtension(expandableExtension);
        fastAdapter.withEventHook(new DiscussionItemExpandable.ExpandBodyClickEvent());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        ArrayList<IItem> items = new ArrayList<>();
        // See if the received data_id exists under the database
        ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(PaperWithDiscussion.this);
        Cursor data = db.getPaperByDataID(data_id);
        if(data.getCount() == 0) {
            title.setText("No paper title for paper ID = " + data_id);
            authors.setText("No authors info for paper ID = " + data_id);
            body.setText("No paper info for paper ID = " + data_id);
        }
        else {
            data.moveToFirst();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                title.setText(Html.fromHtml(data.getString(1), Html.FROM_HTML_MODE_COMPACT));
                authors.setText(Html.fromHtml(data.getString(2), Html.FROM_HTML_MODE_COMPACT));
                body.setText(Html.fromHtml(data.getString(3), Html.FROM_HTML_MODE_COMPACT));
                paper_title = data.getString(1);
            }
            else {
                title.setText(Html.fromHtml(data.getString(1)));
                authors.setText(Html.fromHtml(data.getString(2)));
                body.setText(Html.fromHtml(data.getString(3)));
            }
        }

        Cursor comments_data = db.getCommentsForPaperID(data_id);
        if(comments_data.getCount() != 0) {
            comments_data.moveToFirst();
            main_json = comments_data.getString(1);
            try {
                JSONArray comments = new JSONArray(main_json);
                for(int i = 0; i < comments.length(); i++) {
                    items.add(get_item_from_json(comments.getJSONObject(i), 0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemAdapter.add(items);
            findViewById(R.id.paper_discussion_progressbar).setVisibility(View.GONE);
            return;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        String url = getString(R.string.server_url) + "/get_comments?conference=" + conf_name + "&year=" + year + "&category=" + category + "&data_id=" + data_id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        String finalData_id = data_id;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String rcvd_string = response.body().string();
                    main_json = rcvd_string;
                    db.updateCommentsData(finalData_id, main_json);
                    try {
                        JSONArray comments = new JSONArray(main_json);
                        for(int i = 0; i < comments.length(); i++) {
                            items.add(get_item_from_json(comments.getJSONObject(i), 0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {
                        itemAdapter.add(items);
                        findViewById(R.id.paper_discussion_progressbar).setVisibility(View.GONE);
                    });
                }
                else {
                    runOnUiThread(() -> Toast.makeText(PaperWithDiscussion.this,
                            "Error in response, please contact admin",
                            Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PaperWithDiscussion.this,
                        "Error in response, please contact admin",
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    /**
     * Menu inflater for the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.discussion_menu, menu);
        return true;
    }

    /**
     * Menu click handler for the activity.
     * This either takes you to a WebIntent for viewing the PDF or takes you to `NoteTakingActivity` for viewing/editing notes
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.discussion_action_pdf:
                Uri uri = Uri.parse(getString(R.string.openreview_pdf_link_template) + data_id);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(webIntent);
                return true;
            case R.id.discussion_action_view_notes:
                Intent noteIntent = new Intent(PaperWithDiscussion.this, NoteTakingActivity.class);
                noteIntent.putExtra("data_id", data_id);
                noteIntent.putExtra("paper_title", paper_title);
                startActivity(noteIntent);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}