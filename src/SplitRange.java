public class SplitRange {

    private int itemId;

    private final double lBound;

    private final double rBound;

    public SplitRange(double lBound, double rBound) {
        this.lBound = lBound;
        this.rBound = rBound;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean contains(double value) {
        // value must be less than right bound
        if (value >= rBound)
            return false;

        // if lbound -inf, value must be greater than lbound
        if (lBound == Double.NEGATIVE_INFINITY)
            return value > lBound;

        // if lbound !-inf, value must be greater than or equal to lbound
        return value >= lBound;
    }

    @Override
    public String toString() {
        String l = (lBound == Double.NEGATIVE_INFINITY ? "(-" : String.format("[%8.7e", lBound));
        String r = (rBound == Double.POSITIVE_INFINITY ? "+" : String.format("%8.7e", rBound)) + ")";
        return l + "," + r + "," + itemId;
    }
}
