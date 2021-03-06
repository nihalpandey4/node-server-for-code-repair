package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultVariantSelectionTest {

  private Random random;

  @Before
  public void setup() {
    this.random = new Random(0);
  }

  @Test
  public void testExec() {
    final int variantSize = 5;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> variants = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final double divider = (i % 2 == 0) ? 10 : 20;
      final double value = (double) i / divider;
      final SimpleFitness fitness = new SimpleFitness(value);
      variants.add(createVariant(fitness));
    }
    final List<Variant> selectedVariants = variantSelection.exec(Collections.emptyList(), variants);

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.05", "0.2", "0.15", "0.4", "0.25", "0.6", "0.35", "0.8", "0.45");

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(5)
        .containsExactly("0.8", "0.6", "0.45", "0.4", "0.35");
  }

  @Test
  public void testExecForEmptyVariants() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);
    final List<Variant> variants1 = Collections.emptyList();
    final List<Variant> variants2 = Collections.emptyList();
    final List<Variant> resultVariants = variantSelection.exec(variants1, variants2);
    assertThat(resultVariants).hasSize(0);
  }

  @Test
  public void testExecForNan() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);
    final List<Variant> variants = new ArrayList<>();

    final List<Variant> nanVariants = IntStream.range(0, 10)
        .mapToObj(e -> new SimpleFitness(Double.NaN))
        .map(this::createVariant)
        .collect(Collectors.toList());

    variants.addAll(nanVariants);

    final List<Variant> result1 = variantSelection.exec(Collections.emptyList(), variants);

    assertThat(result1).hasSize(10);

    final Variant normalVariant = createVariant(new SimpleFitness(0.5d));
    variants.add(normalVariant);
    final List<Variant> result2 = variantSelection.exec(Collections.emptyList(), variants);
    assertThat(result2).hasSize(10);
    assertThat(result2.get(0)).isEqualTo(normalVariant);
  }

  @Test
  public void testExecForNanCompare() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);

    final List<Variant> nanVariants = IntStream.range(0, 100)
        .mapToObj(e -> {
          if (e == 50) {
            return new SimpleFitness(SimpleFitness.MAXIMUM_VALUE);
          }
          return new SimpleFitness(Double.NaN);
        })
        .map(this::createVariant)
        .collect(Collectors.toList());

    try {
      final List<Variant> result = variantSelection.exec(Collections.emptyList(), nanVariants);
      assertThat(result).hasSize(10);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * ????????????????????????????????????????????????????????????.<br>
   * Fitness????????????????????????????????????????????????????????????????????????.<br>
   * ???Variant??????????????????id???????????????????????????????????????????????????.
   */
  @Test
  public void testOrderOfVariants() {
    final int variantSize = 5;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> current = new ArrayList<>();
    final List<Variant> generated = new ArrayList<>();

    setupLists(current, generated, 10, e -> new TestResults());

    final List<Variant> selectedVariants = variantSelection.exec(current, generated);

    assertThat(current).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.2", "0.2", "0.4", "0.4", "0.6", "0.6", "0.8", "0.8", "1.0");

    assertThat(generated).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.2", "0.2", "0.4", "0.4", "0.6", "0.6", "0.8", "0.8", "1.0");

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getId)
        .doesNotContainSequence(9L, 19L, 7L, 8L, 17L);
  }

  /**
   * BuildFailed?????????????????????????????????????????????????????????.
   */
  @Test
  public void testBuildFailed() {
    final int variantSize = 12;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> current = new ArrayList<>();
    final List<Variant> generated = new ArrayList<>();

    // 2??????1??????buildFailed??????????????????
    setupLists(current, generated, 10,
        e -> e % 2 == 0 ? new EmptyTestResults("build failed.") : new TestResults());

    final List<Variant> selectedVariants = variantSelection.exec(current, generated);

    // 12?????????????????????buildSuccess???10?????????????????????10???????????????????????????
    assertThat(selectedVariants).hasSize(10)
        .allMatch(Variant::isBuildSucceeded);
  }

  /**
   * Variant???????????????????????????.Fitness?????????????????????.
   *
   * @param fitness Variant?????????Fitness
   * @return variant ????????????Variant
   */
  private Variant createVariant(final Fitness fitness) {
    final TestResults testResults = new TestResults();
    return createVariant(fitness, 0, testResults);
  }

  /**
   * Variant???????????????????????????.Fitness, Id, TestResults???????????????.
   *
   * @param fitness Variant?????????Fitness
   * @param id Variant???????????????
   * @param testResults Variant??????????????????
   * @return variant ????????????Variant
   */
  private Variant createVariant(final Fitness fitness, final int id,
      final TestResults testResults) {
    return new Variant(id, 0, null, null, testResults, fitness, null, null);
  }

  /**
   * ???????????????????????????????????????????????????.
   *
   * @param current variantSelection?????????current?????????
   * @param generated variantSelection?????????generated?????????
   * @param num ????????????????????????????????????
   * @param testResultsCreator testResults???????????????????????????Function?????????????????????int???????????????TestResults????????????
   */
  private void setupLists(final List<Variant> current, final List<Variant> generated, final int num,
      final Function<Integer, TestResults> testResultsCreator) {
    for (int i = 0; i < num; i++) {
      final double value = (1.0d * (i + (i % 2))) / (double) num;
      final SimpleFitness fitness = new SimpleFitness(value);
      current.add(createVariant(fitness, i, testResultsCreator.apply(i)));
      generated.add(createVariant(fitness, i + num, testResultsCreator.apply(i)));
    }
  }
}
