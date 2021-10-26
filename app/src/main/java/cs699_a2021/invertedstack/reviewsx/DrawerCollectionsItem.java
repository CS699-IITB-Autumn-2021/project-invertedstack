package cs699_a2021.invertedstack.reviewsx;

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

public class DrawerCollectionsItem extends BaseDescribeableDrawerItem<DrawerCollectionsItem, DrawerCollectionsItem.ViewHolder> {
    public String collection_name;
    public View.OnClickListener deleteClicked;

    public DrawerCollectionsItem withCollectionName(String name) {
        this.collection_name = name;
        return this;
    }

    public DrawerCollectionsItem withClickListener(View.OnClickListener listener) {
        this.deleteClicked = listener;
        return this;
    }

    public static class RemoveViewEvent extends ClickEventHook<DrawerCollectionsItem> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if(viewHolder instanceof DrawerCollectionsItem.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder)viewHolder).delete);
            }
            return super.onBindMany(viewHolder);
        }
        @Override
        public void onClick(View v, int position, FastAdapter<DrawerCollectionsItem> fastAdapter, DrawerCollectionsItem item) {
            System.out.println("I'm inside the motherfucking fastadapter callback ! FIRST TIME CODING BABY !! I'M A FUCKING GOD IF THIS WORKS LOL");
            ReviewsXDatabaseHelper db = new ReviewsXDatabaseHelper(v.getContext());
            db.deleteCollection(item.collection_name);
            for(int i = 0; i < fastAdapter.getAdapter(1).getAdapterItemCount(); i++) {
                System.out.println(i + " " + ((IItem)fastAdapter.getItem(i)).getClass().toString());
            }
            System.out.println(position);
            db.deleteCollection(item.collection_name);
            fastAdapter.getAdapter(1).getAdapterItems().remove(position-1);
            fastAdapter.notifyAdapterDataSetChanged();
        }
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
        //bindViewHelper(viewHolder);
        viewHolder.collection_name.setText(collection_name);
        viewHolder.collection_name.setTextColor(getColor(ctx));
        viewHolder.delete.setOnClickListener(deleteClicked);
        onPostBindView(this, viewHolder.itemView);
    }

    public static class ViewHolder extends BaseViewHolder {
        final public IconicsImageButton delete;
        final public TextView collection_name;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.material_drawer_collection_delete);
            collection_name = view.findViewById(R.id.material_drawer_collection_name);
        }
    }
}
