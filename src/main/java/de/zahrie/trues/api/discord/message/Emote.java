package de.zahrie.trues.api.discord.message;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@RequiredArgsConstructor
public enum Emote {
  TOP("<:top1:1004702918399758427>", null),
  JUNGLE("<:jgl1:1004702947789254656>", null),
  MIDDLE("<:mid1:1004703031104905286>", null),
  BOTTOM("<:bot1:1004703088982106112>", null),
  SUPPORT("<:sup1:1004703127796187246>", null);

  private final String name;
  private final Long animatedId;

  public Emoji getEmoji() {
    return animatedId == null ? Emoji.fromFormatted(name) : Emoji.fromCustom(name, animatedId, true);
  }
}
