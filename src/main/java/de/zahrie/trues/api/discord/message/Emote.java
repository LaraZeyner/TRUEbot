package de.zahrie.trues.api.discord.message;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@RequiredArgsConstructor
public enum Emote {
  TOP("top", 1082970814569254932L, false),
  JUNGLE("jgl", 1082971266664898610L, false),
  MIDDLE("mid", 1082971285858025492L, false),
  BOTTOM("bot", 1082971310486982656L, false),
  SUPPORT("sup", 1082971327071273010L, false);

  private final String name;
  private final long id;
  private final boolean animated;

  public Emoji getEmoji() {
    return Emoji.fromCustom(name, id, animated);
  }
}
