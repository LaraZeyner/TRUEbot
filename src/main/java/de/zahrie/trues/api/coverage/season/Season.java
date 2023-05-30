package de.zahrie.trues.api.coverage.season;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.coverage.ABetable;
import de.zahrie.trues.api.coverage.EventDTO;
import de.zahrie.trues.api.coverage.match.model.AScheduleable;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.SignupStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Setter
@Table("coverage_season")
public abstract class Season implements ABetable, Id, AScheduleable, ASeason {
  protected int id;
  protected final String name; // season_name
  protected final String fullName; // season_full
  protected TimeRange range; // season_start, season_end
  protected boolean active; // active

  @Override
  public List<Stage> getStages() {
    return new Query<>(Stage.class).where("season", this).entityList();
  }

  @Override
  @NonNull
  public Stage getStage(@NonNull Stage.StageType stageType) {
    return getStages().stream().filter(stage -> stageType.getEntityClass().isInstance(stage)).findFirst().orElseThrow();
  }

  @Override
  @Nullable
  public Stage getStage(int prmId) {
    final Stage.StageType stageType = Stage.StageType.fromPrmId(prmId);
    return Util.avoidNull(stageType, null, this::getStage);
  }

  @Override
  @NonNull
  public PlayStage getStageOfId(int id) {
    for (Stage stage : getStages()) {
      final var playStage = (PlayStage) stage;
      if ((playStage.pageId() == id)) return Util.nonNull(playStage);
    }
    final RuntimeException exception = new NullPointerException("Stage cannot be null");
    new DevInfo().severe(exception);
    throw exception;
  }

  @Override
  public String getSignupStatusForTeam(PRMTeam team) {
    if (team == null) return "kein Team gefunden";
    if (team.getSignupForSeason(this) != null) return "angemeldet";
    return getStages().stream().filter(stage -> stage instanceof SignupStage).findFirst()
        .map(stage -> stage.getRange().hasStarted() ? "Anmeldung gestartet" : "Anmeldung " +
            TimeFormat.DISCORD.of(stage.getRange().getStartTime()))
        .orElse("keine Anmeldung eingerichtet");
  }

  @Override
  @NonNull
  public List<EventDTO> getEvents() {
    return new ArrayList<>(getStages().stream().map(stage -> new EventDTO(stage.getRange(), stage.type(), false)).toList()).stream().sorted().toList();
  }
}
