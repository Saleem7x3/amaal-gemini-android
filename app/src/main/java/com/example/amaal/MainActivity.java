package com.example.amaal;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amaal.adapter.DeedAdapter;
import com.example.amaal.db.AppDatabase;
import com.example.amaal.model.Deed;
import com.example.amaal.logic.PrayerScheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeedAdapter.OnDeedClickListener {

    private AppDatabase db;
    private DeedAdapter adapter;
    private PrayerScheduler prayerScheduler;

    private TextView tvFocusTitle, tvFocusCategory;
    private Button btnCompleteFocus;
    private View cardFocus;
    private com.example.amaal.ui.ConsistencyView consistencyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);
        
        prayerScheduler = new PrayerScheduler(db);
        prayerScheduler.updatePrayerTimesForToday();
        
        initViews();
        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void initViews() {
        tvFocusTitle = findViewById(R.id.tvFocusTitle);
        tvFocusCategory = findViewById(R.id.tvFocusCategory);
        btnCompleteFocus = findViewById(R.id.btnCompleteFocus);
        cardFocus = findViewById(R.id.cardFocus);
        consistencyView = findViewById(R.id.consistencyGraph);
        
        consistencyView.setScores(Arrays.asList(0.5f, 0.8f, 1.0f, 0.6f, 0.9f, 1.0f, 0.4f));

        RecyclerView rvDeeds = findViewById(R.id.rvDeeds);
        rvDeeds.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeedAdapter(this);
        rvDeeds.setAdapter(adapter);
    }

    private void refreshUI() {
        List<Deed> deeds = db.deedDao().getIncompleteDeeds();

        if (deeds.isEmpty()) {
            cardFocus.setVisibility(View.GONE);
            adapter.setDeeds(Collections.emptyList());
            return;
        }

        Collections.sort(deeds);

        Deed focusDeed = deeds.get(0);
        updateFocusCard(focusDeed);

        if (deeds.size() > 1) {
            adapter.setDeeds(deeds.subList(1, deeds.size()));
        } else {
            adapter.setDeeds(Collections.emptyList());
        }
    }

    private void updateFocusCard(Deed deed) {
        cardFocus.setVisibility(View.VISIBLE);
        tvFocusTitle.setText(deed.title);
        tvFocusCategory.setText(deed.category.getDisplayName());
        btnCompleteFocus.setOnClickListener(v -> markDeedComplete(deed));
    }

    private void markDeedComplete(Deed deed) {
        deed.isCompleted = true;
        db.deedDao().update(deed);
        refreshUI();
    }

    @Override
    public void onDeedComplete(Deed deed) {
        markDeedComplete(deed);
    }

    @Override
    public void onDeedLongClick(Deed deed) {
        showJamaahConfigDialog(deed);
    }
    
    private void showJamaahConfigDialog(Deed deed) {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Configure Jamāʿah");
         String[] options = {"Set Time", "Clear"};
         builder.setItems(options, (dialog, which) -> {
             if (which == 0) showTimePicker(deed);
             else {
                 deed.jamaahTimeMillis = 0;
                 db.deedDao().update(deed);
                 refreshUI();
             }
         });
         builder.show();
    }
    
    private void showTimePicker(Deed deed) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, h, m) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, h);
            cal.set(Calendar.MINUTE, m);
            deed.jamaahTimeMillis = cal.getTimeInMillis();
            db.deedDao().update(deed);
            refreshUI();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }
}