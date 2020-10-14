package com.example.goldenmedicare.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {
    private int appointmentId;
    private String status, category, description, scheduleDate, user, userToken;

    public Appointment(int appointmentId, String status, String category, String description, String scheduleDate, String user, String userToken) {
        this.appointmentId = appointmentId;
        this.status = status;
        this.category = category;
        this.description = description;
        this.scheduleDate = scheduleDate;
        this.user = user;
        this.userToken = userToken;
    }

    protected Appointment(Parcel in) {
        appointmentId = in.readInt();
        status = in.readString();
        category = in.readString();
        description = in.readString();
        scheduleDate = in.readString();
        user = in.readString();
        userToken = in.readString();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public int getAppointmentId() { return appointmentId; }

    public String getStatus() { return status; }

    public String getCategory() { return category; }

    public String getDescription() { return description; }

    public String getScheduleDate() { return scheduleDate; }

    public String getUser() { return user; }

    public String getUserToken() { return userToken; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appointmentId);
        dest.writeString(status);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(scheduleDate);
        dest.writeString(user);
        dest.writeString(userToken);
    }
}
