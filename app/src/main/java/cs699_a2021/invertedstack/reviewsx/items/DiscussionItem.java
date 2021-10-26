package cs699_a2021.invertedstack.reviewsx.items;

import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;

/**
 * Item for RecyclerView of Discussion/Comment activity in case the comment has no children
 * <p>
 * As with other items, most of the methods in this class are just setting up FastAdapter with correct layout
 */
public class DiscussionItem extends AbstractItem<DiscussionItem, DiscussionItem.ViewHolder> {
    /**
     * Title of the comment
     */
    public String title;
    /**
     * Body of the comment
     */
    public String body;
    /**
     * Left padding for the layout of this comment. 0 for root level, 16 for 1st level children, 32 for 2nd and so on
     */
    public int padding_left;

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.discussion_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.discussion_card;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<DiscussionItem> {
        TextView title;
        TextView body;
        LinearLayout container;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.discussion_card_title);
            body = view.findViewById(R.id.discussion_card_body);
            container = view.findViewById(R.id.discussion_card_parent_linear);
        }

        @Override
        public void bindView(DiscussionItem item, List<Object> payloads) {
            // ref - https://stackoverflow.com/a/2116191
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                title.setText(Html.fromHtml(item.title, Html.FROM_HTML_MODE_COMPACT));
                body.setText(Html.fromHtml(item.body, Html.FROM_HTML_MODE_COMPACT));
            } else {
                title.setText(Html.fromHtml(item.title));
                body.setText(Html.fromHtml(item.body));
            }
            // ref: https://stackoverflow.com/a/6327095
            float scale = Resources.getSystem().getDisplayMetrics().density;
            container.setPadding(((int) scale * item.padding_left), 0, 0, 0);
        }

        @Override
        public void unbindView(DiscussionItem item) {
            title.setText("");
            body.setText("");
        }
    }
}