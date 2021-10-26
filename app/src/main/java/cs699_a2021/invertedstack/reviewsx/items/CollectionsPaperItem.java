package cs699_a2021.invertedstack.reviewsx.items;

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

import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;
import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;

/**
 * Item for the RecyclerView of `ViewCollectionActivity`
 * <p>
 * As with other items, most of the methods in this class are just setting up FastAdapter with correct layout
 */
public class CollectionsPaperItem extends AbstractItem<CollectionsPaperItem, CollectionsPaperItem.ViewHolder> {
    /**
     * Name of the conference for the paper
     */
    public String conf;
    /**
     * Year of the conference for the paper
     */
    public String year;
    /**
     * Category of the paper within the conference
     */
    public String category;
    /**
     * Title of the paper
     */
    public String title;
    /**
     * Authors of the paper
     */
    public String authors;
    /**
     * More info regarding the paper (one line summary, abstract etc.)
     */
    public String body;
    /**
     * ID of the paper
     */
    public String content_id;
    /**
     * Name of the collection this paper belongs to
     */
    public String collection_name;

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.collection_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.collection_card;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.conf.setText(conf);
        viewHolder.year.setText(year);
        viewHolder.category.setText(category);
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
        viewHolder.conf.setText("");
        viewHolder.year.setText("");
        viewHolder.category.setText("");
        viewHolder.title.setText("");
        viewHolder.authors.setText("");
        viewHolder.body.setText("");
    }

    /**
     * EventHook handling the "expand body" event corresponding to the layout of the item
     */
    public static class ExpandBodyClickEvent extends ClickEventHook<CollectionsPaperItem> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof CollectionsPaperItem.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).expand_body);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<CollectionsPaperItem> fastAdapter, CollectionsPaperItem item) {
            if (v.getId() == View.NO_ID) {
                Log.d("CollectionsPaperItem", "v has no ID");
            } else {
                Log.d("CollectionsPaperItem", "v has ID = " + v.getResources().getResourceName(v.getId()));
            }
            RelativeLayout expand_body_wrapper = ((ViewGroup) v.getParent().getParent()).findViewById(R.id.collection_card_body_wrapper);
            if (expand_body_wrapper != null) {
                expand_body_wrapper.setVisibility(expand_body_wrapper.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * EventHook handling the "remove paper from the collection" event
     */
    public static class CollectionRemoveEvent extends ClickEventHook<CollectionsPaperItem> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof CollectionsPaperItem.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).collections_button);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<CollectionsPaperItem> fastAdapter, CollectionsPaperItem item) {
            ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(v.getContext());
            MaterialDialog.Builder dialog = new MaterialDialog.Builder(v.getContext())
                    .title("Are you sure")
                    .content("You are about to remove this paper from the collection")
                    .positiveText("I'm sure")
                    .negativeText("No, I'll keep it")
                    .onPositive((dialog1, which) -> {
                        db.deletePaperFromCollection(item.collection_name, item.content_id);
                        fastAdapter.getAdapter(0).getAdapterItems().remove(position);
                        fastAdapter.notifyAdapterDataSetChanged();
                    });
            dialog.show();
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authors;
        TextView body;
        TextView conf;
        TextView year;
        TextView category;
        IconicsTextView expand_body;
        IconicsButton collections_button;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.collection_card_title);
            authors = view.findViewById(R.id.collection_card_authors);
            body = view.findViewById(R.id.collection_card_body);
            conf = view.findViewById(R.id.collection_card_conference);
            year = view.findViewById(R.id.collection_card_year);
            category = view.findViewById(R.id.collection_card_category);
            expand_body = view.findViewById(R.id.collection_card_expand_body);
            collections_button = view.findViewById(R.id.collection_card_change_collections);
        }
    }
}
