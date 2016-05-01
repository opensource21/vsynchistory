package com.github.opensource21.vsynchistory.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;

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

    @Resource
    private CalendarService calendarService;

    @Resource
    private DiffService diffService;

    @Override
    public void run(String... args) throws Exception {
    	diffService.commitChanges("", gitService.getChangedFilenames());
        final String[] users = { "gunda", "niels" };
        for (final String user : users) {
            final String changes = calendarService.archive(user);
            if (StringUtils.isNotEmpty(changes)) {
            	diffService.commitChanges(changes + "\n", gitService.getChangedFilenames());
            }
        }
    }
}
