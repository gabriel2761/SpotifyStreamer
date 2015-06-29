package com.example.gabrielmojica.spotify3.Parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by gabrielmojica on 29/06/2015.
 */
public class ParcelableTrack implements Parcelable {
    public String name;
    public String album;
    public String image;

    public ParcelableTrack(Track track) {
        name = track.name;
        album = track.album.name;
        if (!track.album.images.isEmpty()) {
            image = track.album.images.get(0).url;
        } else {
            image = "";
        }

    }

    public ParcelableTrack(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableTrack> CREATOR = new Creator<ParcelableTrack>() {
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };
}
