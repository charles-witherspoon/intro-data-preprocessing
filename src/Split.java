public class Split {

    private final Gene gene;

    private final double value;

    private final double gain;

    private final SplitRange lSplit;

    private final SplitRange rSplit;

    public Split(Gene gene, double value, double gain) {
        this.gene = gene;
        this.value = value;
        this.gain = gain;
        this.lSplit = new SplitRange(Double.NEGATIVE_INFINITY, value);
        this.rSplit = new SplitRange(value, Double.POSITIVE_INFINITY);
    }

    public Gene getGene() {
        return gene;
    }

    public double getValue() {
        return value;
    }

    public double getGain() {
        return gain;
    }

    public SplitRange getlSplit() {
        return lSplit;
    }

    public SplitRange getrSplit() {
        return rSplit;
    }

    public int getItemIdForValue(double value) {
        if (lSplit.contains(value))
            return lSplit.getItemId();

        return rSplit.getItemId();
    }

    public static Split getBetterSplit(Split a, Split b) {
        if (a == null)
            return b;

        if (b == null)
            return a;

        return a.gain > b.gain ? a : b;
    }
}
