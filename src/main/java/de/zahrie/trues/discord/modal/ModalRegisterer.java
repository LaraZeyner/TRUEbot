package de.zahrie.trues.discord.modal;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.discord.modal.models.ApplyModal;
import de.zahrie.trues.discord.modal.models.ApplyStaffModal;
import de.zahrie.trues.discord.modal.models.MembersEditModal;
import de.zahrie.trues.discord.modal.models.RoleEditModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutChampionsModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutHistoryModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutLineupModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutMatchupsModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutOverviewModal;
import de.zahrie.trues.discord.modal.models.TeamEditModal;
import de.zahrie.trues.discord.modal.models.TeamRemoveModal;
import de.zahrie.trues.discord.modal.models.ScrimCreateModal;
import de.zahrie.trues.discord.modal.models.scouting.ScoutScheduleModal;

public class ModalRegisterer implements Registerer<List<ModalImpl>> {
  public static final String APPLY = "apply";
  public static final String APPLY_STAFF = "apply-staff";
  public static final String ROLE_EDIT = "roleedit";
  public static final String MEMBER_EDIT = "memberedit";
  public static final String SCOUT_CHAMPIONS = "scout-champions";
  public static final String SCOUT_HISTORY = "scout-overview";
  public static final String SCOUT_LINEUP = "scout-lineup";
  public static final String SCOUT_MATCHUPS = "scout-matchups";
  public static final String SCOUT_OVERVIEW = "scout-overview";
  public static final String SCOUT_SCHEDULE = "scout-schedule";
  public static final String SCRIM_CREATE = "scrim-create";
  public static final String SCRIM_EDIT = "scrim-edit";
  public static final String TEAM_EDIT = "teamedit";
  public static final String TEAM_REMOVE = "teamremove";

  @Override
  public List<ModalImpl> register() {
    return List.of(
        new ApplyModal(),
        new ApplyStaffModal(),
        new RoleEditModal(),
        new MembersEditModal(),
        new ScoutChampionsModal(),
        new ScoutHistoryModal(),
        new ScoutLineupModal(),
        new ScoutMatchupsModal(),
        new ScoutOverviewModal(),
        new ScoutScheduleModal(),
        new TeamEditModal(),
        new TeamRemoveModal(),
        new ScrimCreateModal()
    );
  }
}
