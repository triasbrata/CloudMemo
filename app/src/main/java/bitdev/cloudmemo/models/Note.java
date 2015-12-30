package bitdev.cloudmemo.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import java.io.Serializable;


/**
 * Created by triasbrata on 12/24/15.
 */
public class Note implements Parcelable {
    private String title;
    private String message;
    private JSONArray tags;
    private String date;
    public int id;

    public Note(String title, String message, JSONArray tags, int id, String date) {
        this.title = title;
        this.message = message;
        this.tags = tags;
        this.id = id;
        this.date = date;
    }

    protected Note(Parcel in) {
        title = in.readString();
        message = in.readString();
        date = in.readString();
        id = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public JSONArray getTags() {
        return tags;
    }

    public String getDate(){ return date;}

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(date);
        dest.writeInt(id);
    }
}
