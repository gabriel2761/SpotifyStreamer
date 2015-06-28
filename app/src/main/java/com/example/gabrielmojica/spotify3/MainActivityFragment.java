package com.example.gabrielmojica.spotify3;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private ArtistAdapter mArtistAdapter;
    private SearchView mSearchView;
    private static Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistAdapter = new ArtistAdapter(
                getActivity(),
                R.layout.artist_item,
                R.id.listview_artists,
                new ArrayList<Artist>()
                );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mArtistAdapter.getItem(position);

                Bundle extras = new Bundle();
                extras.putString("ARTIST_ID", artist.id);
                extras.putString("ARTIST_NAME", artist.name);

                Intent intent = new Intent(getActivity(), TrackActivity.class);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        mSearchView = (SearchView) rootView.findViewById(R.id.search_artist);

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

    public class ArtistAdapter extends ArrayAdapter<Artist> {

        public ArtistAdapter(Context context, int resource, int textViewResourceId, List<Artist> artists) {
            super(context, resource, textViewResourceId, artists);
        }

        class ViewHolder {
            TextView textView;
            ImageView imageView;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (rowView == null) {
                rowView = LayoutInflater.from(getContext()).inflate(R.layout.artist_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) rowView.findViewById(R.id.textview_artistname);
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageview_artistimage);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            Artist artist = getItem(position);

            viewHolder.textView.setText(artist.name);

            if (!artist.images.isEmpty()) {
                Picasso.with(getActivity())
                        .load(artist.images.get(0).url)
                        .resize(100, 100)
                        .centerCrop()
                        .into(viewHolder.imageView);
            }
            return rowView;
        }

    }

    protected class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(params[0]);
                return results.artists.items;
            } catch (RetrofitError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);
            if (artists != null) {
                mArtistAdapter.clear();
                if (!artists.isEmpty()) {
                    mArtistAdapter.addAll(artists);
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
