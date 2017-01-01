/**
 *
 */
package com.github.opensource21.vsynchistory.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.service.api.CalendarService;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;

/**
 * @author niels
 *
 */
@Service
public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarServiceImpl.class);
    
    @Value(value = "${repositoryLocation}")
    private String repositoryLocation;

    @Override
    public String archive(String user) throws IOException, ParserException,
            ValidationException {
        // Harcoded is easier
        final File repoDir = new File(repositoryLocation);
        Date lastModifiedLimit =
                DateUtils.truncate(new Date(), java.util.Calendar.YEAR);
        lastModifiedLimit = DateUtils.addMonths(lastModifiedLimit, -6);
        int moved = 0;
        final File currentFile = new File(repoDir, "volatil/" + user);
        final File archiveFile =
                new File(repoDir, "stabil/" + user + "_archiv");
        final CalendarBuilder builder = new CalendarBuilder();
        final Calendar currentCalendar =
                builder.build(new FileInputStream(currentFile));
        final ComponentList allCurrentEntries =
                currentCalendar.getComponents(Component.VEVENT);
        final Calendar archiveCalendar =
                builder.build(new FileInputStream(archiveFile));
        for (final Object eventObj : allCurrentEntries) {
            final VEvent event = (VEvent) eventObj;

            final Date modifiedDate = getDate(event.getLastModified());
            final Date endDate = getDate(event.getEndDate());
            final Date referenceDate;
            if (modifiedDate == null || modifiedDate.before(endDate)) {
                referenceDate = endDate;
            } else {
                referenceDate = modifiedDate;
            }
            if (referenceDate.before(lastModifiedLimit)) {
                currentCalendar.getComponents().remove(event);
                archiveCalendar.getComponents().add(event);
                moved++;
            }
        }
        final CalendarOutputter outputter = new CalendarOutputter(false);
        outputter.output(currentCalendar, new FileOutputStream(currentFile));
        outputter.output(archiveCalendar, new FileOutputStream(archiveFile));
        if (moved > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Move for ").append(user).append(' ').append(moved)
                    .append(" Entries.");
            return sb.toString();
        } else {
            return null;
        }
    }

    @Override
    public DiffResult compare(InputStream oldCalendar, InputStream newCalendar, String changedFilename)
            throws IOException, ParserException {
        final Map<String, VEvent> oldEntries = parseCalendar(oldCalendar, changedFilename);
        final Map<String, VEvent> newEntries = parseCalendar(newCalendar, changedFilename);
        final Set<String> deletedIds =
                getValuesOnlyInFirst(oldEntries, newEntries);
        final Set<String> newIds = getValuesOnlyInFirst(newEntries, oldEntries);

        final Set<String> possibleChangedIds = new HashSet<>();
        possibleChangedIds.addAll(oldEntries.keySet());
        possibleChangedIds.removeAll(deletedIds);

        final StringBuilder message = new StringBuilder();
        final DateFormat keyDateFormat =
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (final String vEventId : deletedIds) {
            message.append("DELETED: ")
                    .append(createDescription(oldEntries.get(vEventId),
                            keyDateFormat)).append("\n");
        }
        int nrOfChangedEvents = 0;
        for (final String vEventId : possibleChangedIds) {
            final VEvent oldEvent = oldEntries.get(vEventId);
            final VEvent newEvent = newEntries.get(vEventId);
            final String change = getChanges(oldEvent, newEvent);
            if (StringUtils.isNotEmpty(change)) {
                nrOfChangedEvents++;
                message.append("CHANGED - FROM: ")
                        .append(createDescription(oldEvent, keyDateFormat))
                        .append("\n");
                message.append("CHANGED -  TO : ")
                        .append(createDescription(newEvent, keyDateFormat))
                        .append("\n");
                message.append(change).append("\n");
            }

        }
        for (final String vEventId : newIds) {
            message.append("NEW: ")
                    .append(createDescription(newEntries.get(vEventId),
                            keyDateFormat)).append("\n");
        }
        return new DiffResult(deletedIds.size(), newIds.size(),
                nrOfChangedEvents, message.toString());

    }

    private Set<String> getValuesOnlyInFirst(
            final Map<String, VEvent> firstCalendar,
            final Map<String, VEvent> secondCalendar) {
        final Set<String> onlyFirstEntriesUid = new HashSet<>();
        onlyFirstEntriesUid.addAll(firstCalendar.keySet());
        onlyFirstEntriesUid.removeAll(secondCalendar.keySet());
        return onlyFirstEntriesUid;
    }

    private Map<String, VEvent> parseCalendar(InputStream calendarInput, String changedFilename)
            throws IOException, ParserException {
        final CalendarBuilder builder = new CalendarBuilder();
        final Calendar calendar = builder.build(calendarInput);

        final ComponentList allEntries =
                calendar.getComponents(Component.VEVENT);
        final Map<String, VEvent> allEvents = new HashMap<>();
        final DateFormat keyDateFormat =
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (final Object eventObj : allEntries) {
            final VEvent event = (VEvent) eventObj;
            String uidOfEvent = event.getUid().getValue();
            if (event.getRecurrenceId() != null && StringUtils.isNotEmpty(event.getRecurrenceId().getValue())) {
                uidOfEvent = uidOfEvent + event.getRecurrenceId().getValue();
            }
            
            if (allEvents.containsKey(uidOfEvent)) {
                LOG.error("Duplicate UID {} in {} for {}.", uidOfEvent, changedFilename, 
                        createDescription(event, keyDateFormat));
            } else {
                allEvents.put(uidOfEvent, event);
            }
        }
        return allEvents;
    }

    private String createDescription(VEvent event, DateFormat keyDateFormat) {
        final StringBuilder sb = new StringBuilder();
        if (event.getStartDate() != null) {
            sb.append(keyDateFormat.format(event.getStartDate().getDate()))
                    .append('@');
        }
        if (event.getSummary() != null && event.getSummary().getValue() != null) {
            sb.append(event.getSummary().getValue().trim()).append('@');
        } else {
            sb.append("NO-SUMMARY@");
        }
        if (event.getEndDate() != null) {
            sb.append(keyDateFormat.format(event.getEndDate().getDate()))
                    .append('@');
        }
        if (event.getDescription() != null
                && event.getDescription().getValue() != null) {
            sb.append(event.getDescription().getValue().trim());
        }
        return sb.toString();
    }

    private String getChanges(VEvent oldEvent, VEvent newEvent) {
        final StringBuilder sb = new StringBuilder();
        if (!oldEvent.getStartDate().getDate()
                .equals(newEvent.getStartDate().getDate())) {
            addChangeMessage(sb, oldEvent.getStartDate(),
                    newEvent.getStartDate());
        }
        if (!oldEvent.getEndDate().getDate()
                .equals(newEvent.getEndDate().getDate())) {
            addChangeMessage(sb, oldEvent.getEndDate(), newEvent.getEndDate());
        }
        if (!equals(oldEvent.getSummary(), newEvent.getSummary())) {
            addChangeMessage(sb, oldEvent.getSummary(), newEvent.getSummary());
        }
        if (!equals(oldEvent.getDescription(), newEvent.getDescription())) {
            addChangeMessage(sb, oldEvent.getDescription(),
                    newEvent.getDescription());
        }
        if (!equals(oldEvent.getProperty(Property.RRULE),
                newEvent.getProperty(Property.RRULE))) {
            addChangeMessage(sb, oldEvent.getProperty(Property.RRULE),
                    newEvent.getProperty(Property.RRULE));
        }
        if (!equals(oldEvent.getLocation(), newEvent.getLocation())) {
            addChangeMessage(sb, oldEvent.getLocation(), newEvent.getLocation());
        }
        return sb.toString();
    }

    private void addChangeMessage(StringBuilder sb, Content oldContent,
            Content newContent) {
        final String name;
        if (oldContent != null) {
            name = oldContent.getName();
        } else {
            name = newContent.getName();
        }
        sb.append(name).append(": ");
        addContent(sb, oldContent);
        sb.append(" -> ");
        addContent(sb, newContent);
        sb.append(" ");
    }

    private void addContent(StringBuilder sb, Content content) {
        if (content == null) {
            return;
        }
        if (content instanceof DateProperty) {
            sb.append(((DateProperty) content).getDate());
        } else {
            sb.append(content.getValue());
        }
    }

    private boolean equals(Content v1, Content v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v2 == null) {
            return false;
        }

        final boolean result = v1.equals(v2);
        return result;
    }

    private Date getDate(DateProperty content) {
        if (content == null) {
            return null;
        }
        return content.getDate();
    }

}
