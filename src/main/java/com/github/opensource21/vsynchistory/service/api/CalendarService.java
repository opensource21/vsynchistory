package com.github.opensource21.vsynchistory.service.api;

import java.io.IOException;
import java.io.InputStream;

import net.fortuna.ical4j.data.ParserException;

import com.github.opensource21.vsynchistory.model.DiffResult;

public interface CalendarService {

	DiffResult compare(InputStream oldCalendar, InputStream newCalendar) throws IOException, ParserException;

}
