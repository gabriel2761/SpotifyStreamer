package com.example.gabrielmojica.spotify3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabrielmojica.spotify3.Parcelables.ParcelableArtist;
import com.example.gabrielmojica.spotify3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gabrielmojica on 29/06/2015.
 */
public class ArtistAdapter extends ArrayAdapter<ParcelableArtist> {

    public ArtistAdapter(Context context, int resource, int textViewResourceId, List<ParcelableArtist> artists) {
        super(context, resource, textViewResourceId, artists);
    }

    class ViewHolder {
        @Bind(R.id.textview_artistname)
        TextView textView;
        @Bind(R.id.imageview_artistimage)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.artist_item, parent, false);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        ParcelableArtist artist = getItem(position);

        viewHolder.textView.setText(artist.name);

        if (!artist.image.isEmpty()) {
            Picasso.with(getContext())
                    .load(artist.image)
                    .resize(100, 100)
                    .centerCrop()
                    .into(viewHolder.imageView);
        }
        return rowView;
    }

}
