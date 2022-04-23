package de.kiririmo.memory;

import de.kiririmo.memory.exception.MovieUploadException;
import de.kiririmo.memory.keymapper.MovieFileKeyMapper;
import de.kiririmo.memory.keymapper.Mp4CreationTimeKeyMapper;
import de.kiririmo.memory.keymapper.QuickTimeCreationTimeKeyMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.StorageClass;

/**
 * S3への動画ファイルアップローダー。
 */
public class MovieUploader {

  private static final Map<String, MovieFileKeyMapper> extMapperMap
      = Map.of("mp4", new Mp4CreationTimeKeyMapper(),
      "mov", new QuickTimeCreationTimeKeyMapper()
  );
  private final Region region;
  private final String bucketName;
  private final StorageClass storageClass;

  /**
   * コンストラクタ。
   *
   * @param region アップロード対象のS3バケットがあるリージョン。
   * @param bucketName アップロード先のS3バケット名。
   * @param storageClass アップロードするオブジェクトのストレージクラス。
   */
  public MovieUploader(Region region, String bucketName, StorageClass storageClass) {
    this.region = region;
    this.bucketName = bucketName;
    this.storageClass = storageClass;
  }

  /**
   * Pathで示されるファイルが動画ファイルか否かを返す。
   *
   * @param path 対象となるファイル
   * @return pathが示すファイルが動画ファイルであるときのみtrueを返す
   */
  public static boolean isMovie(Path path) {
    if (!path.toFile().isFile()) {
      return false;
    }

    String fileName = path.toFile().getName();
    return fileName.endsWith("MOV")
        || fileName.endsWith("mov")
        || fileName.endsWith("MP4")
        || fileName.endsWith("mp4");
  }

  /**
   * dirで表現されるディレクトリ配下にある動画ファイルをS3にアップロードする。
   *
   * @param dir 動画ファイルの格納ディレクトリ。
   * @throws IOException dirにアクセスする場合にI/Oエラーが発生した場合に送出される。
   */
  public void upload(Path dir) throws IOException {
    Stream<File> movies = Files.walk(dir)
        .filter(MovieUploader::isMovie)
        .map(Path::toFile);

    movies.forEach(f -> {
      final String fileName = f.getName();
      final String fileExt =
          fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);

      try {
        final String key = extMapperMap.get(fileExt).map(f);

        S3Client s3 = S3Client.builder()
            .region(region)
            .build();

        PutObjectRequest req = PutObjectRequest.builder()
            .bucket(bucketName)
            .storageClass(storageClass)
            .key(key)
            .build();
        s3.putObject(req, f.toPath());
      } catch (MovieUploadException e) {
        System.err.printf("[ERROR] %s: %s%n", e.getMovieFile().getPath(), e.getMessage());
      }

    });
  }
}
