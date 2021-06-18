package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestResultsTest {

  private final static long TIMEOUT_SEC = 60;

  /**
   * FLで用いる4メトリクスのテスト
   */
  @Test
  public void testFLMetricsInTestResultsForExample02() {
    // actual確保のためにテストの実行
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(generatedSourceCode);
    final TestResults result = executor.exec(variant);

    // expected確保の作業
    // まずast生成
    final ProductSourcePath fooPath = new ProductSourcePath(rootPath, ExampleAlias.Src.FOO);
    final GeneratedJDTAST<ProductSourcePath> fooAst =
        (GeneratedJDTAST<ProductSourcePath>) generatedSourceCode.getProductAst(fooPath);
    final ASTLocations fooAstLocations = fooAst.createLocations();

    // astから5行目 (n--;) のlocationを取り出す
    final List<ASTLocation> locations1 = fooAstLocations.infer(5);
    final ASTLocation loc1 = locations1.get(locations1.size() - 1);
    final JDTASTLocation jdtLocation1 = (JDTASTLocation) loc1;

    // 一応locationの中身を確認しておく
    assertThat(jdtLocation1.node).isSameSourceCodeAs("n--;");

    // 4メトリクスの取り出しとassertion
    final long a_ep1 = result.getNumberOfPassedTestsExecutingTheStatement(fooPath, loc1);
    final long a_ef1 = result.getNumberOfFailedTestsExecutingTheStatement(fooPath, loc1);
    final long a_np1 = result.getNumberOfPassedTestsNotExecutingTheStatement(fooPath, loc1);
    final long a_nf1 = result.getNumberOfFailedTestsNotExecutingTheStatement(fooPath, loc1);

    assertThat(a_ep1).isSameAs(2L); // test01, test02
    assertThat(a_ef1).isSameAs(0L);
    assertThat(a_np1).isSameAs(1L); // test04
    assertThat(a_nf1).isSameAs(1L); // test03

    // astから10行目 (return n;) のlocationを取り出す
    final List<ASTLocation> locations2 = fooAstLocations.infer(10);
    final ASTLocation loc2 = locations2.get(locations2.size() - 1);
    final JDTASTLocation jdtLocation2 = (JDTASTLocation) loc2;

    // 一応locationの中身を確認しておく
    assertThat(jdtLocation2.node).isSameSourceCodeAs("return n;");

    // 4メトリクスの取り出しとassertion
    final long a_ep2 = result.getNumberOfPassedTestsExecutingTheStatement(fooPath, loc2);
    final long a_ef2 = result.getNumberOfFailedTestsExecutingTheStatement(fooPath, loc2);
    final long a_np2 = result.getNumberOfPassedTestsNotExecutingTheStatement(fooPath, loc2);
    final long a_nf2 = result.getNumberOfFailedTestsNotExecutingTheStatement(fooPath, loc2);

    assertThat(a_ep2).isSameAs(3L); // test01, test02, test04
    assertThat(a_ef2).isSameAs(1L); // test03
    assertThat(a_np2).isSameAs(0L);
    assertThat(a_nf2).isSameAs(0L);
  }

  /**
   * toString()のテスト．JSON形式の確認
   */
  @Test
  public void testToString() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(generatedSourceCode);
    final TestResults result = executor.exec(variant);

    final String expected = new StringBuilder()//
        .append("[")
        .append("  {")
        .append("    \"executedTestFQN\": \"example.FooTest.test04\",")
        .append("    \"wasFailed\": false,")
        .append("    \"coverages\": [")
        .append(
            "      {\"executedTargetFQN\": \"example.Foo\", \"coverages\": [0, 2, 0, 2, 1, 0, 0, 2, 0, 2]}")
        .append("    ]")
        .append("  },")
        .append("  {")
        .append("    \"executedTestFQN\": \"example.FooTest.test01\",")
        .append("    \"wasFailed\": false,")
        .append("    \"coverages\": [")
        .append(
            "      {\"executedTargetFQN\": \"example.Foo\", \"coverages\": [0, 2, 0, 2, 2, 0, 0, 1, 0, 2]}")
        .append("    ]")
        .append("  },")
        .append("  {")
        .append("    \"executedTestFQN\": \"example.FooTest.test03\",")
        .append("    \"wasFailed\": true,")
        .append("    \"coverages\": [")
        .append(
            "      {\"executedTargetFQN\": \"example.Foo\", \"coverages\": [0, 2, 0, 2, 1, 0, 0, 2, 0, 2]}")
        .append("    ]")
        .append("  },")
        .append("  {")
        .append("    \"executedTestFQN\": \"example.FooTest.test02\",")
        .append("    \"wasFailed\": false,")
        .append("    \"coverages\": [")
        .append(
            "      {\"executedTargetFQN\": \"example.Foo\", \"coverages\": [0, 2, 0, 2, 2, 0, 0, 1, 0, 2]}")
        .append("    ]")
        .append("  }")
        .append("]")
        .toString();

    assertThat(result.toString()).isEqualToIgnoringNewLines(expected);
  }

}
