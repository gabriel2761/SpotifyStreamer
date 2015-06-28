package com.example.gabrielmojica.spotify3;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gabrielmojica.spotify3.Adapters.ArtistAdapter;
import com.example.gabrielmojica.spotify3.Parcelables.ParcelableArtist;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static Toast toast;

    @Bind(R.id.search_artist) SearchView mSearchView;
    ArtistAdapter mArtistAdapter;
    ArrayList<ParcelableArtist> artistList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mArtistAdapter = new ArtistAdapter(
                getActivity(),
                R.layout.artist_item,
                R.id.listview_artists,
                artistList
                );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableArtist artist = mArtistAdapter.getItem(position);

                Bundle extras = new Bundle();
                extras.putString("ARTIST_ID", artist.id);
                extras.putString("ARTIST_NAME", artist.name);

                Intent intent = new Intent(getActivity(), TrackActivity.class);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        mSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        dismissKeyboard();
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        updateArtist(newText);
                        return false;
                    }
                });
        return rootView;
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }

    public void updateArtist(String artistName) {
        FetchArtistTask fetchMusic = new FetchArtistTask();
        fetchMusic.execute(artistName);
    }

    protected class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {
            if (isNetworkAvailable()) {
                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    ArtistsPager results = spotify.searchArtists(params[0]);
                    return results.artists.items;
                } catch (RetrofitError e) {
                    return null;
                }
            }
            return null;
        }
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);
            if (artists != null) {
                mArtistAdapter.clear();
                if (!artists.isEmpty()) {

                    for (Artist artist : artists) {
                        mArtistAdapter.addAll(new ParcelableArtist(artist));
                    }
                } else {
                    showToast("Unable to find " + mSearchView.getQuery());
                }
            }
        }

        private void showToast(String message) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

}
