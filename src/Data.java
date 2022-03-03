import java.util.Arrays;

public class Data {

    private final int classification;

    private final double[] genes;

    public Data(String classification, String[] genes) {
        this.classification = "positive".equalsIgnoreCase(classification) ? 1 : 0;
        this.genes = Arrays.stream(genes).mapToDouble(Double::parseDouble).toArray();
    }

    public int getClassification() {
        return classification;
    }

    public double getGene(int i) {
        return genes[i];
    }

    public double[] getGenes() {
        return genes;
    }
}
