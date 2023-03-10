package de.zahrie.trues.discord.modal;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.discord.modal.models.RoleEditModal;
import de.zahrie.trues.discord.modal.models.TeamEditModal;
import de.zahrie.trues.discord.modal.models.TeamRemoveModal;

public class ModalRegisterer implements Registerer<List<ModalImpl>> {
  public static final String PROFILE = "profile";
  public static final String ROLE_EDIT = "roleedit";
  public static final String TEAM_EDIT = "teamedit";
  public static final String TEAM_REMOVE = "teamremove";

  @Override
  public List<ModalImpl> register() {
    return List.of(
        new RoleEditModal(),
        new TeamEditModal(),
        new TeamRemoveModal()
    );
  }
}
