/**
 *
 */
package com.github.opensource21.vsynchistory.service.api;

import java.io.IOException;
import java.util.Set;

import net.fortuna.ical4j.data.ParserException;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Service which compares the caldendar and contacts with the repository state.
 * 
 * @author niels
 *
 */
public interface DiffService {

    /**
     * Commit current changes.
     * 
     * @param additionalCommitMessage an additional message for the commit.
     * @param changedFilenames list of file which should be compared.
     * @throws GitAPIException
     * @throws IOException
     * @throws ParserException
     */
    void commitChanges(String additionalCommitMessage, Set<String> changedFilenames) 
            throws GitAPIException, IOException, ParserException;

}
