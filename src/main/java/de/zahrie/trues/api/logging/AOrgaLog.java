package de.zahrie.trues.api.logging;

import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;

@Table("orga_log")
public interface AOrgaLog {
  String getDetails();
  LocalDateTime getTimestamp();
}
