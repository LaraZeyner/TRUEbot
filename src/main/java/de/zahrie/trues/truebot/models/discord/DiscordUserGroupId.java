package de.zahrie.trues.truebot.models.discord;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import lombok.Data;

@Data
public class DiscordUserGroupId implements Serializable {
  @Serial
  private static final long serialVersionUID = -4019411133222981730L;

  private DiscordUser member;

  private DiscordGroup group;

  private Calendar start;

}