package com.example.gabrielmojica.spotify3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabrielmojica.spotify3.Parcelables.ParcelableTrack;
import com.example.gabrielmojica.spotify3.R;
import com.example.gabrielmojica.spotify3.TrackActivityFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gabrielmojica on 29/06/2015.
 */
public class TrackAdapter extends ArrayAdapter<ParcelableTrack> {

    private TrackActivityFragment trackActivityFragment;

    public TrackAdapter(TrackActivityFragment trackActivityFragment, Context context, int track_item, int resource, List<ParcelableTrack> tracks) {
        super(context, resource);
        this.trackActivityFragment = trackActivityFragment;
    }

    class ViewHolder {
        @Bind(R.id.textview_trackname)
        TextView textView;
        @Bind(R.id.imageview_albumimage)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolder holder;

        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.track_item, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        ParcelableTrack track = getItem(position);

        holder.textView.setText(track.name + "\n" + track.album);

        if (!track.album.isEmpty()) {
            Picasso.with(trackActivityFragment.getActivity())
                    .load(track.image)
                    .resize(100, 100)
                    .centerCrop()
                    .into(holder.imageView);
        }
        return rowView;
    }
}
