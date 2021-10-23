package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaperList extends AppCompatActivity {
    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter;

    private String test_json = "[\n" +
            "      {\n" +
            "            \"data_id\": \"ryQu7f-RZ\",\n" +
            "            \"paper_title\": \"On the Convergence of Adam and Beyond\",\n" +
            "            \"forum_link\": \"https://openreview.net/forum?id=ryQu7f-RZ\",\n" +
            "            \"pdf_link\": \"https://openreview.net/pdf?id=ryQu7f-RZ\",\n" +
            "            \"authors\": [\n" +
            "                  \"Sashank J. Reddi\",\n" +
            "                  \"Satyen Kale\",\n" +
            "                  \"Sanjiv Kumar\"\n" +
            "            ],\n" +
            "            \"abstract\": \"Several recently proposed stochastic optimization methods that have been successfully used in training deep networks such as RMSProp, Adam, Adadelta, Nadam are based on using gradient updates scaled by square roots of exponential moving averages of squared past gradients. In many applications, e.g. learning with large output spaces, it has been empirically observed that these algorithms fail to converge to an optimal solution (or a critical point in nonconvex settings). We show that one cause for such failures is the exponential moving average used in the algorithms. We provide an explicit example of a simple convex optimization setting where Adam does not converge to the optimal solution, and describe the precise problems with the previous analysis of Adam algorithm. Our analysis suggests that the convergence issues can be fixed by endowing such algorithms with ``long-term memory'' of past gradients, and propose new variants of the Adam algorithm which not only fix the convergence issues but often also lead to improved empirical performance.\",\n" +
            "            \"tl;dr\": \"We investigate the convergence of popular optimization algorithms like Adam , RMSProp and propose new variants of these methods which provably converge to optimal solution in convex  settings.\",\n" +
            "            \"keywords\": \"optimization, deep learning, adam, rmsprop\"\n" +
            "      }" + // NOTE: VERY IMPORTANT, DON'T HAVE A RUNNING COMMA HERE. JAVA JSON PARSER IS STUPID AS FUCK
            "]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        TextView header = findViewById(R.id.paperlist_header);
        int year = 2020; // Currently only for ICLR
        String category = "oral_presentations";
        header.setText("ICLR" + year + " " + category);

        RecyclerView recyclerView = findViewById(R.id.paperlist_recyclerview);


        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.withEventHook(new PapersItem.ExpandBodyClickEvent());
        fastAdapter.withEventHook(new PapersItem.CollectionSaveEvent());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        recyclerView.setAdapter(fastAdapter);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        String url = "http://2.tcp.ngrok.io:11452/get_parameters?year=" + year + "&category=" + category;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String rcvd_string = response.body().string();
                    test_json = rcvd_string;
                    ArrayList<IItem> items = new ArrayList<>();
                    try {
                        JSONArray array = new JSONArray(test_json);
                        System.out.println(array.length());
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            PapersItem item = new PapersItem();
                            item.content_id = object.getString("data_id");
                            item.title = object.getString("paper_title");
                            String authors_string = "";
                            authors_string += "<b>Authors: </b><i>";
                            // Authors
                            JSONArray authors = object.getJSONArray("authors");
                            for(int j = 0; j < authors.length(); j++) {
                                authors_string += authors.getString(j) + (j == authors.length() - 1 ? "</i>" : ", ");
                                System.out.println(authors_string);
                            }
                            item.authors = authors_string;
                            String body = "";
                            // Abstract -- could be added as a collapsible as a separate entry
                            body += "<br><b>Abstract:</b><br>";
                            body += object.has("abstract") ? object.getString("abstract") : "null";
                            body += "<br><b>Keywords:</b><br><i>";
                            body += object.has("keywords") ? object.getString("keywords") : "null";
                            item.body = body;
                            items.add(item);
                            System.out.println("-----------------------------");
                        }
                    } catch (JSONException e) {
                        // TODO: Show proper error message
                        e.printStackTrace();
                        // TODO: Check if items is empty
                        // If it is empty, show full blown error screen
                        // Else show whatever items we got correct and then show a toast notification
                    }
                    PaperList.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemAdapter.add(items);
                            findViewById(R.id.paperlist_progressbar).setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
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
                itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<PapersItem>() {
                    @Override
                    public boolean filter(PapersItem item, @Nullable CharSequence constraint) {
                        String title = item.title.toLowerCase();
                        String authors = item.authors.toLowerCase();
                        String body = item.body.toLowerCase();
                        String key = constraint.toString().toLowerCase();
                        return (title.contains(key) || authors.contains(key) || body.contains(key));
                    }
                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}