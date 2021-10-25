package cs699_a2021.invertedstack.reviewsx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageButton;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;

import java.util.List;

public class DrawerCollectionsItem extends BaseDescribeableDrawerItem<DrawerCollectionsItem, DrawerCollectionsItem.ViewHolder> {
    public String collection_name;
    public boolean is_deletable;

    public DrawerCollectionsItem withCollectionName(String name) {
        this.collection_name = name;
        return this;
    }

    public DrawerCollectionsItem withDeletable(boolean deletable) {
        this.is_deletable = deletable;
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        Context ctx = viewHolder.itemView.getContext();
        System.out.println(collection_name);
        //bindViewHelper(viewHolder);
        viewHolder.collection_name.setText(collection_name);
        viewHolder.collection_name.setTextColor(getColor(ctx));
        System.out.println(viewHolder.collection_name.getText());
        if(is_deletable) {
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("You clicked " + collection_name);
                }
            });
        }
        else {
            viewHolder.delete.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        public IconicsImageButton delete;
        public TextView collection_name;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.material_drawer_collection_delete);
            collection_name = view.findViewById(R.id.material_drawer_collection_name);
        }
    }
}
