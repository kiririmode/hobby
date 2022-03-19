package de.kiririmo.memory;

import de.kiririmo.memory.exception.MovieUploadException;
import de.kiririmo.memory.keymapper.MP4CreationTimeKeyMapper;
import de.kiririmo.memory.keymapper.MovieFileKeyMapper;
import de.kiririmo.memory.keymapper.QuickTimeCreationTimeKeyMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.StorageClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class MovieUploader {

    private Region region;
    private String bucketName;
    private StorageClass storageClass;
    private MovieFileKeyMapper movieFileKeyMapper;

    private static final Map<String, MovieFileKeyMapper> extMapperMap
            = Map.of("mp4", new MP4CreationTimeKeyMapper(),
            "mov", new QuickTimeCreationTimeKeyMapper()
             );

    public MovieUploader(Region region, String bucketName, StorageClass storageClass, MovieFileKeyMapper mapper) {
        this.region = region;
        this.bucketName = bucketName;
        this.storageClass = storageClass;
        this.movieFileKeyMapper = mapper;
    }

    public MovieUploader(Region region, String bucketName, StorageClass storageClass){
        this(region, bucketName, storageClass, new MP4CreationTimeKeyMapper());
    }

    public void upload(Path dir) throws IOException {
        Stream<File> movies = Files.walk(dir)
                .filter(p -> isMovie(p))
                .map(p -> p.toFile());

        movies.forEach(f -> {
            final String fileName = f.getName();
            final String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase(Locale.ROOT);

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
                System.err.println(String.format("[ERROR] %s: %s", e.getMovieFile().getPath(), e.getMessage()));
            }

        });
    }

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
}
