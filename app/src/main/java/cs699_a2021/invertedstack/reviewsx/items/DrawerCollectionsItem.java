package cs699_a2021.invertedstack.reviewsx.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.EventHookUtil;
import com.mikepenz.iconics.view.IconicsImageButton;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;

import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;
import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;

/**
 * Item for showing the collection in the MaterialDrawer in the MainActivity
 *
 * As with other items, most of the methods in this class are just setting up FastAdapter with correct layout
 */
public class DrawerCollectionsItem extends BaseDescribeableDrawerItem<DrawerCollectionsItem, DrawerCollectionsItem.ViewHolder> {
    /**
     * Name of the collection
     */
    public String collection_name;
    /**
     * onClickListener for "delete" action -- it's much more convenient to have this passed to the class rather than define it here
     */
    public View.OnClickListener deleteClicked;
    /**
     * onClickListener for "edit" action -- it's much more convenient to have this passed to the class rather than define it here
     */
    public View.OnClickListener editClicked;

    public DrawerCollectionsItem withCollectionName(String name) {
        this.collection_name = name;
        return this;
    }

    public DrawerCollectionsItem withDeleteClickListener(View.OnClickListener listener) {
        this.deleteClicked = listener;
        return this;
    }
    public DrawerCollectionsItem withEditClickListener(View.OnClickListener listener) {
        this.editClicked = listener;
        return this;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.drawer_collection_deletable_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.drawer_collections_deleteable;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        Context ctx = viewHolder.itemView.getContext();
        viewHolder.itemView.setId(R.id.drawer_collection_deletable_item_id);
        viewHolder.collection_name.setText(collection_name);
        viewHolder.collection_name.setTextColor(getColor(ctx));
        viewHolder.delete.setOnClickListener(deleteClicked);
        viewHolder.edit.setOnClickListener(editClicked);
        onPostBindView(this, viewHolder.itemView);
    }

    public static class ViewHolder extends BaseViewHolder {
        final public IconicsImageButton delete;
        final public IconicsImageButton edit;
        final public TextView collection_name;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.material_drawer_collection_delete);
            edit = view.findViewById(R.id.material_drawer_collection_edit);
            collection_name = view.findViewById(R.id.material_drawer_collection_name);
        }
    }
}
