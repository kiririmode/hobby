package de.kiririmo.memory.keymapper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeDirectory;
import de.kiririmo.memory.exception.MovieParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

public class QuickTimeCreationTimeKeyMapper implements MovieFileKeyMapper {

    protected static final Date ZERO_VALUE_DATE;
    static {
        ZonedDateTime utcDateTime = ZonedDateTime.of(1904,1,1,0,0,0,0, ZoneId.of("UTC"));
        ZERO_VALUE_DATE = Date.from(utcDateTime.toInstant());
    }

    protected Logger logger = LoggerFactory.getLogger(QuickTimeCreationTimeKeyMapper.class);

    protected boolean hasValidDate(final Date date) {
        if (date == null) {
            return false;
        }

        // QuickTime仕様におけるCreation Timeは、1904/01/01 (UTC)からの経過秒を32-bit integerで表現するため、
        // Creation Timeが格納されていない場合は、1904/01/01 00:00:00(UTC)が返却される
        if(ZERO_VALUE_DATE.equals(date)) {
            return false;
        }
        return true;
    }

    @Override
    public String map(File movieFile) {
        Metadata metadata = null;
        try {
            // 録画日時の抽出
            metadata = ImageMetadataReader.readMetadata(movieFile);
            Collection<QuickTimeDirectory> directories = metadata.getDirectoriesOfType(QuickTimeDirectory.class);
            Date creationDate = directories.stream()
                    .map(d -> d.getDate(QuickTimeDirectory.TAG_CREATION_TIME))
                    .filter(d -> hasValidDate(d))
                    .findAny()
                    .orElseThrow(() -> new MovieParseException(
                            String.format("Error occurred in parsing [%s]'s creation date", movieFile.getPath()),
                            movieFile));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            final String date = formatter.format(creationDate);
            logger.debug(String.format("file [%s] has date [%s], %s", movieFile.getPath(), date, creationDate));

            return String.format("%s/%s", date, movieFile.getName());
        } catch (ImageProcessingException | IOException e) {
            throw new MovieParseException("Failed to generate Object Key for S3", movieFile, e);
        }
    }
}
