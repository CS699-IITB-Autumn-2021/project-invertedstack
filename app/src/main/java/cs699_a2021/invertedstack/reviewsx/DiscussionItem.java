package cs699_a2021.invertedstack.reviewsx;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class DiscussionItem extends AbstractItem<DiscussionItem, DiscussionItem.ViewHolder> {
    public String title;
    public String body;
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
            title.setText(item.title);
            body.setText(item.body);
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