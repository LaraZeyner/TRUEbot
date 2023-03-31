package de.zahrie.trues.discord.command.models.training;

import java.util.Calendar;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.calendar.TeamCalendar.TeamCalendarType;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "add", descripion = "Training Commands", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "start", description = "Startzeitpunkt"),
    @Option(name = "dauer", description = "Dauer", required = false, type = OptionType.INTEGER),
    @Option(name = "typ", description = "Typ", choices = {"Kalibrierung", "Coaching", "Clash", "Meeting", "Training"}),
    @Option(name = "details", description = "Details", required = false)
})
@ExtensionMethod(StringExtention.class)
public class TrainingAddCommand extends SlashCommand {
  public TrainingAddCommand(SlashCommand... commands) {
    super(commands);
  }

  @Override
  @Msg(value = "Das Ereignis wurde eingetragen.", error = "Das Zeitformat ist fehlerhaft.")
  public boolean execute(SlashCommandInteractionEvent event) {
    if (getLocatedTeam() == null) return reply("Das hier ist kein Teamchannel!");
    final Time start = find("start").time();
    if (start == null) return errorMessage();

    final Integer duration = find("dauer").integer(150);
    final Time end = start.plus(Calendar.MINUTE, duration);
    final TeamCalendarType type = find("typ").toEnum(TeamCalendarType.class, TeamCalendarType.TRAINING);
    final TeamCalendar teamEntry = new TeamCalendar(start, end, find("details").string(), type, getLocatedTeam());
    Database.save(teamEntry);
    return sendMessage();
  }
}
