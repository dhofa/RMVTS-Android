package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FirebaseMessageModel implements Parcelable {
    private String title;
    private String message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FirebaseMessageModel() {
    }

    protected FirebaseMessageModel(Parcel in) {
        title = in.readString();
        message = in.readString();
    }

    public static final Creator<FirebaseMessageModel> CREATOR = new Creator<FirebaseMessageModel>() {
        @Override
        public FirebaseMessageModel createFromParcel(Parcel in) {
            return new FirebaseMessageModel(in);
        }

        @Override
        public FirebaseMessageModel[] newArray(int size) {
            return new FirebaseMessageModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(message);
    }
}
