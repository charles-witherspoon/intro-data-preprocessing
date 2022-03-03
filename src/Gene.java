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

    private static List<Feature> generateFeatureList(int id, Data[] dataset) {
        return Arrays.stream(dataset)
            .map(data ->
                new Feature(data.getGene(id), data.getClassification()))
            .sorted(Comparator.comparingDouble(Feature::getValue))
            .collect(Collectors.toList());
    }

}
