package de.zahrie.trues.api.discord.command.slash;

import java.util.List;

import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.interactions.commands.Command;

public record AutoCompletion (String optionName, String query) {
  public List<Command.Choice> getData() {
    return Database.connection().getSession().createNamedQuery(this.query, String.class)
        .getResultList().stream().map(entry -> new Command.Choice(entry, entry)).toList();
  }
}
