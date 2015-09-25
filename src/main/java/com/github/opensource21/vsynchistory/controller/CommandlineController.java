
package com.github.opensource21.vsynchistory.controller;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.service.GitService;

/**
 * ApplicationController for the commandline.
 * @author niels
 *
 */
@Component
public class CommandlineController implements CommandLineRunner {

	@Resource
	private GitService gitService;

	@Override
	public void run(String... args) throws Exception {
		gitService.printHelloWorld();
	}


}
