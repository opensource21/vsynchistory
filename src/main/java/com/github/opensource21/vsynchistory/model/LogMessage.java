/**
 *
 */
package com.github.opensource21.vsynchistory.model;

import java.util.Date;

/**
 * Die wesentlichen Informationen zu einem Commit.
 * @author niels
 *
 */
public class LogMessage implements Comparable<LogMessage> {

	private final Date commitDate;

	private final String commitMessage;

	public LogMessage(Date commitDate, String commitMessage) {
		super();
		this.commitDate = commitDate;
		this.commitMessage = commitMessage;
	}

	public Date getCommitDate() {
		return commitDate;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	@Override
	public int compareTo(LogMessage o) {
		return this.commitDate.compareTo(o.commitDate);
	}



}
