package de.zahrie.trues.discord;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.scheduling.DateTimeStringConverter;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Message;

@Data
@RequiredArgsConstructor
@ExtensionMethod(StringUtils.class)
public final class Settings {
  private final Message message;

  private Map<RegistrationAction, String> getActions() {
    final String contentDisplay = message.getContentDisplay();
    return Arrays.stream(contentDisplay.split("\n"))
        .filter(line -> line.before(":").upper().toEnum(RegistrationAction.class) != null)
        .collect(Collectors.toMap(line -> line.before(":").toEnum(RegistrationAction.class),
            line -> line.after(":"), (a, b) -> b));
  }

  public boolean validate() {
    return !getActions().isEmpty();
  }

  public void execute(DiscordUser discordUser) {
    getActions().forEach((registrationAction, s) -> registrationAction.getAction().apply(discordUser, s));
  }

  public Message message() {
    return message;
  }


  @RequiredArgsConstructor
  @Getter
  public enum RegistrationAction {
    LOL_NAME((user, userName) -> {
      final Player player = PlayerFactory.getPlayerFromName(userName);
      if (player == null) return "Der Name **" + userName + "** konnte nicht gefunden werden.";
      if (player.getDiscordUser() != null) return "Der Account wurde bereits verknüpft.";
      player.setDiscordUser(user);
      return "**" + user.getMention() + "** wurde mit dem Namen " + userName + " registriert";
    }),
    BDAY((user, birthday) -> {
      final LocalDate birthdate = new DateTimeStringConverter(birthday).toTime().toLocalDate();
      if (birthdate == null) return "Das Format ist fehlerhaft.";

      user.setBirthday(birthdate);
      Database.update(user);
      return "Dein Geburtstag wurde für den " + birthday + " eingetragen";
    }),
    NOTIFY((user, minutes) -> {
      final Integer integer = minutes.intValue(null);
      if (integer == null) return "Das Format ist fehlerhaft (-1 für deaktivieren)";
      user.setNotification(integer.shortValue());
      return "Die Benachrichtigung wurde auf " + integer + " Minuten gestellt.";
    });

    private final BiFunction<DiscordUser, String, String> action;
  }
}
