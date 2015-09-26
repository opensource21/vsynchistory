/**
 *
 */
package com.github.opensource21.vsynchistory.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.service.api.CalendarService;

/**
 * @author niels
 *
 */
@Service
public class CalendarServiceImpl implements CalendarService {

	@Override
	public DiffResult compare(InputStream oldCalendar, InputStream newCalendar)
			throws IOException, ParserException {
		final Map<String, VEvent> oldEntries = parseCalendar(oldCalendar);
		final Map<String, VEvent> newEntries = parseCalendar(newCalendar);
		final Set<String> deletedIds = getValuesOnlyInFirst(oldEntries,
				newEntries);
		final Set<String> newIds = getValuesOnlyInFirst(newEntries, oldEntries);

		final Set<String> possibleChangedIds = new HashSet<>();
		possibleChangedIds.addAll(oldEntries.keySet());
		possibleChangedIds.removeAll(deletedIds);

		final StringBuilder message = new StringBuilder();
		final DateFormat keyDateFormat = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss");
		for (final String vEventId : deletedIds) {
			message.append("DELETED: ")
					.append(createDescription(oldEntries.get(vEventId),
							keyDateFormat)).append("\n");
		}
		int nrOfChangedEvents = 0;
		for (final String vEventId : possibleChangedIds) {
			final VEvent oldEvent = oldEntries.get(vEventId);
			final VEvent newEvent = newEntries.get(vEventId);
			if (!oldEvent.equals(newEvent)) {
				nrOfChangedEvents++;
				message.append("CHANGED - FROM: ")
						.append(createDescription(oldEvent, keyDateFormat))
						.append("\n");
				message.append("CHANGED -  TO : ")
						.append(createDescription(newEvent, keyDateFormat))
						.append("\n");
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

	private Map<String, VEvent> parseCalendar(InputStream calendarInput)
			throws IOException, ParserException {
		final CalendarBuilder builder = new CalendarBuilder();
		final Calendar calendar = builder.build(calendarInput);

		final ComponentList allEntries = calendar
				.getComponents(Component.VEVENT);
		final Map<String, VEvent> allEvents = new HashMap<>();
		for (final Object eventObj : allEntries) {
			final VEvent event = (VEvent) eventObj;
			allEvents.put(event.getUid().getValue(), event);

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

}
