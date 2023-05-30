package de.zahrie.trues.api.discord.command.context;

import java.util.function.Predicate;

import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Replyer;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class ContextCommand extends Replyer {
  private Command.Type type;
  private Predicate<DiscordUser> permissionCheck = o -> true;

  public ContextCommand() {
    super(UserContextInteractionEvent.class);
    final Context annotation = getClass().asSubclass(this.getClass()).getAnnotation(Context.class);
    if (annotation == null) {
      return;
    }
    this.name = annotation.value();
    this.type = annotation.type();
  }

  protected abstract boolean execute(UserContextInteractionEvent event);

  protected void setPermission(Predicate<DiscordUser> permissionCheck) {
    this.permissionCheck = permissionCheck;
  }

  public void handleCommand(UserContextInteractionEvent event) {
    if (!hasModal()) event.deferReply(true).queue();

    final Member targetMember = event.getTargetMember();
    if (targetMember == null) {
      reply("Internal Error");
      return;
    }

    if (permissionCheck.test(DiscordUserFactory.getDiscordUser(event.getTargetMember()))) {
      this.event = event;
      customEmbedData.clear();
      execute(event);
      return;
    }

    else reply("Dir fehlen die nötigen Rechte.");
    reply("Internal Error");
  }
}
