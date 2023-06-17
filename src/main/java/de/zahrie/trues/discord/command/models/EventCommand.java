package de.zahrie.trues.discord.command.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import de.zahrie.trues.api.calendar.event.Event;
import de.zahrie.trues.api.calendar.event.EventCalendar;
import de.zahrie.trues.api.calendar.event.GameMode;
import de.zahrie.trues.api.calendar.event.PlayerLimit;
import de.zahrie.trues.api.calendar.event.Round;
import de.zahrie.trues.api.calendar.scheduling.DateTimeStringConverter;
import de.zahrie.trues.api.calendar.scheduling.TimeStringConverter;
import de.zahrie.trues.api.community.betting.BetFactory;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.riot.GameMap;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "event", descripion = "Ein Event erstellen", perm = @Perm(PermissionRole.EVENT_PLANNING), options = {
    @Option(name = "spielmodus", description = "Spielmodus wählen", choices = {"HA saufen", "SR Handicap"}),
    @Option(name = "map", description = "Spielmodus wählen", choices = {"Summoners Rift", "Twisted Treeline", "The Crystal Scar", "Howling Abyss", "Cosmic Ruins", "Valoran City Park", "Substructure 43", "Crash Site", "Convergence", "Temple of Lotus and Lilly"}),
    @Option(name = "tag", description = "Starttag"),
    @Option(name = "startzeiten", description = "Zeiten mit Leerzeichen getrennt"),
    @Option(name = "letzte", description = "letztmögliche Startzeit"),
    @Option(name = "spielerminimum", description = "minimale Anzahl an Spielern", type = OptionType.INTEGER),
    @Option(name = "spielermaximum", description = "maximale Anzahl an Spielern", type = OptionType.INTEGER),
    @Option(name = "wichtiges", description = "Wichtige zusätzliche Informationen", required = false)
})
@ExtensionMethod({BetFactory.class, StringUtils.class})
public class EventCommand extends SlashCommand {
  @Override
  @Msg(value = "Das Event wurde erfolgreich erstellt.", error = "Das Event konnte nicht erstellt werden!")
  public boolean execute(SlashCommandInteractionEvent event) {
    final var gamemode = find("spielmodus").toEnum(GameMode.class);
    final var map = find("map").toEnum(GameMap.class);
    final var important = find("wichtiges").string();
    final var dayString = find("tag").string();
    final LocalDateTime startingPoint = new DateTimeStringConverter(dayString).toTime();
    if (startingPoint == null) return reply("Der Startzeitpunkt ist ungültig.");

    final var timesString = find("startzeiten").string();
    if (Arrays.stream(timesString.split(" ")).anyMatch(s -> new TimeStringConverter(s).getTime() == null)) return reply("Die Zeiten sind fhelerhaft.");

    EventCalendar calendar = null;
    Event e = null;

    final var endTimeString = find("letzte").string();
    final LocalTime endTime = new TimeStringConverter(endTimeString).getTime();
    if (endTime == null) return reply("Der Endzeitpunkt ist ungültig.");

    final var playersRequired = find("spielerminimum").integer(6);
    final var playerLimit = find("spielermaximum").integer(10);

    final LocalDateTime end = LocalDateTime.of(startingPoint.toLocalDate(), endTime);
    for (final String s : timesString.split(" ")) {
      final LocalTime time = new TimeStringConverter(s).getTime();
      final LocalDateTime start = LocalDateTime.of(startingPoint.toLocalDate(), time);
      if (calendar == null) {
        calendar = new EventCalendar(new TimeRange(start, end), important, getInvoker()).create();
        e = new Event(calendar, gamemode, map, new PlayerLimit(playersRequired, playerLimit)).create();
      }
      final Round round = new Round(e, start.toLocalTime()).create();
      e.addRound(round);
    }
    if (e != null) {
      e.createScheduledEvent();
      e.sendMessage();
      return sendMessage();
    }
    return errorMessage();
  }
}
