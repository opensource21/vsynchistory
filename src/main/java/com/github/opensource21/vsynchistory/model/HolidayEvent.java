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
public class HolidayEvent implements Comparable<HolidayEvent> {

    public static final String UidStartString = "com.github.opensource21.vsynchistory.model.HolidayEvent-";
    
    /**
     * The day on which the holiday happens in UTC-Time.
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final HolidayEvent other = (HolidayEvent) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }

    @Override
    public int compareTo(HolidayEvent o) {
        final int dateCompare = this.date.compareTo(o.date);
        if (dateCompare == 0) {
            return this.description.compareTo(o.description);
        }
        return dateCompare;
    }

    @Override
    public String toString() {
        return "HolidayEvent [date=" + date + ", description=" + description
                + "]";
    }
    
}
