package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.logging.MessageLog;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "strike", descripion = "Einen Nutzer verwarnen", perm = @Perm(PermissionRole.MANAGEMENT), options = {
    @Option(name = "nutzer", description = "User der Nachricht", type = OptionType.USER),
    @Option(name = "grund", description = "Grund der Verwarnung", choices = {"Beleidigung", "Respektloses Verhalten", "sonstige"}),
    @Option(name = "beschreibung", description = "Genaue Beschreibung der Verwarnung")
})
public class StrikeCommand extends SlashCommand {
  @Override
  @Msg(value = "Die Verwarnung wurde erstellt. Ich habe den Nutzer benachrichtigt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final DiscordUser user = find("nutzer").discordUser();
    final MessageLog.MessageDeleteReason reason = switch (find("grund").string()) {
      case "Beleidigung" -> MessageLog.MessageDeleteReason.INSULT;
      case "Respektloses Verhalten" -> MessageLog.MessageDeleteReason.BAD_BEHAVIOUR;
      default -> MessageLog.MessageDeleteReason.OTHER;
    };
    final String description = find("beschreibung").string();
    new MessageLog(getInvoker(), user, description, reason).create();
    user.dm("Du hast eine Verwarnung erhalten: **" + find("grund").string() + "**\n" + description + "\n\n\n_Solltest du widersprechen wollen kontaktiere bitte einen Admin._");
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neue Verwarnung für " + user.getMention() + ": **" + find("grund").string() + "**\n" + description + "\n\n\n_von " + getInvokingMember().getNickname() + "_").queue();
    return sendMessage();
  }
}
