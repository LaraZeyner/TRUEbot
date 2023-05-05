package de.zahrie.trues.api.coverage.season;

import java.util.List;

import de.zahrie.trues.api.coverage.EventDTO;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ASeason extends Comparable<ASeason> {
  List<Stage> getStages();

  @NonNull Stage getStage(@NonNull Stage.StageType stageType);

  @Nullable
  Stage getStage(int prmId);

  @NonNull PlayStage getStageOfId(int id);

  String getSignupStatusForTeam(PRMTeam team);

  @NonNull List<EventDTO> getEvents();

  String getName();

  String getFullName();

  de.zahrie.trues.api.datatypes.calendar.TimeRange getRange();

  boolean isActive();

  void setRange(de.zahrie.trues.api.datatypes.calendar.TimeRange range);

  void setActive(boolean active);

  @Override
  default int compareTo(@NotNull ASeason o) {
    return getRange().compareTo(o.getRange());
  }
}
