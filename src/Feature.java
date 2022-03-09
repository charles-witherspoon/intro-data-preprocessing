public class Feature {

    private final double value;

    private final int classification;


    public Feature(double value, int  classification) {
        this.value = value;
        this.classification = classification;
    }

    public double getValue() {
        return value;
    }

    public int getClassification() {
        return classification;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Feature))
            return false;

        Feature that = (Feature) o;

        return this.value == that.value && this.classification == that.classification;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + ((int) Double.doubleToLongBits(value));
        result = 31 * result + classification;


        return result;
    }
}
