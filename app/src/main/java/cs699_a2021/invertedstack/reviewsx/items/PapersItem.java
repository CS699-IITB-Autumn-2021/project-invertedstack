package cs699_a2021.invertedstack.reviewsx.items;

import android.database.Cursor;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.EventHookUtil;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;
import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;
import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;

public class PapersItem extends AbstractItem<PapersItem, PapersItem.ViewHolder> {
    public String title;
    public String authors;
    public String body;
    public String content_id;

    public static class ExpandBodyClickEvent extends ClickEventHook<PapersItem> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof PapersItem.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).expand_body);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<PapersItem> fastAdapter, PapersItem item) {
            if (v.getId() == View.NO_ID) {
                Log.d("PapersItem", "v has no ID");
            } else {
                Log.d("PapersItem", "v has ID = " + v.getResources().getResourceName(v.getId()));
            }
            RelativeLayout expand_body_wrapper = ((ViewGroup) v.getParent().getParent()).findViewById(R.id.paper_card_body_wrapper);
            if (expand_body_wrapper != null) {
                expand_body_wrapper.setVisibility(expand_body_wrapper.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static class CollectionSaveEvent extends ClickEventHook<PapersItem> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof PapersItem.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).collections_button);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<PapersItem> fastAdapter, PapersItem item) {
            ArrayList<String> options = new ArrayList<>();
            ArrayList<Integer> selected_indices = new ArrayList<>();
            ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(v.getContext());
            Cursor collections = db.getAllCollections();
            int i = 0;
            while (collections.moveToNext()) {
                String collection_name = collections.getString(0);
                options.add(collection_name);
                if (db.isPaperInCollection(collection_name, item.content_id)) {
                    selected_indices.add(i);
                }
                i += 1;
            }
            Integer[] sel_idxs = new Integer[selected_indices.size()];
            selected_indices.toArray(sel_idxs);
            MaterialDialog.Builder dialog = new MaterialDialog.Builder(v.getContext())
                    .title("Save to collections")
                    .items(options)
                    .itemsCallbackMultiChoice(sel_idxs, (dialog1, which, text) -> {
                        ArrayList<String> selected = new ArrayList<>();
                        for (CharSequence s : text) {
                            selected.add(s.toString());
                        }
                        for (String name : options) {
                            if (selected.contains(name)) {
                                db.addPaperToCollection(name, item.content_id);
                                Log.d("PapersItem", "Added paper " + item.content_id + " to " + name);
                            } else {
                                db.deletePaperFromCollection(name, item.content_id);
                                Log.d("PapersItem", "Removed paper " + item.content_id + " from " + name);
                            }
                        }
                        return true;
                    })
                    .positiveText("Done");
            dialog.show();
        }
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.paper_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.paper_card;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.title.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
            viewHolder.authors.setText(Html.fromHtml(authors, Html.FROM_HTML_MODE_COMPACT));
            viewHolder.body.setText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT));
        } else {
            viewHolder.title.setText(Html.fromHtml(title));
            viewHolder.authors.setText(Html.fromHtml(authors));
            viewHolder.body.setText(Html.fromHtml(body));
        }
    }

    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.title.setText("");
        viewHolder.authors.setText("");
        viewHolder.body.setText("");
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authors;
        TextView body;
        IconicsTextView expand_body;
        IconicsButton collections_button;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.paper_card_title);
            authors = view.findViewById(R.id.paper_card_authors);
            body = view.findViewById(R.id.paper_card_body);
            expand_body = view.findViewById(R.id.paper_card_expand_body);
            collections_button = view.findViewById(R.id.paper_card_add_collections);
        }
    }
}
