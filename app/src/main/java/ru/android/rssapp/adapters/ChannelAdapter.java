package ru.android.rssapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ru.android.rssapp.R;
import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;

/**
 * Created by Olga on 20.01.2016.
 */
public class ChannelAdapter extends ArrayAdapter<BaseEntity> {
    public ChannelAdapter(Context context) {
        super(context, 0);
    }
    protected static class ViewHolder {
        TextView date;
        TextView url;
        TextView title;
        ProgressBar progress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.line_channel, parent,false);
            holder.date = (TextView)convertView.findViewById(R.id.date);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.url = (TextView)convertView.findViewById(R.id.url);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progressbar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseEntity item = getItem(position);
        if(item instanceof RssChannel){
            fillForRssChannel((RssChannel)item,holder);
        }else if(item instanceof RssItem){
            fillForRssItem((RssItem) item, holder);
        }
        return convertView;
    }

    protected void fillForRssChannel(RssChannel channel, ViewHolder holder){
        if(channel != null){
            boolean inProgress = channel.isLoadingState();
            holder.progress.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
            holder.title.setVisibility(inProgress ? View.INVISIBLE : View.VISIBLE);
            holder.url.setVisibility(inProgress ? View.INVISIBLE : View.VISIBLE);
            holder.date.setText(channel.getLastBuildDate());
            holder.title.setText(channel.getTitle());
            holder.url.setText(channel.getURL());
        }
    }
    protected void fillForRssItem(RssItem item,ViewHolder holder){
        if(item != null){
            holder.progress.setVisibility( View.INVISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            holder.url.setVisibility(View.GONE);
            holder.title.setText(item.getTitle());
            holder.date.setText(item.getPubdate());
        }
    }
}
