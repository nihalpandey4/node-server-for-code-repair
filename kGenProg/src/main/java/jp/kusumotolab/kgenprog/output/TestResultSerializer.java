package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResult;

/**
 * TestResultをシリアライズするクラス.<br>
 *
 * <table border="1">
 * <thead>
 * <tr>
 * <td>キー</td>
 * <td>説明</td>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td>fqn</td>
 * <td>実行したテストメソッドの完全修飾名</td>
 * </tr>
 *
 * <tr>
 * <td>isSuccess</td>
 * <td>実行したテストメソッドの結果</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class TestResultSerializer implements JsonSerializer<TestResult> {

  /**
   * シリアライズを行う.<br>
   *
   * @param testResult シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final TestResult testResult, final Type type,
      final JsonSerializationContext context) {
    final JsonObject serializedTestResult = new JsonObject();

    serializedTestResult.addProperty("fqn", testResult.executedTestFQN.toString());
    serializedTestResult.addProperty("isSuccess", !testResult.failed);

    return serializedTestResult;
  }
}
