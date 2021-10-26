package cs699_a2021.invertedstack.reviewsx.items;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import cs699_a2021.invertedstack.reviewsx.R;

/**
 * Item displaying the name of the conference, year of the conference and a particular category
 * in the MainActivity
 *
 * As with other items, most of the methods in this class are just setting up FastAdapter with correct layout
 */
public class MainActivityItem extends AbstractItem<MainActivityItem, MainActivityItem.ViewHolder> {
    /**
     * Name of the conference -- this will be used in API calls further down the line
     */
    public String confname;
    /**
     * "Nicer" name of the conference -- this will be used for actually showing the name of the conference
     */
    public String confname_nice;
    /**
     * Year of the cofnerence
     */
    public String year;
    /**
     * Name of the category -- this will be used in API calls futher down the line
     */
    public String category;
    /**
     * "Nicer" name of the category -- this will be used for actually showing the name of the category
     */
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
