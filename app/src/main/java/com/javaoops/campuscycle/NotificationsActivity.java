package com.javaoops.campuscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.model.Notification;
import com.javaoops.campuscycle.service.NotificationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private ListView              lvNotifications;
    private TextView              tvEmpty;
    private Button                btnMarkAllRead;

    private NotificationService   notificationService;
    private ArrayList<Notification> notificationList;
    private ArrayAdapter<String>  listAdapter;
    private ArrayList<String>     displayList;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        notificationService = new NotificationService(this, userId);

        lvNotifications = findViewById(R.id.lvNotifications);
        tvEmpty         = findViewById(R.id.tvEmpty);
        btnMarkAllRead  = findViewById(R.id.btnMarkAllRead);

        notificationList = new ArrayList<>();
        displayList      = new ArrayList<>();
        listAdapter      = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvNotifications.setAdapter(listAdapter);

        lvNotifications.setOnItemClickListener((parent, view, position, id) -> {
            Notification n = notificationList.get(position);
            if (!n.isRead()) {
                notificationService.markAsRead(n.getNotificationId());
                Toast.makeText(this, "Marked as read.", Toast.LENGTH_SHORT).show();
                loadNotifications();
            }
        });

        btnMarkAllRead.setOnClickListener(v -> {
            notificationService.markAllAsRead();
            Toast.makeText(this, "All notifications marked as read.", Toast.LENGTH_SHORT).show();
            loadNotifications();
        });

        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        notificationList = notificationService.getMyNotifications();
        displayList.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

        for (Notification n : notificationList) {
            String readStatus = n.isRead() ? "✓" : "🔵";
            String time       = sdf.format(new Date(n.getTimestamp()));
            String item       = readStatus + "  " + n.getMessage() + "\n" + time;
            displayList.add(item);
        }

        listAdapter.notifyDataSetChanged();

        if (notificationList.isEmpty()) {
            lvNotifications.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvNotifications.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}