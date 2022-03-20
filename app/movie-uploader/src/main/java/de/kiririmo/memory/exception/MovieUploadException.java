package de.kiririmo.memory.exception;

import java.io.File;

/**
 * 動画ファイルをアップロードする際に発生する例外。
 */
public class MovieUploadException extends RuntimeException {
  private final File movieFile;

  public MovieUploadException(String message, File movieFile) {
    super(message);
    this.movieFile = movieFile;
  }

  public MovieUploadException(String message, File movieFile, Throwable cause) {
    super(message, cause);
    this.movieFile = movieFile;
  }

  public File getMovieFile() {
    return movieFile;
  }

}
