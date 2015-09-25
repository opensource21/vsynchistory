package com.github.opensource21.vsynchistory.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.service.GitService;

/**
 * ApplicationController for the commandline.
 *
 * @author niels
 *
 */
@Component
public class CommandlineController implements CommandLineRunner {

	@Resource
	private GitService gitService;

	@Override
	public void run(String... args) throws Exception {
		gitService.printStatus();
		final Set<String> changedFilenames = gitService.getChangedFilenames();
		for (final String changedFile : changedFilenames) {
			final BufferedReader oldContent = new BufferedReader(
					new InputStreamReader(gitService.getContents(changedFile)
							.getOldContent()));
			String line;
			while ((line = oldContent.readLine()) != null) {
				System.out.println(line);
			}

		}
	}
}
