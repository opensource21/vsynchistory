package com.github.opensource21.vsynchistory.service;

import org.springframework.stereotype.Service;


/**
 * Everything to handle the git-repo.
 * @author niels
 *
 */
@Service
public class GitServiceImpl implements GitService {

	@Override
	public void printHelloWorld() {
		System.out.println("Hello World");
	}

}
