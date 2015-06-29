package com.example.gabrielmojica.spotify3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabrielmojica.spotify3.Parcelables.ParcelableTrack;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */



public class PlayerActivityFragment extends Fragment {

    @Bind(R.id.textView_artistAlbum_player)
    TextView mTextViewArtistAlbum;

    @Bind(R.id.imageView_albumImage_player)
    ImageView mImageViewAlbum;

    @Bind(R.id.textView_trackName_player)
    TextView mTextViewTrackName;

    ParcelableTrack mTrack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();

        Bundle bundle = intent.getExtras();

        String artist = bundle.getString("TRACK_ARTIST");
        String album = bundle.getString("TRACK_ALBUM");
        String preview = bundle.getString("TRACK_PREVIEW");
        String image = bundle.getString("TRACK_IMAGE");
        String name = bundle.getString("TRACK_NAME");



        mTextViewArtistAlbum.setText(artist + "\n" + album);
        mTextViewTrackName.setText(name);

        if (image != null)
            if (!image.isEmpty()) {
                Picasso.with(getActivity())
                        .load(image)
                        .resize(100, 100)
                        .centerCrop()
                        .into(mImageViewAlbum);
            }
        return view;
    }
}
