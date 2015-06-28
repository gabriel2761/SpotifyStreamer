package com.example.gabrielmojica.spotify3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackActivityFragment extends Fragment {

    public TrackActivityFragment() {
    }

    private TrackAdapter mTrackAdapter;
    private ArrayList<ParcelableTrack> mTrackList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        Intent intent = getActivity().getIntent();

        mTrackAdapter = new TrackAdapter(
                getActivity(),
                R.layout.track_item,
                R.id.textview_trackname,
                mTrackList
        );

        if (savedInstanceState != null) {
            mTrackList = savedInstanceState.getParcelableArrayList("tracks");
            mTrackAdapter.addAll(mTrackList);
        } else if (intent != null) {
            Bundle extras = intent.getExtras();
            String artistId = extras.getString("ARTIST_ID");
            FetchTracksTask fetchTracksTask = new FetchTracksTask();
            fetchTracksTask.execute(artistId);
        }

        ListView listView = (ListView) view.findViewById(R.id.track_listView);
        listView.setAdapter(mTrackAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tracks", mTrackList);
        super.onSaveInstanceState(outState);
    }

    public class TrackAdapter extends ArrayAdapter<ParcelableTrack> {

        public TrackAdapter(Context context, int track_item, int resource, List<ParcelableTrack> tracks) {
            super(context, resource);
        }

        class ViewHolder {
            public TextView textView;
            public ImageView imageView;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {

            ViewHolder holder;

            if (rowView == null) {
                rowView = LayoutInflater.from(getContext()).inflate(R.layout.track_item, parent, false);
                holder = new ViewHolder();
                holder.textView = (TextView) rowView.findViewById(R.id.textview_trackname);
                holder.imageView = (ImageView) rowView.findViewById(R.id.imageview_albumimage);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            ParcelableTrack track = getItem(position);

            holder.textView.setText(track.getName() + "\n" + track.getAlbum());

            if (!track.mTrackCover.isEmpty()) {
                Picasso.with(getActivity())
                        .load(track.getCover())
                        .resize(100, 100)
                        .centerCrop()
                        .into(holder.imageView);
            }
            return rowView;
        }
    }

    public static class ParcelableTrack implements Parcelable {
        private String mTrackName;
        private String mTrackAlbum;
        private String mTrackCover;

        public String getName() {
            return mTrackName;
        }

        public String getAlbum() {
            return mTrackAlbum;
        }

        public String getCover() {
            return mTrackCover;
        }


        public ParcelableTrack(Track track) {
            mTrackName = track.name;
            mTrackAlbum = track.album.name;
            mTrackCover = track.album.images.get(0).url;
        }

        public ParcelableTrack(Parcel in) {
            mTrackName = in.readString();
            mTrackAlbum = in.readString();
            mTrackCover = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mTrackName);
            dest.writeString(mTrackAlbum);
            dest.writeString(mTrackCover);
        }

        @Override
        public int describeContents() {
            return 0;
        }


        public static final Parcelable.Creator<ParcelableTrack> CREATOR = new Parcelable.Creator<ParcelableTrack>() {
            public ParcelableTrack createFromParcel(Parcel in) {
                return new ParcelableTrack(in);
            }
            public ParcelableTrack[] newArray(int size) {
                return new ParcelableTrack[size];
            }
        };
    }

    private class FetchTracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            // TODO add a try and catch
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");
            Tracks tracks = spotifyService.getArtistTopTrack(params[0], options);
            return tracks.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);
            if (!tracks.isEmpty()) {
                for (Track track : tracks) {
                    mTrackList.add(new ParcelableTrack(track));
                }
                mTrackAdapter.addAll(mTrackList);

            } else {
                String message = "Unable to find top tracks";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
