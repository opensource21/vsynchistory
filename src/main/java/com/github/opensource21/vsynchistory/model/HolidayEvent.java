/**
 * 
 */
package com.github.opensource21.vsynchistory.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model which holds the data for a holyday.
 * 
 * @author niels
 *
 */
public class HolidayEvent {

    public static final String UidStartString = "com.github.opensource21.vsynchistory.model.HolidayEvent-";
    
    /**
     * The day on which the holiday happens.
     */
    private final Date date;

    /**
     * A description of the Event
     */
    private final String description;

    public HolidayEvent(Date date, String description) {
        super();
        this.date = date;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
    
    /**
     * A unique-id for the event-.
     */
    public String getUid() {
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return UidStartString + formatter.format(date);
    }

}
