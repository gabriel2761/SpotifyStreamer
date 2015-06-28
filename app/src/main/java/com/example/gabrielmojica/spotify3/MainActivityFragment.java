package com.example.gabrielmojica.spotify3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private ArtistAdapter mArtistAdapter;
    private EditText mEditText;
    private ArrayList<ArtistParcelable> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {

        } else {
            list = savedInstanceState.getParcelableArrayList("key");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", list);
        super.onSaveInstanceState(outState);
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

        mEditText = (EditText) rootView.findViewById(R.id.editText);

        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    dismissKeyboard();

                    String artist;
                    if (!(artist = mEditText.getText().toString()).equals("")) {
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
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public void updateArtist(String artistName) {
        FetchArtistTask fetchMusic = new FetchArtistTask();
        fetchMusic.execute(artistName);
    }

    public static class ArtistParcelable implements Parcelable {
        private String artistName = null;
        private String artistImage = null;

        public ArtistParcelable(Artist artist) {
            this.artistName = artist.name;
            if (!artist.images.isEmpty()) {
                this.artistImage = artist.images.get(0).url;
            }
        }

        public ArtistParcelable(Parcel in) {
            artistName = in.readString();
            artistImage = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(artistName);
            dest.writeString(artistImage);
        }

        public static final Parcelable.Creator<ArtistParcelable> CREATOR = new Parcelable.Creator<ArtistParcelable>() {
            public ArtistParcelable createFromParcel(Parcel in) {
                return new ArtistParcelable(in);
            }
            public ArtistParcelable[] newArray(int size) {
                return new ArtistParcelable[size];
            }
        };
    }

    public class ArtistAdapter extends ArrayAdapter<Artist> {

        public ArtistAdapter(Context context, int resource, int textViewResourceId, List<Artist> artists) {
            super(context, resource, textViewResourceId, artists);
        }

        class ViewHolder {
            public TextView textView;
            public ImageView imageView;
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
                if (artists.isEmpty()) {
                    showToast("Unable to find " + mEditText.getText().toString());
                } else {
                    mArtistAdapter.addAll(artists);
                }
            } else {
                showToast("Search Error Occurred");
            }
        }

        private void showToast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }
}
