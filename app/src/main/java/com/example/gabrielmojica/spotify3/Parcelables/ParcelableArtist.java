package com.example.gabrielmojica.spotify3.Parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by gabrielmojica on 29/06/2015.
 */
public class ParcelableArtist implements Parcelable {
    public String name;
    public String image;
    public String id;

    @Override
    public int describeContents() {
        return 0;
    }

    public ParcelableArtist(Artist artist) {
        name = artist.name;
        id = artist.id;
        if (!artist.images.isEmpty()) {
            image = artist.images.get(0).url;
        } else {
            image = "";
        }
    }

    public ParcelableArtist(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
    }

    public static final Creator<ParcelableArtist> CREATOR = new Creator<ParcelableArtist>() {
        public ParcelableArtist createFromParcel(Parcel in) {
            return new ParcelableArtist(in);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };
}
