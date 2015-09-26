package com.github.opensource21.vsynchistory.controller;

import java.io.InputStream;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.GitService;
import com.github.opensource21.vsynchistory.service.api.GitService.ContentTupel;

/**
 * ApplicationController for the commandline.
 *
 * @author niels
 *
 */
@Component
public class CommandlineController implements CommandLineRunner {

	private static final String PROPS_FILESUFFIX = ".props";

	private static final Logger LOG = LoggerFactory
			.getLogger(CommandlineController.class);

	private static final String CALENDAR = "BEGIN:VCALENDAR";
	private static final String ADDRESS = "BEGIN:VCARD";

	@Resource
	private GitService gitService;

	@Resource
	private CalendarService calendarService;

	@Override
	public void run(String... args) throws Exception {
		final Set<String> changedFilenames = gitService.getChangedFilenames();
		for (final String changedFile : changedFilenames) {
			final ContentTupel changes = gitService.getContents(changedFile);
			final InputStream oldContent = changes.getOldContent();
			final byte[] typeAsBytes = new byte[CALENDAR.length()];
			oldContent.read(typeAsBytes);
			final String type = new String(typeAsBytes);
			oldContent.reset();
			String commitMessage;
			if (CALENDAR.equals(type)) {
				final DiffResult diffResult = calendarService.compare(
						oldContent, changes.getNewContent());

				commitMessage = changedFile + " : -"
						+ diffResult.getNrOfDeletedEntries() + " +"
						+ diffResult.getNrOfNewEntries() + " ~"
						+ diffResult.getNrOfChangedEntries() + "\n"
						+ diffResult.getCommitMessage();
			} else if (type.startsWith(ADDRESS)) {
				commitMessage = "CARD";
			} else {
				commitMessage = null;
			}

			if (commitMessage == null) {
				if (!changedFile.endsWith(PROPS_FILESUFFIX)) {
					LOG.error("Unhandled file {}.", changedFile);
				}
			} else {
				gitService.commit(commitMessage, changedFile, changedFile
						+ PROPS_FILESUFFIX);
			}
		}
	}
}
