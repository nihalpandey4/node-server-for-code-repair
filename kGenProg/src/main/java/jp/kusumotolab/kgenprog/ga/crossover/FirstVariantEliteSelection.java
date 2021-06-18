package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，1つ目の親を評価関数に基づいて選択するアルゴリズムを実装したクラス．<br>
 * 評価関数が最大のバリアントを選択される．<br>
 * 最大のバリアントが複数ある場合は，それらの中からランダムに選択する．<br>
 *
 * @author higo
 */
public class FirstVariantEliteSelection implements FirstVariantSelectionStrategy {

  private final Random random;

  /**
   * コンストラクタ．選択においてランダム処理を行うためのシードを引数として渡す必要あり．
   *
   * @param random ランダム処理を行うためのシード
   */
  public FirstVariantEliteSelection(final Random random) {
    this.random = random;
  }

  /**
   * 選択を行うメソッド．選択対象の個体群を引数として与える必要あり．
   *
   * @param variants 選択対象の個体群
   * @return 選択された個体
   * @see jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy#exec(List)
   */
  @Override
  public Variant exec(final List<Variant> variants) {
    final Fitness maxFitness = variants.stream()
        .max(Comparator.comparing(Variant::getFitness))
        .get()
        .getFitness();
    final List<Variant> maxFitnessVariants = variants.stream()
        .filter(v -> 0 == maxFitness.compareTo(v.getFitness()))
        .collect(Collectors.toList());
    final int index = random.nextInt(maxFitnessVariants.size());
    return maxFitnessVariants.get(index);
  }
}
