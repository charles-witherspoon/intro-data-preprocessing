import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gene {

    private final int id;

    private final List<Feature> features;

    public Gene(int id, Data[] dataset) {
        this.id = id + 1;
        this.features = generateFeatureList(id, dataset);
    }

    public int getId() {
        return id;
    }

    public List<Feature> getFeatures() {
        return features;
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
