package de.zahrie.trues.discord.context;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.discord.context.models.ApplyContext;
import de.zahrie.trues.discord.context.models.ProfileContext;
import de.zahrie.trues.discord.context.models.RoleEditContext;
import de.zahrie.trues.discord.context.models.TeamEditContext;
import de.zahrie.trues.discord.context.models.TeamRemoveContext;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContextRegisterer extends ListenerAdapter implements Registerer<List<ContextCommand>> {
  @Override
  public List<ContextCommand> register() {
    return List.of(
        new ApplyContext(),
        new ProfileContext(),
        new RoleEditContext(),
        new TeamEditContext(),
        new TeamRemoveContext()
    );
  }
}
