ZIP: project-organizer-output.zip

📁 project-organizer-output/
 ├─ 📁 .github/
  │  └─ 📁 workflows/
  │     └─ 📄 android_build.yml
 ├─ 📁 app/
  │  ├─ 📁 src/
  │   │  └─ 📁 main/
  │   │     ├─ 📁 java/
  │   │      │  └─ 📁 com/
  │   │      │     └─ 📁 example/
  │   │      │        └─ 📁 amaal/
  │   │      │           ├─ 📁 adapter/
  │   │      │            │  └─ 📄 DeedAdapter.java
  │   │      │           ├─ 📁 db/
  │   │      │            │  ├─ 📄 AppDatabase.java
  │   │      │            │  └─ 📄 DeedDao.java
  │   │      │           ├─ 📁 logic/
  │   │      │            │  └─ 📄 PrayerScheduler.java
  │   │      │           ├─ 📁 model/
  │   │      │            │  ├─ 📄 Deed.java
  │   │      │            │  └─ 📄 FiqhCategory.java
  │   │      │           ├─ 📁 ui/
  │   │      │            │  └─ 📄 ConsistencyView.java
  │   │      │           ├─ 📁 widget/
  │   │      │            │  └─ 📄 FocusWidget.java
  │   │      │           └─ 📄 MainActivity.java
  │   │     ├─ 📁 res/
  │   │      │  ├─ 📁 layout/
  │   │      │   │  ├─ 📄 activity_main.xml
  │   │      │   │  └─ 📄 item_deed.xml
  │   │      │  ├─ 📁 values/
  │   │      │   │  ├─ 📄 strings.xml
  │   │      │   │  └─ 📄 themes.xml
  │   │      │  └─ 📁 xml/
  │   │      │     ├─ 📄 backup_rules.xml
  │   │      │     ├─ 📄 data_extraction_rules.xml
  │   │      │     └─ 📄 focus_widget_info.xml
  │   │     └─ 📄 AndroidManifest.xml
  │  └─ 📄 build.gradle
 ├─ 📁 gradle/
  │  └─ 📁 wrapper/
  │     └─ 📄 gradle-wrapper.properties
 ├─ 📄 build.gradle
 ├─ 📄 gradlew
 ├─ 📄 gradlew.bat
 ├─ 📄 README.md
 └─ 📄 settings.gradle


========== FILE: project-organizer-output/app/src/main/AndroidManifest.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Amaal"
        tools:targetApi="31">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <receiver android:name=".widget.FocusWidget"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.example.amaal.ACTION_MARK_COMPLETE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/focus_widget_info" />
        </receiver>

    </application>

</manifest>
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/model/FiqhCategory.java ==========
```java
package com.example.amaal.model;

public enum FiqhCategory {
    FARD_AYN(100, "Fard ʿAyn"),
    FARD_KIFAYAH(90, "Fard Kifāyah"),
    SUNNAH_MUAKKADAH(80, "Sunnah Muʾakkadah"),
    SUNNAH_GHAYR_MUAKKADAH(70, "Sunnah Ghayr Muʾakkadah"),
    MUSTAHAB(60, "Mustahab"),
    NAWAFIL(50, "Nawāfil"),
    MUBAH(10, "Mubāh"),
    MAKRUH(-10, "Makrūh");

    private final int weight;
    private final String displayName;

    FiqhCategory(int weight, String displayName) {
        this.weight = weight;
        this.displayName = displayName;
    }

    public int getWeight() {
        return weight;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/model/Deed.java ==========
```java
package com.example.amaal.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "deeds")
public class Deed implements Comparable<Deed> {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public FiqhCategory category;
    public boolean isCompleted;
    
    public boolean isPrayer;
    public long scheduledTimeMillis;
    
    public long jamaahTimeMillis;
    public int priorityWindowMinutes;
    
    public int estimatedDuration; 

    public Deed() {
        // Required for Room
    }

    @Ignore
    public Deed(String title, FiqhCategory category, boolean isPrayer) {
        this.title = title;
        this.category = category;
        this.isPrayer = isPrayer;
        this.isCompleted = false;
        this.priorityWindowMinutes = 15;
        this.estimatedDuration = 5;
    }

    public long getPriorityScore() {
        if (isCompleted) return -1;

        long score = category.getWeight() * 1000L;
        long now = System.currentTimeMillis();

        if (isPrayer) {
            if (now < scheduledTimeMillis) {
                return 0; 
            }
            
            if (jamaahTimeMillis > 0) {
                long windowStart = jamaahTimeMillis - (priorityWindowMinutes * 60 * 1000L);
                if (now >= windowStart && now <= jamaahTimeMillis) {
                    score += 500;
                }
            }
            
            if (now >= scheduledTimeMillis) {
                score += 200;
            }
        }

        score -= estimatedDuration;

        return score;
    }

    @Override
    public int compareTo(Deed other) {
        return Long.compare(other.getPriorityScore(), this.getPriorityScore());
    }
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/db/DeedDao.java ==========
```java
package com.example.amaal.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.amaal.model.Deed;
import java.util.List;

@Dao
public interface DeedDao {
    @Insert
    void insert(Deed deed);

    @Insert
    void insertAll(List<Deed> deeds);

    @Update
    void update(Deed deed);

    @Delete
    void delete(Deed deed);

    @Query("SELECT * FROM deeds WHERE isCompleted = 0")
    List<Deed> getIncompleteDeeds();

    @Query("SELECT * FROM deeds")
    List<Deed> getAllDeeds();
    
    @Query("DELETE FROM deeds")
    void deleteAll();
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/db/AppDatabase.java ==========
```java
package com.example.amaal.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import com.example.amaal.model.Deed;
import com.example.amaal.model.FiqhCategory;

@Database(entities = {Deed.class}, version = 1, exportSchema = false)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DeedDao deedDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "amaal_database")
                            .allowMainThreadQueries() 
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static class Converters {
        @TypeConverter
        public static FiqhCategory fromString(String value) {
            return value == null ? null : FiqhCategory.valueOf(value);
        }

        @TypeConverter
        public static String categoryToString(FiqhCategory category) {
            return category == null ? null : category.name();
        }
    }
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/logic/PrayerScheduler.java ==========
```java
package com.example.amaal.logic;

import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;
import com.example.amaal.db.AppDatabase;
import com.example.amaal.db.DeedDao;
import com.example.amaal.model.Deed;
import com.example.amaal.model.FiqhCategory;
import java.util.Date;
import java.util.List;

public class PrayerScheduler {

    private final DeedDao deedDao;

    public PrayerScheduler(AppDatabase db) {
        this.deedDao = db.deedDao();
    }

    public void updatePrayerTimesForToday() {
        Coordinates coordinates = new Coordinates(21.4225, 39.8262);
        
        com.batoulapps.adhan.CalculationParameters params = 
                CalculationMethod.UMM_AL_QURA.getParameters();
        params.madhab = Madhab.SHAFI; 

        DateComponents today = DateComponents.from(new Date());
        PrayerTimes times = new PrayerTimes(coordinates, today, params);

        updateOrInsertPrayer("Fajr", times.fajr.getTime());
        updateOrInsertPrayer("Dhuhr", times.dhuhr.getTime());
        updateOrInsertPrayer("Asr", times.asr.getTime());
        updateOrInsertPrayer("Maghrib", times.maghrib.getTime());
        updateOrInsertPrayer("Isha", times.isha.getTime());
    }

    private void updateOrInsertPrayer(String title, long timeMillis) {
        List<Deed> existing = deedDao.getAllDeeds(); 
        Deed target = null;
        for (Deed d : existing) {
            if (d.title.equalsIgnoreCase(title)) {
                target = d;
                break;
            }
        }

        if (target != null) {
            target.scheduledTimeMillis = timeMillis;
            if (timeMillis > System.currentTimeMillis()) {
                target.isCompleted = false;
            }
            deedDao.update(target);
        } else {
            Deed newPrayer = new Deed(title, FiqhCategory.FARD_AYN, true);
            newPrayer.scheduledTimeMillis = timeMillis;
            deedDao.insert(newPrayer);
        }
    }
}
```

========== FILE: project-organizer-output/app/src/main/res/layout/item_deed.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="16dp"
    android:layout_marginVertical="4dp"
    app:cardElevation="0dp"
    app:strokeWidth="1dp"
    app:strokeColor="#E0E0E0">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical">

        <View
            android:id="@+id/viewCategoryIndicator"
            android:layout_width="4dp"
            android:layout_height="40dp"
            android:background="@android:color/holo_blue_dark"
            android:layout_marginEnd="16dp"/>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/tvTitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Deed Title"
                android:textSize="16sp"
                android:textStyle="bold"
                android:textColor="@android:color/black"/>

            <TextView
                android:id="@+id/tvCategory"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Category"
                android:textSize="12sp"
                android:textColor="#757575"/>
        </LinearLayout>

        <CheckBox
            android:id="@+id/cbComplete"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

========== FILE: project-organizer-output/app/src/main/res/layout/activity_main.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F5F5F5">

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingBottom="80dp">
            
            <ImageButton
                android:id="@+id/btnDataSettings"
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:layout_gravity="end"
                android:layout_margin="16dp"
                android:src="@android:drawable/ic_menu_save"
                android:background="?attr/selectableItemBackgroundBorderless"
                android:contentDescription="Data Settings" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Current Focus"
                android:layout_marginStart="16dp"
                android:textSize="14sp"
                android:textAllCaps="true"
                android:textColor="#616161"
                android:letterSpacing="0.1"/>

            <com.google.android.material.card.MaterialCardView
                android:id="@+id/cardFocus"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="16dp"
                app:cardElevation="4dp"
                app:cardBackgroundColor="@android:color/white">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="24dp">

                    <TextView
                        android:id="@+id/tvFocusCategory"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="FARD AYN"
                        android:textSize="12sp"
                        android:textColor="@android:color/holo_blue_dark"
                        android:textStyle="bold"
                        android:textAllCaps="true"/>

                    <TextView
                        android:id="@+id/tvFocusTitle"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Loading..."
                        android:textSize="28sp"
                        android:textColor="@android:color/black"
                        android:layout_marginTop="8dp"/>

                    <Button
                        android:id="@+id/btnCompleteFocus"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Mark Complete"
                        android:layout_marginTop="24dp"
                        style="@style/Widget.MaterialComponents.Button.UnelevatedButton"/>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Consistency"
                android:layout_marginStart="16dp"
                android:layout_marginTop="8dp"
                android:textSize="14sp"
                android:textAllCaps="true"
                android:textColor="#616161"
                android:letterSpacing="0.1"/>

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="120dp"
                android:layout_marginHorizontal="16dp"
                android:layout_marginTop="8dp"
                android:layout_marginBottom="24dp"
                app:cardElevation="0dp"
                app:strokeColor="#E0E0E0"
                app:strokeWidth="1dp"
                app:cardBackgroundColor="@android:color/white">
                
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Last 7 Days (Fard ʿAyn)"
                        android:textSize="12sp"
                        android:textColor="#757575"/>

                    <com.example.amaal.ui.ConsistencyView
                        android:id="@+id/consistencyGraph"
                        android:layout_width="match_parent"
                        android:layout_height="0dp"
                        android:layout_weight="1"
                        android:layout_marginTop="8dp"/>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Remaining Deeds"
                android:layout_marginStart="16dp"
                android:textSize="14sp"
                android:textAllCaps="true"
                android:textColor="#616161"
                android:letterSpacing="0.1"/>

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rvDeeds"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:nestedScrollingEnabled="false"
                android:layout_marginTop="8dp"/>

        </LinearLayout>
    </androidx.core.widget.NestedScrollView>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/ui/ConsistencyView.java ==========
```java
package com.example.amaal.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConsistencyView extends View {

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Float> dailyScores = new ArrayList<>(); 

    public ConsistencyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        barPaint.setColor(0xFF1976D2); 
        barPaint.setStyle(Paint.Style.FILL);
        
        axisPaint.setColor(0xFFE0E0E0);
        axisPaint.setStrokeWidth(2f);
    }

    public void setScores(List<Float> scores) {
        this.dailyScores = scores;
        invalidate(); 
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dailyScores.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float barWidth = (width / dailyScores.size()) * 0.6f; 
        float spacing = (width / dailyScores.size()) * 0.4f;

        canvas.drawLine(0, height, width, height, axisPaint);

        float currentX = spacing / 2;

        for (Float score : dailyScores) {
            float barHeight = score * height;
            canvas.drawRect(currentX, height - barHeight, currentX + barWidth, height, barPaint);
            currentX += barWidth + spacing;
        }
    }
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/adapter/DeedAdapter.java ==========
```java
package com.example.amaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.amaal.R;
import com.example.amaal.model.Deed;
import java.util.ArrayList;
import java.util.List;

public class DeedAdapter extends RecyclerView.Adapter<DeedAdapter.DeedViewHolder> {

    private List<Deed> deeds = new ArrayList<>();
    private final OnDeedClickListener listener;

    public interface OnDeedClickListener {
        void onDeedComplete(Deed deed);
        void onDeedLongClick(Deed deed);
    }

    public DeedAdapter(OnDeedClickListener listener) {
        this.listener = listener;
    }

    public void setDeeds(List<Deed> deeds) {
        this.deeds = deeds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deed, parent, false);
        return new DeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeedViewHolder holder, int position) {
        Deed deed = deeds.get(position);
        holder.tvTitle.setText(deed.title);
        holder.tvCategory.setText(deed.category.getDisplayName());
        
        holder.cbComplete.setOnCheckedChangeListener(null);
        holder.cbComplete.setChecked(deed.isCompleted);
        
        holder.cbComplete.setOnClickListener(v -> listener.onDeedComplete(deed));
        
        holder.itemView.setOnLongClickListener(v -> {
            if (deed.isPrayer) {
                listener.onDeedLongClick(deed);
                return true; 
            }
            return false;
        });
        
        int colorRes = deed.category.getWeight() >= 90 ? android.R.color.holo_red_dark : android.R.color.holo_blue_dark;
        holder.categoryIndicator.setBackgroundResource(colorRes);
    }

    @Override
    public int getItemCount() {
        return deeds.size();
    }

    static class DeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory;
        CheckBox cbComplete;
        View categoryIndicator;

        public DeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            cbComplete = itemView.findViewById(R.id.cbComplete);
            categoryIndicator = itemView.findViewById(R.id.viewCategoryIndicator);
        }
    }
}
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/MainActivity.java ==========
```java
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
```

========== FILE: project-organizer-output/app/src/main/java/com/example/amaal/widget/FocusWidget.java ==========
```java
package com.example.amaal.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.example.amaal.R;
import com.example.amaal.db.AppDatabase;
import com.example.amaal.model.Deed;
import java.util.Collections;
import java.util.List;

public class FocusWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        AppDatabase db = AppDatabase.getDatabase(context);
        List<Deed> deeds = db.deedDao().getIncompleteDeeds();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main); // Simplified for placeholder
        
        if (!deeds.isEmpty()) {
            Collections.sort(deeds);
            Deed focus = deeds.get(0);
            // In a real widget, we'd use a specific widget layout
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
```

========== FILE: project-organizer-output/app/src/main/res/values/strings.xml ==========
```
<resources>
    <string name="app_name">Amaal</string>
</resources>
```

========== FILE: project-organizer-output/app/src/main/res/values/themes.xml ==========
```
<resources>
    <style name="Theme.Amaal" parent="Theme.MaterialComponents.Light.NoActionBar">
        <item name="colorPrimary">#1976D2</item>
        <item name="colorPrimaryVariant">#1565C0</item>
        <item name="colorOnPrimary">#FFFFFF</item>
        <item name="android:statusBarColor">#1565C0</item>
    </style>
</resources>
```

========== FILE: project-organizer-output/app/src/main/res/xml/data_extraction_rules.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <include domain="database" path="amaal_database"/>
    </cloud-backup>
    <device-transfer>
        <include domain="database" path="amaal_database"/>
    </device-transfer>
</data-extraction-rules>
```

========== FILE: project-organizer-output/app/src/main/res/xml/backup_rules.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <include domain="database" path="amaal_database"/>
</full-backup-content>
```

========== FILE: project-organizer-output/app/src/main/res/xml/focus_widget_info.xml ==========
```
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="250dp"
    android:minHeight="110dp"
    android:updatePeriodMillis="86400000"
    android:initialLayout="@layout/activity_main"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />
```

========== FILE: project-organizer-output/gradlew ==========
```
#!/bin/sh
# Minimal gradlew for ZIP convenience
export GRADLE_OPTS="-Xmx1024m -Dfile.encoding=UTF-8"
APP_HOME=$( cd "$( dirname "$0" )" && pwd )
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ ! -f "$CLASSPATH" ]; then
    echo "Error: gradle-wrapper.jar not found."
    echo "Please open this project in Android Studio to auto-generate it."
    exit 1
fi
java $GRADLE_OPTS -jar "$CLASSPATH" "$@"
```

========== FILE: project-organizer-output/gradlew.bat ==========
```
@if "%DEBUG%" == "" @echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_HOME=%DIRNAME%
set GRADLE_OPTS=-Xmx1024m -Dfile.encoding=UTF-8
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
if not exist "%CLASSPATH%" (
    echo Error: gradle-wrapper.jar not found.
    echo Please open this project in Android Studio to auto-generate it.
    goto end
)
java %GRADLE_OPTS% -jar "%CLASSPATH%" %*
:end
```

========== FILE: project-organizer-output/app/build.gradle ==========
```
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.example.amaal'
    compileSdk 33

    defaultConfig {
        applicationId "com.example.amaal"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.1'
    
    implementation "androidx.room:room-runtime:2.5.2"
    annotationProcessor "androidx.room:room-compiler:2.5.2"
    
    implementation 'com.batoulapps:adhan:1.2.1'
}
```

========== FILE: project-organizer-output/.github/workflows/android_build.yml ==========
```yaml
name: Android CI Build

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout Code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Grant Execute Permission for Gradlew
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug

    - name: Upload APK Artifact
      uses: actions/upload-artifact@v4
      with:
        name: Amaal-Debug-APK
        path: app/build/outputs/apk/debug/app-debug.apk
```

========== FILE: project-organizer-output/settings.gradle ==========
```
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Amaal"
include ':app'
```

========== FILE: project-organizer-output/build.gradle ==========
```
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.0'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

========== FILE: project-organizer-output/gradle/wrapper/gradle-wrapper.properties ==========
```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

========== FILE: project-organizer-output/README.md ==========
```markdown
# Amaal - Android CI/CD Pipeline

This project includes a GitHub Actions workflow to automatically build a debug APK on every push or pull request to the `main` or `master` branches.

## How to use

1. **Push to GitHub**: Ensure the `.github/workflows/android_build.yml` and Gradle configuration files are in your repository.
2. **Actions Tab**: Navigate to the "Actions" tab in your GitHub repository to see the build progress.
3. **Download APK**: Once the workflow completes, the `Amaal-Debug-APK` will be available as an artifact in the build summary.

## Requirements
- Java 17
- Gradle Wrapper (`gradlew`)
- Standard Android project structure
```

