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