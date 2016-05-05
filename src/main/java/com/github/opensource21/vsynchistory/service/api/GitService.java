package com.github.opensource21.vsynchistory.service.api;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.github.opensource21.vsynchistory.model.LogMessage;
import com.github.opensource21.vsynchistory.model.ContentTupel;

public interface GitService {

    /**
     * Returns a set of changed filenames, staged or unstaged relative to the
     * repository.
     * 
     * @return a set of changed files, staged or unstaged.
     *
     */
    Set<String> getChangedFilenames() throws GitAPIException;

    /**
     * Returns the old and new content.
     * 
     * @param filename name of the file.
     * @return the old and new content.
     */
    ContentTupel getContents(String filename) throws GitAPIException, IOException;

    /**
     * Commit the given files if they are in the index.
     * 
     * @param commitMessage the commit message.
     * @param filenames the name of the files which should be added.
     * @throws GitAPIException
     */
    void commit(String commitMessage, String... filenames) throws GitAPIException;

    /**
     * Add the given files.
     * 
     * @param filenames the name of the files which should be added.
     * @throws GitAPIException
     */
    void add(String... filenames) throws GitAPIException;

    /**
     * List all log messages.
     * 
     * @return all log messages.
     * @throws GitAPIException
     */
    List<LogMessage> getLogMessages() throws GitAPIException;

}