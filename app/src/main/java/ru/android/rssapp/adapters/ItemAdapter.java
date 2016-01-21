package ru.android.rssapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.android.rssapp.dao.Table;

/**
 * Created by Olga on 20.01.2016.
 */
public class ItemAdapter extends CursorAdapter {
    private LayoutInflater mInflater; //нужен для создания объектов класса View

    public ItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public static class ViewHolder {
        public TextView itemTitle;
        public long itemID;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.itemTitle = (TextView)root.findViewById(android.R.id.text1);;
        root.setTag(holder);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(Table.RssItems.COLUMN_ID));
        String itemTitle = cursor.getString(cursor.getColumnIndex(Table.RssItems.COLUMN_TITLE));

        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder != null) {
            holder.itemTitle.setText(itemTitle);
            holder.itemID = id;
        }
    }
}
