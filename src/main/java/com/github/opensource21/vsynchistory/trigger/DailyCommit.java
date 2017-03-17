/**
 *
 */
package com.github.opensource21.vsynchistory.trigger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.model.HolidayEvent;
import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;
import com.github.opensource21.vsynchistory.service.api.HolidayService;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ValidationException;

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

    @Resource
    private HolidayService holidayService;
    
    @Value(value = "${archive.user}")
    private String archiveUsersAsString;

    @Value(value = "${holidays.calendar}")
    private String holidaysCalendarsAsString;

    @Value(value = "${holidays.start}")
    private int startYear;

    @Value(value = "${holidays.end}")
    private int endYear;
    
    @Value(value = "${holidays.bundesland}")
    private String bundesland;
    
    @Scheduled(cron = "${cron.dailyCommit}")
    public void dailyCommit() throws Exception {
        LOG.info("Running daily commit.");
        diffService.commitChanges("", gitService.getChangedFilenames());
        handleArchive();
        importHolidays();
    }

    private void importHolidays() throws IOException, ParserException,
            ValidationException, GitAPIException {
        if (StringUtils.isEmpty(holidaysCalendarsAsString)) {
            LOG.debug("Keine Kalender in holidays.calendar angegeben.");
            return;
        }
        
        final int realStartYear = Math.max(LocalDate.now().getYear(), startYear);
        final int realEndYear = Math.max(realStartYear, endYear);
        
        final Set<HolidayEvent> holidays = holidayService.getHolydays(
                realStartYear, realEndYear , "ni");
        
        final String[] calendars = holidaysCalendarsAsString.trim().split("[ ,]+");
        for (final String calendar : calendars) {
            LOG.debug("Importiere Feiertage in Kalender {}.", calendar);
            final String changes = calendarService.addHolydays(calendar, holidays);
            if (StringUtils.isNotEmpty(changes)) {
                LOG.warn("Feiertage importiert für Kalender {}.", calendar);
                diffService.commitChanges(changes + "\n",
                        gitService.getChangedFilenames());
            }
        }
    }

    private void handleArchive() throws IOException, ParserException,
            ValidationException, GitAPIException {
        if (StringUtils.isEmpty(archiveUsersAsString)) {
            LOG.debug("Keine Archiv user mit archive.user angegeben.");
            return;
        }
        final String[] users = archiveUsersAsString.trim().split("[ ,]+");
        for (final String user : users) {
            LOG.debug("Archiviere Kalender für {}.", user);
            final String changes = calendarService.archive(user);
            if (StringUtils.isNotEmpty(changes)) {
                LOG.warn("Kalender archiviert für {}.", user);
                diffService.commitChanges(changes + "\n",
                        gitService.getChangedFilenames());
            }
        }
    }

}
