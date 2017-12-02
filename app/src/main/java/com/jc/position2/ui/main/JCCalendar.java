package com.jc.position2.ui.main;

import com.jc.position2.logic.alarm.AlarmTimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Data;

/**
 * Created by tconan on 16/4/27.
 */
@Data
public class JCCalendar {
    private GregorianCalendar gregorianCalendar;
    private int style; // 默认是0 -> AlarmTimeUtils.STATE_NONE = 0
    private String dateStr;
    private String addressStr;
    public final static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    public static List<JCCalendar> convert(List<GregorianCalendar> list) {
        List<JCCalendar> listAnswer = new ArrayList<>();

        if (list!=null && list.size()>0) {
            for (GregorianCalendar gregorianCalendar : list) {

                // Init
                JCCalendar jcCalendar = new JCCalendar();

                // Calendar
                jcCalendar.setGregorianCalendar(gregorianCalendar);

                // Time
                String dateStr = formatter.format(jcCalendar.getGregorianCalendar().getTime());
                jcCalendar.setDateStr(dateStr);

                // Add
                listAnswer.add(jcCalendar);
            }
        }

        return listAnswer;

    }
}
