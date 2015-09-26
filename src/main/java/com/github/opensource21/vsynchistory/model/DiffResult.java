/**
 *
 */
package com.github.opensource21.vsynchistory.model;

/**
 * Describe the differences.
 * @author niels
 *
 */
public class DiffResult {

	private final int nrOfDeletedEntries;

	private final int nrOfNewEntries;

	private final int nrOfChangedEntries;

	private final String commitMessage;

	public DiffResult(int nrOfDeletedEntries, int nrOfNewEntries,
			int nrOfChangedEntries, String commitMessage) {
		super();
		this.nrOfDeletedEntries = nrOfDeletedEntries;
		this.nrOfNewEntries = nrOfNewEntries;
		this.nrOfChangedEntries = nrOfChangedEntries;
		this.commitMessage = commitMessage;
	}

	public int getNrOfDeletedEntries() {
		return nrOfDeletedEntries;
	}

	public int getNrOfNewEntries() {
		return nrOfNewEntries;
	}

	public int getNrOfChangedEntries() {
		return nrOfChangedEntries;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public int getTotalChanges() {
		return nrOfDeletedEntries + nrOfChangedEntries + nrOfNewEntries;
	}



}
