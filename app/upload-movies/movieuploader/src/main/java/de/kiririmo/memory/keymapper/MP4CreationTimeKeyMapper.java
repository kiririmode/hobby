package de.kiririmo.memory.keymapper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;
import de.kiririmo.memory.exception.MovieUploadException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class MP4CreationTimeKeyMapper implements MovieFileKeyMapper {

    @Override
    public String map(File movieFile) throws RuntimeException {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(movieFile);
            Collection<Mp4Directory> directories = metadata.getDirectoriesOfType(Mp4Directory.class);
            Date creationDate = directories.stream()
                    .map(d -> d.getDate(Mp4Directory.TAG_CREATION_TIME))
                    .filter(d -> d != null)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException(String.format("Error occurred in parsing %s metadata", movieFile.getPath())));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            return String.format("%s/%s", formatter.format(creationDate), movieFile.getName());
        } catch (ImageProcessingException | IOException e) {
            throw new MovieUploadException("Failed to generate Object Key for S3", movieFile, e);
        }
    }
}