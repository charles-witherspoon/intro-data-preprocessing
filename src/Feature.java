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

}
