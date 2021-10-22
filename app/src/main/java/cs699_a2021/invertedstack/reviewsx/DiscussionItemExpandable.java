package cs699_a2021.invertedstack.reviewsx;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

public class DiscussionItemExpandable<Parent extends IItem & IExpandable, SubItem extends IItem & ISubItem> extends AbstractExpandableItem<DiscussionItemExpandable<Parent, SubItem>, DiscussionItemExpandable.ViewHolder, SubItem> {
    public String title;
    public String body;

    private OnClickListener<DiscussionItemExpandable> mOnClickListener;

    //we define a clickListener in here so we can directly animate
    final private OnClickListener<DiscussionItemExpandable<Parent, SubItem>> onClickListener = new OnClickListener<DiscussionItemExpandable<Parent, SubItem>>() {
        @Override
        public boolean onClick(View v, IAdapter adapter, @NonNull DiscussionItemExpandable item, int position) {
            if (item.getSubItems() != null) {
                if (!item.isExpanded()) {
                    ViewCompat.animate(v.findViewById(R.id.discussion_expand)).rotation(180).start();
                } else {
                    ViewCompat.animate(v.findViewById(R.id.discussion_expand)).rotation(0).start();
                }
                return mOnClickListener == null || mOnClickListener.onClick(v, adapter, item, position);
            }
            return mOnClickListener != null && mOnClickListener.onClick(v, adapter, item, position);
        }
    };
    // Ref: https://github.com/mikepenz/FastAdapter/blob/v3.3.1/app/src/main/java/com/mikepenz/fastadapter/app/items/expandable/SimpleSubExpandableItem.java
    /**
     * we overwrite the item specific click listener so we can automatically animate within the item
     *
     * @return
     */
    @Override
    public OnClickListener<DiscussionItemExpandable<Parent, SubItem>> getOnItemClickListener() {
        return onClickListener;
    }

    @Override
    public boolean isSelectable() {
        //this might not be true for your application
        return getSubItems() == null;
    }
    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.discussion_item_expandable_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.discussion_card;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(title);
        viewHolder.body.setText(body);
        if (getSubItems() == null || getSubItems().size() == 0) {
            viewHolder.icon.setVisibility(View.GONE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
        }

        if (isExpanded()) {
            ViewCompat.setRotation(viewHolder.icon, 0);
        } else {
            ViewCompat.setRotation(viewHolder.icon, 180);
        }
    }

    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.title.setText("");
        viewHolder.body.setText("");
        viewHolder.icon.clearAnimation();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        TextView icon;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.discussion_card_title);
            body = view.findViewById(R.id.discussion_card_body);
            icon = view.findViewById(R.id.discussion_expand);
        }
    }
}
