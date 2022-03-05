import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class P1DPP {

    public static void main(String[] args) {
        // get args
        String datafilename = args[0];
        int k = Integer.parseInt(args[1]);
        int m = Integer.parseInt(args[2]);

        Data[] dataset = generateDataset(datafilename);
        if (dataset == null)
            return;

//        listPossibleTotalEntropies();
        // Task 1
        doTask1(dataset, k);

        // Task 2
    }
    private static void listPossibleTotalEntropies() {
        double N = 62.0;

        for (int i = 0; i <= 62; i++) {
            double pNum = i;
            double p = pNum / N;
            double n = (N - pNum) / N;

            double entropy = -1.0 * ((p * log2(p)) + (n * log2(n)));
            System.out.printf("p=%d/62\tn=%d/62\tentropy=%7.17f\n", i, (int) (N - i), entropy);
        }
    }

    private static Data[] generateDataset(String datafilename) {
        try (BufferedReader csvReader = new BufferedReader(new FileReader(datafilename))) {

            List<Data> dataset = new ArrayList<>();

            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");

                String classification = data[data.length - 1];
                String[] genes = Arrays.copyOfRange(data, 0, data.length - 1);

                dataset.add(new Data(classification, genes));
            }

            return dataset.toArray(new Data[0]);

        } catch (Exception e) {
            System.out.printf("Unable to read csv file due to error: %s\n", e);
            return null;
        }
    }

    private static void doTask1(Data[] dataset, int k) {

        List<Split> splits = getSplits(dataset);

        // order splits by gain
        List<Split> bestKGenes = splits.stream()
            .sorted(Comparator.comparingDouble(Split::getGain).reversed())
            .limit(k)
            .collect(Collectors.toList());

        // (a)
        printAttRankEntropy(bestKGenes, k);

        // (b)
        Map<Integer, MapItem> discretizationMap = getDiscretizationMap(bestKGenes);
        printEntropyItemMap(discretizationMap);

        // (c)
        printItemizedDataEntropy();
    }

    private static List<Split> getSplits(Data[] dataset) {
        List<Split> splits = new ArrayList<>();
        int geneCount = dataset[0].getGenes().length;
        for (int i = 0; i < geneCount; i++) {
            Gene g = new Gene(i, dataset);
            List<Feature> features = g.getFeatures();

            Split bestSplit = null;
            double entropy = getEntropy(features);

            for (int j = 0; j < features.size() - 1; j++) {
                double splitVal = (features.get(j).getValue() + features.get(j + 1).getValue()) / 2.0;
                Split split = getInfoFromSplit(splitVal, features, entropy, i);
                bestSplit = Split.getBetterSplit(split, bestSplit);
            }

            // Add split to split list
            splits.add(bestSplit);
        }

        return splits;
    }

    private static double getEntropy(List<Feature> features) {
        double N = features.size();
        double positiveCount = (double) features.stream().filter(f -> f.getClassification() == 1).count();
        double p = positiveCount / N;
        double n = (N - positiveCount) / N;

        return -1.0 * ((p * log2(p)) + (n * log2(n)));
    }

    private static double log2(double a) {
        if (a == 0.0)
            return 0;

        // logb a = logd a / logd b
        // -> log2 a = loge a / loge 2
        return Math.log(a) / Math.log(2.0);
    }

    private static Split getInfoFromSplit(double splitVal, List<Feature> features, double entropy, int geneId) {
        List<List<Feature>> splits = new ArrayList<>(features.stream()
            .collect(Collectors.partitioningBy(s -> s.getValue() < splitVal))
            .values());

        double entropyS1 = getEntropy(splits.get(0));
        double entropyS2 = getEntropy(splits.get(1));

        double N = features.size();
        double s1 = splits.get(0).size();
        double s2 = splits.get(0).size();

        double info = (s1 / N * entropyS1) + (s2 / N * entropyS2);
        double gain = entropy - info;

        return new Split(geneId, splitVal, gain);
    }

    private static void printAttRankEntropy(List<Split> bestKGenes, int k) {
        System.out.printf("Best %d genes:\n", k);
        System.out.println("gene,split,gain");
        for (Split split : bestKGenes)
            System.out.printf("%d,%8.7e,%8.7e\n",
                split.getGeneId(), split.getValue(), split.getGain());

        System.out.println("\n");
    }

    private static void printEntropyItemMap(Map<Integer, MapItem> dMap) {
        System.out.println("gene,range,identifier");

        dMap.forEach((id, item) ->
                System.out.printf("g%d,%s,%d\n",
                        item.geneId, item.range, id));

        System.out.println("\n");
    }

    private static void printItemizedDataEntropy() {

    }

    private static Map<Integer, MapItem> getDiscretizationMap(List<Split> bestKGenes) {
        Map<Integer, MapItem> dMap = new HashMap<>();
        int id = 0;

        for (Split split : bestKGenes) {
           dMap.put(id++, new MapItem(split.getGeneId(), split.getlSplit()));
           dMap.put(id++, new MapItem(split.getGeneId(), split.getrSplit()));
        }

        return dMap;
    }

    private static class MapItem {
        private final int geneId;
        private final SplitRange range;

        public MapItem(int geneId, SplitRange range) {
            this.geneId = geneId;
            this.range = range;
        }
    }
}
