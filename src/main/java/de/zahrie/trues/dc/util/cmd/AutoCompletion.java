package de.zahrie.trues.dc.util.cmd;

import java.util.List;

import de.zahrie.trues.PrimeData;
import net.dv8tion.jda.api.interactions.commands.Command;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
public record AutoCompletion (String optionName, String query) {
  public List<Command.Choice> getData() {
    return PrimeData.getInstance().getSession().createNamedQuery(this.query, String.class)
        .getResultList().stream().map(entry -> new Command.Choice(entry, entry)).toList();
  }
}
