package com.github.opensource21.vsynchistory.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface GitService {

	void printStatus() throws  GitAPIException;

	/**
	 * Returns a set of changed filenames, staged or unstaged relative to the repository.
	 * @return a set of changed files, staged or unstaged.
	 */
	Set<String> getChangedFilenames() throws  GitAPIException;

	ContentTupel getContents(String filename) throws  GitAPIException, IOException;

	void commit(Set<String> filenames, String commitMessage) throws  GitAPIException;

	class ContentTupel {
		private final InputStream oldContent;
		private final InputStream newContent;


		public ContentTupel(InputStream oldContent, InputStream newContent) {
			super();
			this.oldContent = oldContent;
			this.newContent = newContent;
		}


		public InputStream getOldContent() {
			return oldContent;
		}


		public InputStream getNewContent() {
			return newContent;
		}


	}

}