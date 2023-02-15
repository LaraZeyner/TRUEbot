package de.zahrie.trues.util.io.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.util.Util;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class HTML {
  private final String html;

  HTML(String html) {
    this.html = html;
  }

  /*
  public HTML read(String tagString) {
    final String[] tags = tagString.split("\\.");
    String htmlToRead = html;
    for (String tag : tags) htmlToRead = new HTML(htmlToRead).readTag(tag).toString();
    return new HTML(htmlToRead);
  }

  public HTML readTag(String tag) {
    if (html.contains("<" + tag + ">")) {
      return new HTML(html.substring(html.indexOf("<" + tag + ">") + tag.length() + 2, html.indexOf("</" + tag + ">")));
    }
    return this;
  }*/

  public HTML findByClass(String tag, String clazz) {
    final List<HTML> htmlFound = findAllByClass(tag, clazz);
    return htmlFound.isEmpty() ? new HTML("") : htmlFound.get(0);
  }

  public HTML findById(String tag, String id) {
    final List<HTML> htmlFound = findAllById(tag, id);
    return htmlFound.isEmpty() ? new HTML("") : htmlFound.get(0);
  }

  public HTML find(String tag) {
    final List<HTML> htmlFound = findAll(tag);
    return htmlFound.isEmpty() ? new HTML("") : htmlFound.get(0);
  }

  public List<HTML> findAllByClass(String tag, String clazz) {
    final List<String> split = Arrays.asList(html.split("<" + tag));
    if (!html.startsWith("<" + tag)) {
      split.remove(0);
    }

    return Arrays.stream(html.split("<" + tag))
        .filter(str -> str.contains("class=\"") && (clazz == null || Util.between(str, "class=\"", "\"").contains(clazz)))
        .mapToInt(html::indexOf)
        .mapToObj(index -> html.substring(index, findClosingIndex(tag, index))).map(HTML::new).collect(Collectors.toList());
  }

  public List<HTML> findAll(String tag) {
    final List<String> split = Arrays.asList(html.split("<" + tag));
    removeFirstAndLast(tag, split);

    return split.stream()
        .mapToInt(html::indexOf)
        .mapToObj(index -> html.substring(index, findClosingIndex(tag, index))).map(HTML::new).collect(Collectors.toList());
  }

  private void removeFirstAndLast(String tag, List<String> split) {
    if (!html.startsWith("<" + tag)) {
      split.remove(0);
    }
    if (!html.endsWith("</" + tag + ">")) {
      split.remove(split.size() - 1);
    }
  }

  private int findClosingIndex(String tag, int index) {
    int opened = 1;
    for (int i = index; i < html.length(); i++) {
      String s = html.substring(i);
      if (s.startsWith("<" + tag)) {
        opened++;
      }
      if (s.startsWith("</" + tag + ">")) {
        opened--;
      }
      if (opened == 0) {
        return i + 2 + tag.length();
      }
    }
    return html.length();
  }

  public List<HTML> findAllById(String tag, String id) {
    return Arrays.stream(html.split("<" + tag))
        .filter(str -> str.contains("id=\"") && (id == null || Util.between(str, "id=\"", "\"").contains(id)))
        .mapToInt(html::indexOf)
        .mapToObj(index -> html.substring(index, findClosingIndex(tag, index))).map(HTML::new).collect(Collectors.toList());
  }

  public String text() {
    if (this.html.equals("")) {
      return null;
    }

    String resultString = this.html;
    while (resultString.contains("<") && resultString.contains("</")) {
      resultString = Util.between(resultString, ">", "</");
    }
    return resultString;
  }

  public List<Attribute> getAttributes() {
    if (!this.html.contains("<") || !this.html.contains(">")) {
      return List.of();
    }

    String attrSection = Util.between(this.html, "<", ">");
    if (attrSection.equals("")) {
      return List.of();
    }

    if (!attrSection.contains(" ")) {
      return List.of();
    }
    attrSection = attrSection.substring(attrSection.indexOf(" ") + 1);
    String[] tagStrings = attrSection.split("\" ");
    if (tagStrings.length == 0) {
      if (attrSection.contains("=")) {
        var attribute = new Attribute(attrSection.split("=\"")[0], attrSection.split("=\"")[1]);
        return List.of(attribute);
      }
    }
    return Arrays.stream(attrSection.split("\" "))
        .map(str -> new Attribute(str.split("=\"")[0], str.split("=\"")[1].replace("\"", ""))).toList();
  }

  public String getAttribute(String key) {
    return getAttributes().stream().filter(attribute -> attribute.key().equalsIgnoreCase(key))
        .map(Attribute::value).findFirst().orElse(null);
  }

  @Override
  public String toString() {
    return this.html;
  }

}
