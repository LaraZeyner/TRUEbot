package de.zahrie.trues.discord.command.models.training;

import java.time.LocalDateTime;

import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "training", descripion = "Training Commands", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "start", description = "Startzeitpunkt"),
    @Option(name = "dauer", description = "Dauer", required = false, type = OptionType.INTEGER),
    @Option(name = "typ", description = "Typ", choices = {"Kalibrierung", "Coaching", "Clash", "Meeting", "Training"}),
    @Option(name = "details", description = "Details", required = false)
})
@ExtensionMethod(StringUtils.class)
public class TrainingCommand extends SlashCommand {

  @Override
  @Msg(value = "Das Ereignis wurde eingetragen.", error = "Das Zeitformat ist fehlerhaft.")
  public boolean execute(SlashCommandInteractionEvent event) {
    if (getLocatedTeam() == null) return reply("Das hier ist kein Teamchannel!");
    final LocalDateTime start = find("start").time();
    final String details = find("details").string();
    if (start == null) return errorMessage();

    final Integer duration = find("dauer").integer(150);
    final LocalDateTime end = start.plusMinutes(duration);
    final var timeRange = new TimeRange(start, end);
    final TeamCalendar.TeamCalendarType type = find("typ").toEnum(TeamCalendar.TeamCalendarType.class, TeamCalendar.TeamCalendarType.TRAINING);
    final TextChannel textChannel = (TextChannel) getLocatedTeam().getChannelOf(TeamChannelType.SCOUTING);
    final String typeString = find("typ").string();
    textChannel.sendMessage("**Neues " + typeString + " am " + TimeFormat.DEFAULT.of(start) + "**\n" + details)
        .queue(message -> textChannel.createThreadChannel(typeString + " am " + TimeFormat.DAY_LONG.of(start))
            .queue(threadChannel -> Database.save(new TeamCalendar(timeRange, details, type, getLocatedTeam(), threadChannel.getIdLong()))));
    return sendMessage();
  }
}
