package com.example.asus.schoolhouse.EventActivity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.schoolhouse.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        getSupportActionBar().setTitle("Calendar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MaterialCalendarView materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        materialCalendarView.setDateSelected(calendar.getTime(), true);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(1900, 1, 1))
                .setMaximumDate(CalendarDay.from(2100, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
                Toast.makeText(CalendarView.this, "" + FORMATTER.format(date.getDate()),Toast.LENGTH_SHORT).show();
            }
        });


        /*
        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Calendar cal1 = day.getCalendar();
                Calendar cal2 = Calendar.getInstance();

                return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                        && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                        && cal1.get(Calendar.DAY_OF_YEAR) ==
                        cal2.get(Calendar.DAY_OF_YEAR));
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(ContextCompat.getDrawable(CalendarView.this,R.drawable.calendar_selector));
            }
        });
        */
    }


}
