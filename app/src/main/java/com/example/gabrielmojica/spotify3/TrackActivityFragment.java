package com.example.gabrielmojica.spotify3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gabrielmojica.spotify3.Adapters.TrackAdapter;
import com.example.gabrielmojica.spotify3.Parcelables.ParcelableTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


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

        mTrackAdapter = new TrackAdapter(this,
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class FetchTracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            try {
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotifyService = spotifyApi.getService();
                Map<String, Object> options = new HashMap<>();
                options.put("country", "US");
                Tracks tracks = spotifyService.getArtistTopTrack(params[0], options);
                return tracks.tracks;
            } catch (RetrofitError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);
            if (tracks == null) {
                showToastError("Unable to find top tracks");
            } else if (tracks.isEmpty()){
                showToastError("No top tracks for this Artist");
            } else {
                addTracks(tracks);
            }
        }

        private void addTracks(List<Track> tracks) {
            for (Track track : tracks) {
                mTrackList.add(new ParcelableTrack(track));
            }
            mTrackAdapter.addAll(mTrackList);
        }

        private void showToastError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();;
        }
    }
}
