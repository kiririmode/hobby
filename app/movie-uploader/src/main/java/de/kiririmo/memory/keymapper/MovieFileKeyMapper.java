package de.kiririmo.memory.keymapper;

import java.io.File;

/**
 * 動画ファイルから、当該ファイルを格納するためのオブジェクトストレージのキーを生成する。
 */
@FunctionalInterface
public interface MovieFileKeyMapper {
  String map(File movieFile) throws RuntimeException;
}
