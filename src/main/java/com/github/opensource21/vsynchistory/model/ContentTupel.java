package com.github.opensource21.vsynchistory.model;

import java.io.InputStream;

/**
 * A tupel of contents.
 * @author niels
 *
 */
public class ContentTupel {
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