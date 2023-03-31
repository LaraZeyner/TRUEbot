package de.zahrie.trues.api.discord.command.context;

import java.util.function.Predicate;

import de.zahrie.trues.api.discord.util.Replyer;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class ContextCommand extends Replyer {
  private Command.Type type;
  private Predicate<DiscordUser> permissionCheck = o -> true;
  private UserContextInteractionEvent event;

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
    if (permissionCheck.test(getInvoker())) {
      this.event = event;
      execute(event);
    } else {
      reply("Dir fehlen die nötigen Rechte.", true);
    }
    if (!end) {
      reply("Internal Error", true);
    }
  }
}
