package de.kiririmo.memory.keymapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.kiririmo.memory.exception.MovieParseException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class QuickTimeCreationTimeKeyMapperTest {

  @Test
  void map＿撮影日付が抽出できる() {
    QuickTimeCreationTimeKeyMapper sut = new QuickTimeCreationTimeKeyMapper();
    String withDate = sut.map(Path.of("src/test/resources/with_date.mov").toFile());
    assertEquals("2021/03/06/with_date.mov", withDate, "撮影日付が抽出できる");
  }

  @Test
  void map_撮影日付が抽出できない() {
    QuickTimeCreationTimeKeyMapper sut = new QuickTimeCreationTimeKeyMapper();

    assertThrows(MovieParseException.class,
        () -> sut.map(Path.of("src/test/resources/without_date.mov").toFile()),
        "撮影日付を保持していないファイルからは例外が返却される");
  }
}