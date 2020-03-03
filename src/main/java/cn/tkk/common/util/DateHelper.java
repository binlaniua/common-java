package cn.tkk.common.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tkk on 2018/8/15.
 */
public class DateHelper {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Calendar calendar;

    public DateHelper(Date date) {
        if (date == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
    }

    public DateHelper() {
        this.calendar = Calendar.getInstance();
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDate() {
        return calendar.get(Calendar.DATE);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return calendar.get(Calendar.MINUTE);
    }

    public void addDays(int i) {
        calendar.add(Calendar.DATE, i);
    }

    public void addMin(int i) {
        calendar.add(Calendar.MINUTE, i);
    }

    public void setSecond(int i) {
        calendar.set(Calendar.SECOND, i);
    }

    public void setMin(int i) {
        calendar.set(Calendar.MINUTE, i);
    }

    public void setHour(int i) {
        calendar.set(Calendar.HOUR_OF_DAY, i);
    }

    public void setDate(int i) {
        calendar.set(Calendar.DAY_OF_MONTH, i);
    }

    public void addMonth(int i) {
        calendar.add(Calendar.MONTH, i);
    }

    public boolean isToday() {
        return DateUtils.isSameDay(this.calendar, Calendar.getInstance());
    }

    public Date getDateTime() {
        return calendar.getTime();
    }
}
