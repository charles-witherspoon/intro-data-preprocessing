import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Gene {

    private final int id;

    private final List<Feature> features;

    public Gene(int id, Data[] dataset) {
        this.id = id;
        this.features = generateFeatureList(id, dataset);
    }

    public int getId() {
        return id;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public List<SplitRange> getMBins(int m) {

        List<Feature> sortedFeatures = features.stream()
            .sorted(Comparator.comparingDouble(Feature::getValue))
            .collect(Collectors.toList());

        double min = Double.NEGATIVE_INFINITY;
        double max;

        List<SplitRange> bins = new ArrayList<>();
        int i = 0;
        int binSize = sortedFeatures.size() / m;
        int extras = sortedFeatures.size() % m;
        while (i < sortedFeatures.size()) {
            int leftIndex = i + binSize - 1 + (extras == 0 ? extras : extras--);
            int rightIndex = leftIndex + 1;

            if (rightIndex >= sortedFeatures.size())
                max = Double.POSITIVE_INFINITY;
            else
                max = (sortedFeatures.get(leftIndex).getValue() + sortedFeatures.get(rightIndex).getValue()) / 2.0;

            bins.add(new SplitRange(min, max));
            min = max;
            i = leftIndex;
        }

        return bins;
    }

    private static List<Feature> generateFeatureList(int id, Data[] dataset) {
        return Arrays.stream(dataset)
            .map(data ->
                new Feature(data.getGene(id), data.getClassification()))
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Gene))
            return false;

        Gene that = (Gene) o;

        return this.id == that.id && this.features == that.features;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + id;
        result = 31 * result + features.hashCode();

        return result;
    }
}
