package com.example.gabrielmojica.spotify3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

    TrackAdapter mTrackAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            String artistId = extras.getString("ARTIST_ID");

            FetchTracksTask fetchTracksTask = new FetchTracksTask();
            fetchTracksTask.execute(artistId);
        }



        mTrackAdapter = new TrackAdapter(
                getActivity(),
                R.layout.track_item,
                R.id.textview_trackname,
                new ArrayList<Track>()
        );

        ListView listView = (ListView) view.findViewById(R.id.track_listView);
        listView.setAdapter(mTrackAdapter);

        return view;
    }

    public class TrackAdapter extends ArrayAdapter<Track> {

        public TrackAdapter(Context context, int track_item, int resource, List<Track> tracks) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_item, parent, false);
            }

            Track track = getItem(position);

            TextView textView = (TextView) convertView.findViewById(R.id.textview_trackname);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_albumimage);

            textView.setText(track.name + "\n" + track.album.name);

            if (!track.album.images.isEmpty()) {
                Picasso.with(getActivity())
                        .load(track.album.images.get(0).url)
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);
            }

            return convertView;
        }
    }

    private class FetchTracksTask extends AsyncTask<String, Void, List<Track>> {

        private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

        @Override
        protected List<Track> doInBackground(String... params) {

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
                mTrackAdapter.addAll(tracks);
            } else {
                String message = "Unable to find top tracks";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
