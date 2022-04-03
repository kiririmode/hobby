package de.kiririmo.memory;

import java.io.IOException;
import java.nio.file.Path;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.StorageClass;

/**
 * 指定されたファイルから動画をAWS S3へアップロードするアプリケーション。
 */
public class App {
  private static final String BUCKET_NAME = "memories.kiririmo.de";

  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("Movie directories are missing");
      System.exit(1);
    }

    try {
      MovieUploader uploader =
          new MovieUploader(Region.AP_NORTHEAST_1, BUCKET_NAME, StorageClass.REDUCED_REDUNDANCY);
      for (String dir : args) {
        uploader.upload(Path.of(dir));
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
