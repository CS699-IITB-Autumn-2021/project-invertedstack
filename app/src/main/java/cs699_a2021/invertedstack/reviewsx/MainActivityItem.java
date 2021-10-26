package cs699_a2021.invertedstack.reviewsx;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class MainActivityItem extends AbstractItem<MainActivityItem, MainActivityItem.ViewHolder> {
    public String confname;
    public String confname_nice;
    public String year;
    public String category;
    public String category_nice;

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.main_activity_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.main_activity_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<MainActivityItem> {
        TextView name_and_year;
        TextView category;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_and_year = itemView.findViewById(R.id.conf_name_and_year);
            category = itemView.findViewById(R.id.conf_category);
        }

        @Override
        public void bindView(MainActivityItem item, List<Object> payloads) {
            name_and_year.setText(item.confname_nice + " " + item.year);
            category.setText(item.category_nice);
        }

        @Override
        public void unbindView(MainActivityItem item) {
            name_and_year.setText("");
            category.setText("");
        }
    }
}
