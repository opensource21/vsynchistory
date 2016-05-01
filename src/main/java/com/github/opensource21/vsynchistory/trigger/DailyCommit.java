/**
 *
 */
package com.github.opensource21.vsynchistory.trigger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Job which ensures, that at least daily a commit happens and the archive runs.
 * @author niels
 *
 */
public class DailyCommit {

	@Resource
    private GitService gitService;

    @Resource
    private CalendarService calendarService;

    @Resource
    private DiffService diffService;

    @Scheduled(cron = "${cron.dailyCommit}")
    public void dailyCommit() throws Exception {
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
