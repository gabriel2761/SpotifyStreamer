package com.example.gabrielmojica.spotify3.Parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by gabrielmojica on 29/06/2015.
 */
public class ParcelableTrack implements Parcelable {
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

    public static final Creator<ParcelableTrack> CREATOR = new Creator<ParcelableTrack>() {
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };
}
