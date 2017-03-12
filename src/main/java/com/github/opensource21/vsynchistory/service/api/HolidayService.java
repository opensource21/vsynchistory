package com.github.opensource21.vsynchistory.service.api;

import java.util.Set;

import com.github.opensource21.vsynchistory.model.HolidayEvent;

public interface HolidayService {

    /**
     * Find all Holyday for the given year
     * @param startYear the starting year including 1.1.
     * @param endYear the ending year including 31.12.
     * @param bundesland the bundesland of Germany
     * @return the set of holyday-events.
     */
    Set<HolidayEvent> getHolydays(int startYear, int endYear, String bundesland);

}