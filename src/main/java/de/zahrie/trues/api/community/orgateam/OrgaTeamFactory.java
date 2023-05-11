package de.zahrie.trues.api.community.orgateam;


import java.util.Arrays;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.group.GroupType;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(StringUtils.class)
public final class OrgaTeamFactory {
  /**
   * Erhalte {@link OrgaTeam} vom Channel oder der Categorie in der sich der registrierte Channel befindet
   */
  @Nullable
  public static OrgaTeam getTeamFromChannel(@NonNull GuildChannel channel) {
    Category category = null;
    if (channel instanceof ICategorizableChannel categorizableChannel) category = categorizableChannel.getParentCategory();
    if (channel instanceof Category categoryChannel) category = categoryChannel;
    channel = Util.avoidNull(category, channel);
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(channel);
    return Util.avoidNull(teamChannel, null, TeamChannel::getOrgaTeam);
  }

  /**
   * Erstelle Team der Organisation
   * @param name Name des Teams (darf nicht Null sein)
   * @param abbreviation wenn {@code null} wird eine Abkürzung generiert
   * @param id wenn {@code not null} wird das {@link OrgaTeam} einem {@link PRMTeam} zugewiesen
   */
  public static OrgaTeam create(@NonNull String name, @Nullable String abbreviation, @Nullable Integer id) {
    if (abbreviation == null) {
      abbreviation = "TRU" + Arrays.stream(name.split(" ")).map(word -> word.substring(0, 1).upper()).collect(Collectors.joining());
    }
    if (fromAbbreviation(abbreviation) != null) return null;

    final String teamName = "TRUE " + name;
    final OrgaTeam orgaTeam = new OrgaTeam(teamName, abbreviation);

    final PRMTeam team = Util.avoidNull(id, null, TeamFactory::getTeam);
    Nunu.getInstance().getGuild().createRole().setName(orgaTeam.getRoleManager().getRoleName())
        .setPermissions().setMentionable(true).setHoisted(true)
        .queue(role -> new CustomDiscordGroup(role.getIdLong(), orgaTeam.getRoleManager().getRoleName(), GroupType.PINGABLE, true, orgaTeam).create());
    orgaTeam.getChannels().createChannels();
    Database.connection().commit();
    return orgaTeam;
  }

  @Nullable
  public static OrgaTeam fromAbbreviation(String abbreviation) {
    return new Query<>(OrgaTeam.class).where("team_abbr_created", abbreviation).entity();
  }
}
