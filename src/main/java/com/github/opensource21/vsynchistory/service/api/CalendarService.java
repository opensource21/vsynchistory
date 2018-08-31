package com.github.opensource21.vsynchistory.service.api;

import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.model.HolidayEvent;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface CalendarService {

    /**
     * Compare to calendars.
     *
     * @param oldCalendar     the old state.
     * @param newCalendar     the new state
     * @param changedFilename the name of the file to get this information for the result.
     * @return the result of the comparision.
     * @throws IOException     if there was a problem to read the file.
     * @throws ParserException if there was a problem to parse the file.
     */
    DiffResult compare(InputStream oldCalendar, InputStream newCalendar, String changedFilename)
            throws IOException, ParserException;

    /**
     * Move old calendar-entries into an archive calendar.
     *
     * @param user the user which calendar should be archived.
     * @return information about move or null if nothing happends
     * @throws IOException         if there was a problem to read or write the file.
     * @throws ParserException     if there was a problem to parse the file.
     * @throws ValidationException the new calendar isn't valid.
     */
    String archive(String user) throws IOException, ParserException, ValidationException;

    /**
     * Add holyday to the calendar.
     *
     * @param calendarFile the name of the calendar file, where the holiday should be imported.
     * @param holidays     the set of holidays.
     * @return information about move or null if nothing happens
     * @throws IOException         if there was a problem to read or write the file.
     * @throws ParserException     if there was a problem to parse the file.
     * @throws ValidationException the new calendar isn't valid.
     */
    String addHolydays(String calendarFile, Set<HolidayEvent> holidays) throws IOException, ParserException, ValidationException;

}
