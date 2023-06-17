package de.zahrie.trues.discord.event.models;

import de.zahrie.trues.api.calendar.event.Event;
import de.zahrie.trues.api.calendar.event.Round;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(StringUtils.class)
public class ButtonEvent extends ListenerAdapter {

  @Override
  public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
    final String id = event.getButton().getId();
    if (id == null) return;
    if (event.getMember() == null) return;
    final DiscordUser member = DiscordUserFactory.getDiscordUser(event.getMember());

    final Player player = member.getPlayer();
    if (player == null) {
      member.dm("Du musst deinen LOL-Account vorher mit **/settings** registriert haben.");
      return;
    }

    if (id.startsWith("round-")) {
      final Integer integer = id.after("round-").intValue();
      if (integer == null) return;

      new Query<>(Round.class).entity(integer).addPlayer(player);
    }

    if (id.startsWith("event-")) {
      final Integer integer = id.after("event-").intValue();
      if (integer == null) return;

      new Query<>(Event.class).entity(integer).removePlayer(player);
    }
  }
}
