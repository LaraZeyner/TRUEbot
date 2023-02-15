package de.zahrie.trues.models.discord.member;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import de.zahrie.trues.models.discord.DiscordGroup;
import lombok.Data;

@Data
public class DiscordMemberGroupId implements Serializable {
  @Serial
  private static final long serialVersionUID = -4019411133222981730L;

  private DiscordMember member;

  private DiscordGroup group;

  private Calendar start;

}