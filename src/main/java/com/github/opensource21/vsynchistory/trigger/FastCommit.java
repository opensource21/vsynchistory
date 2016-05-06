/**
 *
 */
package com.github.opensource21.vsynchistory.trigger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
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
public class FastCommit implements InitializingBean {

    private static Logger LOG = LoggerFactory.getLogger(FastCommit.class);

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

        final long currentTimeMillis = System.currentTimeMillis();
        final long timeSinceLastChange = currentTimeMillis - lastChange;
        LOG.trace(
                "Currenttime: {}, lastChange{}, timeSinceLastChange: {}, silenceTime: {}",
                currentTimeMillis, lastChange, timeSinceLastChange, silenceTime);
        if (timeSinceLastChange > silenceTime) {
            LOG.info("Run-Commit");
            lastChange = Long.MAX_VALUE;
            diffService.commitChanges("", gitService.getChangedFilenames());
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelayString = "${watch.fixedDelay}")
    public void waitForChanges() {
        try {
            final WatchKey watchKey = watcher.take();
            final List<WatchEvent<?>> events = watchKey.pollEvents();
            for (final WatchEvent<?> event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    LOG.debug("Created: {} ", event.context());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    LOG.debug("Delete: {}", event.context());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    LOG.debug("Modify: {}", event.context());
                }
            }
            lastChange = System.currentTimeMillis();
            watchKey.reset();
        } catch (final InterruptedException e) {
            LOG.error("Filewatch wurd unterbrochen: ", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Path repoDir = Paths.get(repositoryLocation);
        watcher = repoDir.getFileSystem().newWatchService();
        registerAll(repoDir);

    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                if (dir.toString().endsWith(".git")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                LOG.debug("Register directory {}.", dir.toAbsolutePath());
                dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
