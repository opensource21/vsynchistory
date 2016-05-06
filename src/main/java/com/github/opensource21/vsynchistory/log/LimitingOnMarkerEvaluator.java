package com.github.opensource21.vsynchistory.log;

import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;


/**
 * Protect a appender to get no to much events per second.
 * @author niels
 *
 */
public class LimitingOnMarkerEvaluator extends OnMarkerEvaluator {

  private long lastSend = 0;
  private long interval = 0;

  @Override
  public boolean evaluate(ILoggingEvent event) throws EvaluationException {
    if (super.evaluate(event)) {
      final long now = System.currentTimeMillis();

      if (now - lastSend > interval) {
        lastSend = now;
        return true;
      }
    }

    return false;
  }


  public long getInterval() {
    return interval;
  }

  public void setInterval(long interval) {
    this.interval = interval;
  }

}