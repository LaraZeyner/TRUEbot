package de.zahrie.trues.models.community.application;

public enum TeamRole {
  /**
   * Bewerbungsgespräch angenommen <br>
   * Wenn Tryout für Team dann ist dies die Auswahlrolle
   */
  TRYOUT,
  /**
   * Substitude oder wenn temporär Tryout für dieses Team
   */
  SUBSTITUDE,
  /**
   * Stammspieler (max. 5 pro Team)
   */
  MAIN,
  /**
   * Teil als Staffmember
   */
  ORGA
}
