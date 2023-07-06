package de.zahrie.trues.discord.command.models;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.api.discord.channel.AbstractDiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.logging.MessageLog;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "delete", descripion = "Eine Nachricht löschen und loggen lassen", perm = @Perm(PermissionRole.TEAM_CAPTAIN), options = {
    @Option(name = "grund", description = "Grund der Loeschung", choices = {"Beleidigung", "Respektloses Verhalten", "sonstige"}),
    @Option(name = "nummer", description = "x-te Nachricht oder DiscordID oder 0=alle", type = OptionType.INTEGER),
    @Option(name = "nutzer", description = "User der Nachricht", type = OptionType.USER, required = false),
    @Option(name = "dauer", description = "Nachrichten der letzten Stunden", type = OptionType.INTEGER, required = false)
})
public class DeleteMessageCommand extends SlashCommand {
  @Override
  @Msg(value = "{} Nachricht(en) wurden gelöscht und intern geloggt.", error = "Die Nachricht(en) konnte nicht gelöscht werden!")
  public boolean execute(SlashCommandInteractionEvent event) {
    final long index = find("nummer").bigInt((long) -1); // oder so ...
    final DiscordUser user = find("nutzer").discordUser();
    final Integer durationInHours = find("dauer").integer();
    final LocalDateTime from = determineFrom(durationInHours);
    final MessageLog.MessageDeleteReason reason = switch (find("grund").string()) {
      case "Beleidigung" -> MessageLog.MessageDeleteReason.INSULT;
      case "Respektloses Verhalten" -> MessageLog.MessageDeleteReason.BAD_BEHAVIOUR;
      default -> MessageLog.MessageDeleteReason.OTHER;
    };
    final AbstractDiscordChannel channel = DiscordChannelFactory.getDiscordChannel((GuildChannel) event.getChannel());

    final List<Message> messagesToDelete = determineMessages(event, index, user, from);
    messagesToDelete.stream().sorted(Comparator.comparing(Message::getTimeCreated)).forEach(message -> {
      new MessageLog(message.getTimeCreated().toLocalDateTime(), message.getContentDisplay(), getInvoker(), user, channel, reason).create();
      Nunu.DiscordChannel.getAdminChannel().sendMessage("Gelöschte Nachricht von " + user.getMention() + " aufgrund von **" + find("grund").string() + "**\n" + message.getContentDisplay()).queue();
      message.delete().complete();
    });

    if (!messagesToDelete.isEmpty()) {
      event.getChannel().sendMessage("Ich habe " + messagesToDelete.size() + " Nachricht(en) gelöscht.").queue();
    }


    return send(!messagesToDelete.isEmpty(), messagesToDelete.size());
  }

  private static List<Message> determineMessages(SlashCommandInteractionEvent event, long index, DiscordUser user, LocalDateTime from) {
    if (index > 100) {
      final Message message = event.getChannel().retrieveMessageById(index).complete();
      return message == null ? SortedList.of() : List.of(message);
    }

    try {
      final List<Message> messagesToDelete = event.getChannel().getIterableHistory().takeAsync(1000).thenApply(messages -> {
        Stream<Message> stream = messages.stream();
        if (from != null) stream = stream.filter(message -> message.getTimeCreated().toLocalDateTime().isAfter(from));
        if (user != null)
          stream = stream.filter(message -> Util.avoidNull(message.getMember(), 0L, ISnowflake::getIdLong) == user.getDiscordId());
        return SortedList.of(stream);
      }).get();

      if (index > 0) return index > messagesToDelete.size() ? SortedList.of() : List.of(messagesToDelete.get((int) (index - 1)));
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    return SortedList.of();
  }

  private LocalDateTime determineFrom(Integer durationInHours) {
    if (durationInHours == null) return null;
    if (durationInHours == -1 || durationInHours == 0) return LocalDateTime.MIN;
    return LocalDateTime.now().minusHours(durationInHours);
  }
}
