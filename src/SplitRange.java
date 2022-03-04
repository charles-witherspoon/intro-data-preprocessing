public class SplitRange {

    private final double lBound;

    private final double rBound;

    public SplitRange(double lBound, double rBound) {
        this.lBound = lBound;
        this.rBound = rBound;
    }

    @Override
    public String toString() {
        String l = (lBound == Double.NEGATIVE_INFINITY ? "(-" : String.format("[%8.7e", lBound));
        String r = (rBound == Double.POSITIVE_INFINITY ? "+" : String.format("%8.7e", rBound)) + ")";
        return l + "," + r;
    }
}
