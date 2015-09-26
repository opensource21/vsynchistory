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
			final DiffResult diffResult;
			if (CALENDAR.equals(type)) {
				diffResult = calendarService.compare(oldContent, changes.getNewContent());
			} else if (type.startsWith(ADDRESS)) {
				diffResult = null;
			} else {
				diffResult = null;
			}

			if (diffResult == null) {
				if (!changedFile.endsWith(PROPS_FILESUFFIX)) {
					LOG.error("Unhandled file {}.", changedFile);
				}
			} else {
				final StringBuilder commitMessage = new StringBuilder();
				commitMessage.append(changedFile).append(" :");
				commitMessage.append(" -").append(diffResult.getNrOfDeletedEntries());
				commitMessage.append(" ~").append(diffResult.getNrOfChangedEntries());
				commitMessage.append(" +").append(diffResult.getNrOfNewEntries());
				if (diffResult.getTotalChanges() > 10) {
					LOG.error("Many changes: {}", commitMessage);
				}
				commitMessage.append("\n");
				commitMessage.append(diffResult.getCommitMessage());
				gitService.commit(commitMessage.toString(), changedFile, changedFile
						+ PROPS_FILESUFFIX);

			}
		}
	}
}
