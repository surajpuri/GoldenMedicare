package com.example.goldenmedicare.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.goldenmedicare.Model.Appointment;
import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Model.Report;
import com.example.goldenmedicare.View.ReportViewActivity;
import com.example.goldenmedicare.View.ViewAppointmentActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    static int notificationId = 0;
    public MyFireBaseMessagingService() {
    }
    //recevied notificaiton message
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        try {
            int reportId = Integer.parseInt(data.get("report_id"));
            String reportTitle = data.get("report_title");
            String reportSummary = data.get("report_summary");
            String reportRemarks = data.get("report_remarks");
            String reportDate = data.get("report_date");

            Report report = new Report(reportId, reportTitle, reportSummary, reportRemarks, reportDate);

            sendNotification(reportTitle, "You have got your report on " + reportTitle
                    + " at " + reportDate, report);
        } catch (Exception e){
            try {
                int appointmentId = Integer.parseInt(data.get("appointment_id"));
                String category = data.get("category");
                String appointmentDesc = data.get("appointment_desc");
                String status = data.get("status");
                String user = data.get("user_name");
                String token = data.get("token");

                Appointment appointment = new Appointment(appointmentId, status, category, appointmentDesc, "", user, token);
                sendNotification("New Appointment Request", "You have an appointment request from " + user, appointment);
            } catch (Exception ex){
                try {
                    String title = data.get("title");
                    String message = data.get("message");

                    sendNotification(title, message);
                } catch (Exception e1){
                }
            }
        }
    }

    /**
     * send notification using firebase messaging service
     * @param messageTitle
     * @param messageBody
     */
    private void sendNotification(String messageTitle, String messageBody, Report report){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String channelId = "NexusIMNotificationChannel";
            // The user-visible name of the channel.
            CharSequence channelName = "NexusIM Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel= new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(channelId);
        }


        Intent intent = new Intent(this, ReportViewActivity.class);
        intent.putExtra("report", report);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationBuilder.setSmallIcon(R.drawable.clinic);
        notificationBuilder.setContentTitle(messageTitle);
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationId++;
    }

    /**
     * send notification using firebase messaging service
     * @param messageTitle
     * @param messageBody
     */
    private void sendNotification(String messageTitle, String messageBody, Appointment appointment){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String channelId = "NexusIMNotificationChannel";
            // The user-visible name of the channel.
            CharSequence channelName = "NexusIM Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel= new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(channelId);
        }


        Intent intent = new Intent(this, ViewAppointmentActivity.class);
        intent.putExtra("appointment", appointment);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationBuilder.setSmallIcon(R.drawable.clinic);
        notificationBuilder.setContentTitle(messageTitle);
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationId++;
    }

    /**
     * send notification using firebase messaging service
     * @param messageTitle
     * @param messageBody
     */
    private void sendNotification(String messageTitle, String messageBody){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String channelId = "NexusIMNotificationChannel";
            // The user-visible name of the channel.
            CharSequence channelName = "NexusIM Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel= new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(channelId);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationBuilder.setSmallIcon(R.drawable.clinic);
        notificationBuilder.setContentTitle(messageTitle);
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationId++;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN), token);
        editor.commit();
        Log.d("token:", token);
    }
}
