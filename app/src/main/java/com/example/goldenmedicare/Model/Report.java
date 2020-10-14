package com.example.goldenmedicare.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable {
    private int reportId;
    private String title, summary, remarks, reportDate;

    public Report(int reportId, String title, String summary, String remarks, String reportDate) {
        this.reportId = reportId;
        this.title = title;
        this.summary = summary;
        this.remarks = remarks;
        this.reportDate = reportDate;
    }

    protected Report(Parcel in) {
        reportId = in.readInt();
        title = in.readString();
        summary = in.readString();
        remarks = in.readString();
        reportDate = in.readString();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public int getReportId() {
        return reportId;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getReportDate() {
        return reportDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(reportId);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(remarks);
        dest.writeString(reportDate);
    }
}
