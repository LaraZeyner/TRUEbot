package de.zahrie.trues.discord.scouting;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public record CustomScouting(Team opponent) {
  public void sendCustom(IReplyCallback event, ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(type.getTitleStart() + opponent.getName())
        .setDescription("Lineup: opgg und porofessor coming soon\nTyp: ");
    new ScoutingEmbedHandler(opponent, null, gameType, days, page).get(type, null).forEach(builder::addField);
    event.getHook().sendMessageEmbeds(builder.build()).queue();
  }
}
