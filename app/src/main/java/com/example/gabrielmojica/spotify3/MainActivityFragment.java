package com.example.gabrielmojica.spotify3;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    ArtistAdapter mArtistAdapter;
    EditText editText;


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
                R.layout.search_item,
                R.id.listview_artists,
                new ArrayList<Artist>()
                );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);

        editText = (EditText) rootView.findViewById(R.id.editText);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    dismissKeyboard();

                    String artist;
                    if (!(artist = editText.getText().toString()).equals("")) {
                        mArtistAdapter.clear();
                        updateArtist(artist);
                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void updateArtist(String artistName) {
        FetchArtistTask fetchMusic = new FetchArtistTask();
        fetchMusic.execute(artistName);
    }


    public class ArtistAdapter extends ArrayAdapter<Artist> {

        public ArtistAdapter(Context context, int resource, int textViewResourceId, List<Artist> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_item, parent, false);
            }

            Artist artist = getItem(position);

            TextView textView = (TextView) convertView.findViewById(R.id.textview_artistname);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_artistimage);

            textView.setText(artist.name.toString());

            if (!artist.images.isEmpty()) {
                Picasso.with(getActivity())
                        .load(artist.images.get(0).url)
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);
            }

            return convertView;
        }
    }

    protected class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);
            return results.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);

            if (!artists.isEmpty()) {
                mArtistAdapter.addAll(artists);
            } else {
                String message = "Unable to find " + editText.getText().toString();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
