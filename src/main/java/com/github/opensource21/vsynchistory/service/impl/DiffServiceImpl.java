/**
 *
 */
package com.github.opensource21.vsynchistory.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.annotation.Resource;

import net.fortuna.ical4j.data.ParserException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.model.ContentTupel;
import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.service.api.AddressService;
import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Default-Implementierung of {@link DiffService}.
 *
 * @author niels
 *
 */
@Service
public class DiffServiceImpl implements DiffService {

    private static final Logger LOG = LoggerFactory
            .getLogger(DiffServiceImpl.class);

    private static final String PROPS_FILESUFFIX = ".props";
    private static final String CALENDAR = "BEGIN:VCALENDAR";
    private static final String ADDRESS = "BEGIN:VCARD";

    @Resource
    private GitService gitService;

    @Resource
    private CalendarService calendarService;

    @Resource
    private AddressService addressService;

    @Override
    public synchronized void commitChanges(String additionalCommitMessage,
            final Set<String> changedFilenames) throws GitAPIException,
            IOException, ParserException {
        for (final String changedFile : changedFilenames) {
            final ContentTupel changes = gitService.getContents(changedFile);
            final InputStream oldContent = changes.getOldContent();
            final byte[] typeAsBytes = new byte[CALENDAR.length()];
            oldContent.read(typeAsBytes);
            final String type = new String(typeAsBytes);
            oldContent.reset();
            final DiffResult diffResult;
            if (CALENDAR.equals(type)) {
                gitService.add(changedFile, changedFile + PROPS_FILESUFFIX);
                diffResult = calendarService.compare(oldContent, changes.getNewContent(), changedFile);
            } else if (type.startsWith(ADDRESS)) {
                gitService.add(changedFile, changedFile + PROPS_FILESUFFIX);
                diffResult = addressService.compare(oldContent, changes.getNewContent());
            } else {
                diffResult = null;
            }

            if (diffResult == null) {
                if (!changedFile.endsWith(PROPS_FILESUFFIX)) {
                    LOG.error("Unhandled file {}.", changedFile);
                }
            } else {
                final StringBuilder commitMessage =
                        new StringBuilder(additionalCommitMessage);
                commitMessage.append(changedFile).append(" :");
                commitMessage.append(" -").append(
                        diffResult.getNrOfDeletedEntries());
                commitMessage.append(" ~").append(
                        diffResult.getNrOfChangedEntries());
                commitMessage.append(" +").append(
                        diffResult.getNrOfNewEntries());
                if (diffResult.getTotalChanges() > 10) {
                    LOG.error("Many changes: {}", commitMessage);
                }
                commitMessage.append("\n");
                commitMessage.append(diffResult.getCommitMessage());
                gitService.commit(commitMessage.toString(), changedFile,
                        changedFile + PROPS_FILESUFFIX);
            }
        }
    }
}
