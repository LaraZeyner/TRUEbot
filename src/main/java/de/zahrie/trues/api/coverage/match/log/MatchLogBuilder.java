package de.zahrie.trues.api.coverage.match.log;

import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record MatchLogBuilder(Match match, Team team, List<MatchLog> matchLogs) {
  public MatchLogBuilder(Match match, Team team) {
    this(match, team, match.getLogs().stream().filter(matchLog -> matchLog.getAction().getOutput() != null).sorted(Comparator.reverseOrder()).toList());
  }

  public MessageEmbed getLog() {
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Match " + match.getId() + " gegen " + team.getName() + " (" + team.getId() + ")")
        .setDescription(getDescription());
    if (matchLogs.size() > 1) getFields().forEach(builder::addField);
    return builder.build();
  }

  private String getDescription() {
    if (matchLogs.isEmpty()) {
      return "Match erstellt - Ausweichtermin am: **" + match.getStart().text(TimeFormat.DEFAULT) + "**";
    }
    final MatchLog log = matchLogs.get(0);
    return log.getAction().getOutput() + "( von " + log.getParticipator().getTeam().getName() + " )\n" + log.detailsOutput();
  }

  private List<MessageEmbed.Field> getFields() {
    return new EmbedFieldBuilder<>(matchLogs.subList(1, matchLogs.size()))
        .add("Action", MatchLog::actionOutput)
        .add("Team", MatchLog::teamOutput)
        .add("Details", MatchLog::detailsOutput)
        .build();
  }
}
