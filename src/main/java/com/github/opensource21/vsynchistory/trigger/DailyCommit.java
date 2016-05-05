/**
 *
 */
package com.github.opensource21.vsynchistory.trigger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Job which ensures, that at least daily a commit happens and the archive runs.
 *
 * @author niels
 *
 */
@Component
public class DailyCommit {

    private static Logger LOG = LoggerFactory.getLogger(DailyCommit.class);

    @Resource
    private GitService gitService;

    @Resource
    private CalendarService calendarService;

    @Resource
    private DiffService diffService;

    @Scheduled(cron = "${cron.dailyCommit}")
    public void dailyCommit() throws Exception {
        LOG.info("Running daily commit.");
        diffService.commitChanges("", gitService.getChangedFilenames());
        final String[] users = { "gunda", "niels" };
        for (final String user : users) {
            LOG.debug("Archiviere Kalender für {}.", user);
            final String changes = calendarService.archive(user);
            if (StringUtils.isNotEmpty(changes)) {
                LOG.warn("Kalender archiviert für {}.", user);
                diffService.commitChanges(changes + "\n", gitService.getChangedFilenames());
            }
        }
    }

}
