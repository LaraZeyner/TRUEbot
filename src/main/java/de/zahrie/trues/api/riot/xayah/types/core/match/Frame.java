package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;
import java.util.Map;
import java.util.function.Function;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import org.joda.time.Duration;

public abstract class Frame extends OriannaObject.ListProxy<Event, de.zahrie.trues.api.riot.xayah.types.data.match.Event, de.zahrie.trues.api.riot.xayah.types.data.match.Frame> implements Comparable<Frame> {
  @Serial
  private static final long serialVersionUID = 460862393234334223L;

  public Frame(final de.zahrie.trues.api.riot.xayah.types.data.match.Frame coreData) {
    super(coreData);
  }

  public Frame(final de.zahrie.trues.api.riot.xayah.types.data.match.Frame coreData,
               final Function<de.zahrie.trues.api.riot.xayah.types.data.match.Event, Event> transform) {
    super(coreData, transform::apply);
  }

  @Override
  public int compareTo(final Frame o) {
    return getTimestamp().compareTo(o.getTimestamp());
  }

  public abstract Map<MatchParticipant, ParticipantFrame> getParticipantFrames();

  public abstract Duration getTimestamp();
}
