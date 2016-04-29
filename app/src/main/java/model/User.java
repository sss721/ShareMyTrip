package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
/**
 * Created by Shweta on 4/7/2016.
 */
public class User implements Parcelable {
    private int id;
    private String userName;
    private String userPassword;
    private String userAddress;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String sex;
    private String aboutMe;



    public int getId() {  return id;    }

    public void setId(int id) {  this.id = id;    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserPassword() { return userPassword;    }

    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel p) {
            return new User(p);
        }

        public User[] newArray(int size) { return new User[size]; }
    };

    public User(Parcel p) {

        Log.d("Parcel object: ", p.toString());
        this.id = p.readInt();
        this.firstName = p.readString();
        this.userName = p.readString();
        this.userPassword = p.readString();
        this.lastName = p.readString();
        this.userAddress = p.readString();
        this.sex = p.readString();
        this.phoneNumber = p.readString();
        this.aboutMe = p.readString();
    }

    public User(int id,String firstName, String userName,String userPassword,String lastName, String userAddress,String sex, String phoneNumber,  String aboutMe ) {

    }

    public User() {
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }



}
