/**
 * 
 */
package com.github.opensource21.vsynchistory.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.model.HolidayEvent;
import com.github.opensource21.vsynchistory.service.api.HolidayService;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameter;
import de.jollyday.ManagerParameters;

/**
 * Service which provides access to all german holidays.
 * @author niels
 *
 */
@Service
public class HolidayServiceImpl implements HolidayService {
    
    /* (non-Javadoc)
     * @see com.github.opensource21.vsynchistory.service.impl.HolidayService#getHolydays(int, int, java.lang.String)
     */
    @Override
    public Set<HolidayEvent> getHolydays(int startYear, int endYear, String bundesland) {
        final ManagerParameter managerParameter = ManagerParameters.create(HolidayCalendar.GERMANY);
        final HolidayManager holidayManager = HolidayManager.getInstance(managerParameter);
        final Set<Holiday> hls = holidayManager.getHolidays(
                LocalDate.of(startYear, 1, 1), LocalDate.of(endYear, 12, 31), "ni");
        final Set<HolidayEvent> result = new HashSet<>();
        for (final Holiday holiday : hls) {
            final HolidayEvent holidayEvent = new HolidayEvent(
                    Date.from(holiday.getDate().atStartOfDay(ZoneId.of("UTC")).toInstant()),
                    holiday.getDescription(Locale.GERMANY));
            result.add(holidayEvent);
        }
        return result;
    }
}
