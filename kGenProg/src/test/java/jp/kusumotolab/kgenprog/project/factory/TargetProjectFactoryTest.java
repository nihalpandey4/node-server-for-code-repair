package jp.kusumotolab.kgenprog.project.factory;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAZ_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Lib;

public class TargetProjectFactoryTest {

  private final static ClassPath JUNIT = new ClassPath(Lib.JUNIT);

  @Test
  public void testCreateByBasePath01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTest = new TestSourcePath(rootPath, FOO_TEST);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
  }

  @Test
  public void testCreateByBasePath02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTest = new TestSourcePath(rootPath, FOO_TEST);
    final ProductSourcePath bar = new ProductSourcePath(rootPath, BAR);
    final TestSourcePath barTest = new TestSourcePath(rootPath, BAR_TEST);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
  }

  @Test
  public void testCreateByBasePath03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTest = new TestSourcePath(rootPath, FOO_TEST);
    final ProductSourcePath bar = new ProductSourcePath(rootPath, BAR);
    final TestSourcePath barTest = new TestSourcePath(rootPath, BAR_TEST);
    final ProductSourcePath baz = new ProductSourcePath(rootPath, BAZ);
    final TestSourcePath bazTest = new TestSourcePath(rootPath, BAZ_TEST);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar, baz);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest, bazTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
  }

  @Test
  public void testCreateByCompletelySpecified01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");

    final List<Path> fooPath = Arrays.asList(rootPath.resolve(FOO));
    final List<Path> fooTestPath = Arrays.asList(rootPath.resolve(FOO_TEST));

    // ???????????????????????????????????????
    final TargetProject project = TargetProjectFactory.create(rootPath, fooPath, fooTestPath,
        Collections.emptyList(), JUnitVersion.JUNIT4);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths())
        .containsExactlyInAnyOrder(new ProductSourcePath(rootPath, FOO));
    assertThat(project.getTestSourcePaths())
        .containsExactlyInAnyOrder(new TestSourcePath(rootPath, FOO_TEST));
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFailure01() {
    final Path rootPath = Paths.get("example/NonExistentProject");

    // Exception to be thrown
    TargetProjectFactory.create(rootPath);
  }

  @Test
  public void testCreateByBuildConfig01() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final List<Path> fooPath = Arrays.asList(rootPath.resolve(FOO));
    final List<Path> fooTestPath = Arrays.asList(rootPath.resolve(FOO_TEST));

    // runtime exception??????????????????system.err????????????????????????
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {

      @Override
      public void write(final int b) {
      }
    }));

    // ?????????????????????build.xml?????????
    // ????????????????????????????????????????????????
    final Path configPath = rootPath.resolve("build.xml");
    try {
      Files.createFile(configPath);
    } catch (final IOException e) {
      if (!Files.exists(configPath)) {
        // ????????????????????????????????????
        throw e;
      }
    }

    final TargetProject project = TargetProjectFactory.create(rootPath, fooPath, fooTestPath,
        Collections.emptyList(), JUnitVersion.JUNIT4);

    final Path actualBuildConfigPath = Paths.get("example/BuildSuccess01/build.xml");
    final Path projectBuildConfigPath = TargetProjectFactory.getBuildConfigPaths(rootPath)
        .get(0);

    // ??????????????????????????????
    Files.deleteIfExists(configPath);
    System.setErr(ps);

    // TODO ???????????????buildConfigPath????????????????????????????????????TargetProject??????????????????????????????assert????????????
    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
    assertThat(projectBuildConfigPath).isEqualTo(actualBuildConfigPath);
  }

  @Test
  public void testFactorialBehavior01() throws IOException {
    // Factory??????????????????????????????????????????????????????
    // ????????????????????????????????????????????????

    final Path rootPath = Paths.get("example/BuildSuccess01");

    // runtime exception??????????????????system.err????????????????????????
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {

      @Override
      public void write(final int b) {
      } // ???????????????writer
    }));

    // ?????????????????????build.xml?????????
    final Path configPath = rootPath.resolve("build.xml");
    try {
      Files.createFile(configPath);
    } catch (final IOException e) {
      if (!Files.exists(configPath)) {
        // ??????????????????????????????????????????????????????????????????throw
        throw e;
      }
    }

    // Factory.create????????????
    final TargetProject project = TargetProjectFactory.create(rootPath);

    // ????????????????????????????????????assert???????????????????????????
    Files.deleteIfExists(configPath);
    System.setErr(ps);

    // ????????????build.xml????????????????????????AntProjectBuilder???????????????null???TargetProject????????????????????????
    assertThat(project).isNull();

  }

  @Test
  public void testCreateBySpecifyingPathsForProductAndTest() {
    final Path rootPath = Paths.get("example/BuildSuccess07");
    final List<Path> srcPaths = Arrays.asList(rootPath.resolve("src"));
    final List<Path> testPaths = Arrays.asList(rootPath.resolve("test"));
    final TargetProject project = TargetProjectFactory.create(rootPath, srcPaths, testPaths,
        Collections.emptyList(), JUnitVersion.JUNIT4);

    // *Test????????????????????? "src" ??? "test" ???????????????
    final String fooTestStr = FOO_TEST.toString()
        .replace("src", "test");
    final String barTestStr = BAR_TEST.toString()
        .replace("src", "test");

    final ProductSourcePath foo = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTest = new TestSourcePath(rootPath, Paths.get(fooTestStr));
    final ProductSourcePath bar = new ProductSourcePath(rootPath, BAR);
    final TestSourcePath barTest = new TestSourcePath(rootPath, Paths.get(barTestStr));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT);
  }
}
