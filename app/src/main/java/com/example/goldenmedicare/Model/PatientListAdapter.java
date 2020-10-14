package com.example.goldenmedicare.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.goldenmedicare.R;
import com.example.goldenmedicare.View.ReportCreateActivity;

import java.util.ArrayList;

public class PatientListAdapter extends ArrayAdapter<Patient> {

    private ArrayList<Patient> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtPatientName;
        TextView txtPatientEmail;
    }

    public PatientListAdapter(ArrayList<Patient> data, Context context) {
        super(context, R.layout.list_row, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent, false);
            viewHolder.txtPatientName = convertView.findViewById(R.id.list_title);
            viewHolder.txtPatientEmail = convertView.findViewById(R.id.list_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        final Patient patient = dataSet.get(position);
        viewHolder.txtPatientName.setText(patient.getPatientName());
        viewHolder.txtPatientEmail.setText(patient.getPatientEmail());
        convertView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.listbg));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getApplicationContext(), ReportCreateActivity.class);
                intent.putExtra("patient", patient);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if(mContext instanceof Activity){
                    ((Activity)mContext.getApplicationContext()).finish();
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public ArrayList<Patient> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<Patient> dataSet) {
        this.dataSet = dataSet;
    }
}
