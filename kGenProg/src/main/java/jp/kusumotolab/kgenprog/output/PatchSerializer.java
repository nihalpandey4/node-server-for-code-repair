package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Patchをシリアライズするクラス.<br>
 * シリアライズされた{@link FileDiff}の配列にシリアライズする.
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class PatchSerializer implements JsonSerializer<Patch> {

  /**
   * シリアライズを行う.<br>
   *
   * @param patch シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Patch patch, final Type type,
      final JsonSerializationContext context) {

    return context.serialize(patch.getFileDiffs());
  }
}
