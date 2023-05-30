package de.zahrie.trues.api.discord.builder.modal;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@ExtensionMethod(StringUtils.class)
public abstract class ModalImpl extends ModalBase {
  protected DiscordUser target;

  public void setTarget(DiscordUser target) {
    this.target = target;
  }

  public ModalImpl() {
    super();
  }

  public ModalImpl create(String title) {
    builder = Modal.create(name, title);
    return this;
  }

  public ModalImpl single(String index, String title, String description, int length) {
    builder.addComponents(getRow(index, title, description, length, TextInputStyle.SHORT));
    return this;
  }

  public ModalImpl single(String index, String title, Query<?> description, int length) {
    final String desc = description.list().stream().map(objects -> (String) objects[0]).collect(Collectors.joining(", "));
    return single(index, title, desc, length);
  }

  public ModalImpl single(String index, String title, List<? extends Enum<?>> enums) {
    final String desc = enums.stream().map(Enum::toString).filter(Objects::nonNull).collect(Collectors.joining(", "));
    final int length = enums.stream().map(Enum::toString).filter(Objects::nonNull).map(String::length).mapToInt(Integer::intValue).max().orElse(25);
    return single(index, title, desc, length);
  }

  public ModalImpl multi(String index, String title, String description, int length) {
    builder.addComponents(getRow(index, title, description, length, TextInputStyle.PARAGRAPH));
    return this;
  }

  public ModalImpl multi(String index, String title, List<? extends Enum<?>> enums) {
    final String desc = enums.stream().map(Enum::toString).filter(Objects::nonNull).collect(Collectors.joining(", "));
    final int length = enums.stream().map(Enum::toString).filter(Objects::nonNull).map(String::length).mapToInt(Integer::intValue).max().orElse(25);
    return multi(index, title, desc, length);
  }

  private ActionRow getRow(String index, String title, String description, int length, TextInputStyle style) {
    return ActionRow.of(TextInput.create(index, title, style).setPlaceholder(description).setMaxLength(length).build());
  }

  public Modal get() {
    return builder.build();
  }

  public String getString(String index) {
    return Util.nonNull(modalEvent().getValue(index)).getAsString();
  }

  public Integer getInt(String index) {
    try {
      return Integer.parseInt(getString(index));
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  public Boolean getBoolean(String index) {
    return switch (getString(index).toLowerCase()) {
      case "ja" -> true;
      case "nein" -> false;
      default -> null;
    };
  }

  @Nullable
  protected <T extends Enum<T>> T getEnum(Class<T> enumClass, String index) {
    final String string = getString(index);
    return Arrays.stream(enumClass.getEnumConstants()).filter(t -> t.toString() != null).filter(t -> t.toString().equalsIgnoreCase(string)).findFirst().orElse(null);
  }

  //<editor-fold desc="objects">
  @Override
  protected DiscordUser getInvoker() {
    final String targetIdString = Util.nonNull(modalEvent().getValue("target-name")).getAsString();
    return new Query<>(DiscordUser.class).entity(targetIdString.intValue());
  }
  //</editor-fold>

  private ModalInteractionEvent modalEvent() {
    return ((ModalInteractionEvent) event);
  }
}
