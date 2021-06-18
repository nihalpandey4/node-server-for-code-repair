package jp.kusumotolab.kgenprog.ga.selection;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * Fitnessが高い順に個体を選択する.Fitnessが同値の場合は古い個体を優先的に残す.
 */

public class EliteAndOldVariantSelection implements VariantSelection {

  private final int maxVariantsPerGeneration;

  public EliteAndOldVariantSelection(final int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  /**
   * Variantの算出を行う.結果はVariantのリストとして返す.
   *
   * @param current 現在の世代に引き継がれた個体群
   * @param generated 現在の世代で新しく生成された個体群
   * @return list Variantの算出結果
   */
  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    return Stream.concat(current.stream(), generated.stream())
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
  }
}
