package com.example.goldenmedicare.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    private int patientId;
    private String patientName, patientEmail, patientToken;

    public Patient (int patientId, String patientName, String patientEmail, String patientToken) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientToken = patientToken;
    }

    protected Patient(Parcel in) {
        patientId = in.readInt();
        patientName = in.readString();
        patientEmail = in.readString();
        patientToken = in.readString();
    }

    public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public int getPatientId() { return patientId; }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientToken() {
        return patientToken;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(patientId);
        dest.writeString(patientName);
        dest.writeString(patientEmail);
        dest.writeString(patientToken);
    }
}
