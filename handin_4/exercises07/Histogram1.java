package exercises07;
// raup@itu.dk * 05/10/2022

import java.util.HashSet;

// jst@itu.dk * 23/9/2023

class Histogram1 implements Histogram {
  private int[] counts;
  private HashSet<String> myset;

  public static int countFactors(int p) {
    if (p < 2)
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
        factorCount++;
        p = p / k;
      } else
        k = k + 1;
    }
    return factorCount;
  }

  public Histogram1(int span) {
    this.counts = new int[span];
    this.myset = new HashSet<>();
  }

  @Override
  public void increment(int bin) {
    counts[bin] = counts[bin] + 1;
    myset.clear();
    String a = String.valueOf(bin);
    myset.add(a);
  }

  @Override
  public int getCount(int bin) {
    return counts[bin];
  }

  @Override
  public int getSpan() {
    return counts.length;
  }

  @Override
  public int getAndClear(int bin) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAndClear'");
  }

  public int getMyset() {
    return myset.size();
  }
}
