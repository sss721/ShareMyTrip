package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

/**
 * Created by Shweta on 4/25/2016.
 */
public class BookTrip implements Parcelable {

    private int id;
    private String userId;
    private String source;
    private String destination;
    private String numberOfPassengers;
    private String date;
    private String time;
    private Date dateThis;



    public int getId() {  return id;    }

    public void setId(int id) {  this.id = id;    }

    public String getDestination() { return destination;    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(String numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getDateThis() {
        return dateThis;
    }

    public void setDateThis(Date date) {
        this.dateThis = dateThis;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }



    public static final Parcelable.Creator<BookTrip> CREATOR = new Parcelable.Creator<BookTrip>() {
        public BookTrip createFromParcel(Parcel p) {
            return new BookTrip(p);
        }

        public BookTrip[] newArray(int size) { return new BookTrip[size]; }
    };

    public BookTrip(Parcel p) {

        Log.d("Parcel object: ", p.toString());
        this.id = p.readInt();
        this.userId = p.readString();
        this.source = p.readString();
        this.destination = p.readString();
        this.numberOfPassengers= p.readString();
        this.date = p.readString();
        this.time = p.readString();
        this.dateThis = new Date(p.readLong());

    }

    public BookTrip(int id,String source, String destination,String numberOfPassengers,String date,String time, Date dateThis  ) {

    }

    public BookTrip() {
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }



}

