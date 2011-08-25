package org.apache.flume.source;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.event.SimpleEvent;

public class SequenceGeneratorSource extends AbstractEventSource {

  private long sequence;

  @Override
  public Event next(Context context) throws InterruptedException,
      EventDeliveryException {

    Event event = new SimpleEvent();

    event.setBody(Long.valueOf(sequence++).toString().getBytes());

    return event;
  }

}