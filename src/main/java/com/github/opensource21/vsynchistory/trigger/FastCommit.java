/**
 *
 */
package com.github.opensource21.vsynchistory.trigger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.opensource21.vsynchistory.service.api.CalendarService;
import com.github.opensource21.vsynchistory.service.api.DiffService;
import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Trigger welcher versucht nach jeder Änderung ein commit zu machen.
 *
 * @author niels
 *
 */
@Component
public class FastCommit implements InitializingBean  {

	private static Logger LOG = LoggerFactory.getLogger(DailyCommit.class);

	@Resource
	private GitService gitService;

	@Resource
	private CalendarService calendarService;

	@Resource
	private DiffService diffService;

	@Value(value = "${repositoryLocation}")
	private String repositoryLocation;

	private WatchService watcher;

	private volatile long lastChange = Long.MAX_VALUE;

	@Value("${watch.silencetime}")
	private long silenceTime;

	@Scheduled(initialDelay = 1500, fixedDelayString = "${watch.fixedDelay}")
    public void dailyCommit() throws Exception {
		if ((System.currentTimeMillis() - lastChange) > silenceTime) {
			diffService.commitChanges("", gitService.getChangedFilenames());
			lastChange = Long.MAX_VALUE;
		}
    }

	@Scheduled(initialDelay = 1000, fixedDelayString = "${watch.fixedDelay}")
	public void waitForChanges() {
		try {
			final WatchKey watckKey = watcher.take();
			final List<WatchEvent<?>> events = watckKey.pollEvents();
			for (final WatchEvent<?> event : events) {
				if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
					LOG.debug("Created: " + event.context().toString());
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					LOG.debug("Delete: " + event.context().toString());
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
					LOG.debug("Modify: " + event.context().toString());
				}
			}
			lastChange = System.currentTimeMillis();
			watckKey.reset();
		} catch (final InterruptedException e) {
			LOG.error("Filewatch wurd unterbrochen: ", e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Path repoDir = Paths.get(repositoryLocation);
		watcher = repoDir.getFileSystem().newWatchService();
		repoDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);

	}
}