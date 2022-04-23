package de.kiririmo.memory.exception;

import java.io.File;

/**
 * 動画ファイル解析時の例外。
 */
public class MovieParseException extends RuntimeException {
  private final File movieFile;

  public MovieParseException(String message, File movieFile) {
    super(message);
    this.movieFile = movieFile;
  }

  public MovieParseException(String message, File movieFile, Throwable cause) {
    super(message, cause);
    this.movieFile = movieFile;
  }

  public File getMovieFile() {
    return movieFile;
  }
}
