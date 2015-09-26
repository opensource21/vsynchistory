package com.github.opensource21.vsynchistory.service.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface GitService {


	/**
	 * Returns a set of changed filenames, staged or unstaged relative to the repository.
	 * @return a set of changed files, staged or unstaged.
	 *
	 */
	Set<String> getChangedFilenames() throws  GitAPIException;

	/**
	 * Returns the old and new content.
	 * @param filename name of the file.
	 * @return the old and new content.
	 */
	ContentTupel getContents(String filename) throws  GitAPIException, IOException;

	/**
	 * Commit the given files.
	 * @param commitMessage the commit message.
	 * @param filenames the name of the files which should be added.
	 * @throws GitAPIException
	 */
	void commit(String commitMessage, String... filenames) throws  GitAPIException;

	/**
	 * A tupel of contents.
	 * @author niels
	 *
	 */
	class ContentTupel {
		private final InputStream oldContent;
		private final InputStream newContent;

		/**
		 * Create a tupel of old and new content.
		 * @param oldContent the old content.
		 * @param newContent the new content.
		 */
		public ContentTupel(InputStream oldContent, InputStream newContent) {
			super();
			this.oldContent = oldContent;
			this.newContent = newContent;
		}

		/**
		 * Returns the old content.
		 * @return the old content.
		 */
		public InputStream getOldContent() {
			return oldContent;
		}


		/**
		 * Returns the new content.
		 * @return the new content.
		 */
		public InputStream getNewContent() {
			return newContent;
		}


	}

}