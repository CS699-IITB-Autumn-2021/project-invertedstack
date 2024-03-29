package cs699_a2021.invertedstack.reviewsx.items;

import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.utils.EventHookUtil;

import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;

/**
 * Expandable `DiscussionItem` for the use in RecyclerView of `PaperWithDiscussion` activity. This implements
 * expandable sub items feature that is needed in case of nested comments. Note that this item corresponds to
 * a single comment -- this comment can ether be toplevel (padding_left=0) or child (padding_left != 0)
 * <p>
 * `DiscussionItemExpandable` can be a subItem of `DiscussionItemExpandable` too -- which is much more convenient
 * than having 2 separate types of items. In future, `DiscussionItem` should be deprecated
 * <p>
 * As with other items, most of the methods in this class are just setting up FastAdapter with correct layout
 *
 * @param <Parent>
 * @param <SubItem>
 */
public class DiscussionItemExpandable<Parent extends IItem & IExpandable, SubItem extends IItem & ISubItem> extends AbstractExpandableItem<DiscussionItemExpandable<Parent, SubItem>, DiscussionItemExpandable.ViewHolder, SubItem> {
    /**
     * Title of the comment
     */
    public String title;
    /**
     * Body of the comment
     */
    public String body;
    /**
     * Value of left padding for the layout of this comment
     */
    public int padding_left;

    private OnClickListener<DiscussionItemExpandable> mOnClickListener;
    /**
     * onClickListener for directly animating the expand children animation
     */
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

    //we define a clickListener in here so we can directly animate

    @Override
    public OnClickListener<DiscussionItemExpandable<Parent, SubItem>> getOnItemClickListener() {
        return onClickListener;
    }
    // Ref: https://github.com/mikepenz/FastAdapter/blob/v3.3.1/app/src/main/java/com/mikepenz/fastadapter/app/items/expandable/SimpleSubExpandableItem.java

    @Override
    public boolean isSelectable() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.title.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
            viewHolder.body.setText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT));
        } else {
            viewHolder.title.setText(Html.fromHtml(title));
            viewHolder.body.setText(Html.fromHtml(body));
        }
        // ref: https://stackoverflow.com/a/6327095
        float scale = Resources.getSystem().getDisplayMetrics().density;
        viewHolder.container.setPadding(((int) scale * padding_left), 0, 0, 0);
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

    /**
     * EventHook handling the "expand body" event corresponding to the layout of the item
     */
    public static class ExpandBodyClickEvent extends ClickEventHook<DiscussionItemExpandable> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof DiscussionItemExpandable.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).expand_body);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<DiscussionItemExpandable> fastAdapter, DiscussionItemExpandable item) {
            if (v.getId() == View.NO_ID) {
                Log.d("DiscussionItemExpandable", "v has no ID");
            } else {
                Log.d("DiscussionItemExpandable", "v has ID = " + v.getResources().getResourceName(v.getId()));
            }
            RelativeLayout expand_body_wrapper = ((ViewGroup) v.getParent().getParent()).findViewById(R.id.discussion_card_body_wrapper);
            if (expand_body_wrapper != null) {
                expand_body_wrapper.setVisibility(expand_body_wrapper.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        TextView icon;
        RelativeLayout body_wrapper;
        LinearLayout container;
        TextView expand_body;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.discussion_card_title);
            body = view.findViewById(R.id.discussion_card_body);
            icon = view.findViewById(R.id.discussion_expand);
            body_wrapper = view.findViewById(R.id.discussion_card_body_wrapper);
            container = view.findViewById(R.id.discussion_card_parent_linear);
            expand_body = view.findViewById(R.id.discussion_card_expand_body);
        }
    }
}
