package de.kiririmo.memory.keymapper;

import java.io.File;

@FunctionalInterface
public interface MovieFileKeyMapper {
    String map(File movieFile) throws RuntimeException;
}
