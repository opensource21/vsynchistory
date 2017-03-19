/**
 * 
 */
package com.github.opensource21.vsynchistory.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.github.opensource21.vsynchistory.model.HolidayEvent;
import com.github.opensource21.vsynchistory.service.api.HolidayService;

/**
 * Test of the {@linkplain HolidayServiceImpl}.
 * @author niels
 *
 */
public class HolidayServiceImplTest {

    private final HolidayService testee = new HolidayServiceImpl();
    
    /**
     * Test method for {@link com.github.opensource21.vsynchistory.service.impl.HolidayServiceImpl#getHolydays(int, int, java.lang.String)}.
     * @throws Exception 
     */
    @Test
    public void testGetHolydays() throws Exception {
        //Arrange
        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        final HolidayEvent[] expected = new HolidayEvent[25-6];
        int i = 0;
        expected[i++] =new HolidayEvent(formatter.parse("01.01.2017 00:00:00"), "Neujahr");
        expected[i++] =new HolidayEvent(formatter.parse("14.04.2017 00:00:00"), "Karfreitag");
//        expected[i++] =new HolidayEvent(formatter.parse("16.04.2017 00:00:00"), "Ostern");
        expected[i++] =new HolidayEvent(formatter.parse("17.04.2017 00:00:00"), "Ostermontag");
        expected[i++] =new HolidayEvent(formatter.parse("01.05.2017 00:00:00"), "Tag der Arbeit");
        expected[i++] =new HolidayEvent(formatter.parse("25.05.2017 00:00:00"), "Christi Himmelfahrt");
//        expected[i++] =new HolidayEvent(formatter.parse("04.06.2017 00:00:00"), "Pfingstsonntag");
        expected[i++] =new HolidayEvent(formatter.parse("05.06.2017 00:00:00"), "Pfingstmontag");
        expected[i++] =new HolidayEvent(formatter.parse("03.10.2017 00:00:00"), "Tag der Deutschen Einheit");
        expected[i++] =new HolidayEvent(formatter.parse("31.10.2017 00:00:00"), "Reformationstag");
//        expected[i++] =new HolidayEvent(formatter.parse("24.12.2017 00:00:00"), "Heiligabend");
        expected[i++] =new HolidayEvent(formatter.parse("25.12.2017 00:00:00"), "1. Weihnachtsfeiertag");
        expected[i++] =new HolidayEvent(formatter.parse("26.12.2017 00:00:00"), "2. Weihnachtsfeiertag");
        expected[i++] =new HolidayEvent(formatter.parse("01.01.2018 00:00:00"), "Neujahr");
        expected[i++] =new HolidayEvent(formatter.parse("30.03.2018 00:00:00"), "Karfreitag");
//        expected[i++] =new HolidayEvent(formatter.parse("01.04.2018 00:00:00"), "Ostern");
        expected[i++] =new HolidayEvent(formatter.parse("02.04.2018 00:00:00"), "Ostermontag");
        expected[i++] =new HolidayEvent(formatter.parse("01.05.2018 00:00:00"), "Tag der Arbeit");
        expected[i++] =new HolidayEvent(formatter.parse("10.05.2018 00:00:00"), "Christi Himmelfahrt");
//        expected[i++] =new HolidayEvent(formatter.parse("20.05.2018 00:00:00"), "Pfingstsonntag");
        expected[i++] =new HolidayEvent(formatter.parse("21.05.2018 00:00:00"), "Pfingstmontag");
        expected[i++] =new HolidayEvent(formatter.parse("03.10.2018 00:00:00"), "Tag der Deutschen Einheit");
//        expected[i++] =new HolidayEvent(formatter.parse("24.12.2018 00:00:00"), "Heiligabend");
        expected[i++] =new HolidayEvent(formatter.parse("25.12.2018 00:00:00"), "1. Weihnachtsfeiertag");
        expected[i++] =new HolidayEvent(formatter.parse("26.12.2018 00:00:00"), "2. Weihnachtsfeiertag");
        
        final List<HolidayEvent> hd = new ArrayList<>(testee.getHolydays(2017, 2018, "ni"));
//        Collections.sort(hd);
//        
//        for (final HolidayEvent holidayEvent : hd) {
//            System.out.println("expected[i++] = new HolidayEvent(formatter.parse(\""+formatter.format(holidayEvent.getDate())+"\"), \""+ holidayEvent.getDescription()+"\");");
//        }
        assertThat(hd).containsExactlyInAnyOrder(expected);
    }

}
