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

        // parse dataset
        Data[] dataset = generateDataset(datafilename);
        if (dataset == null)
            return;

        // get best splits for each gene
        List<Split> splits = getSplits(dataset);

        // get genes with best splits yielding largest gain
        List<Split> bestKGenes = splits.stream()
            .sorted(Comparator.comparingDouble(Split::getGain).reversed())
            .limit(k)
            .collect(Collectors.toList());

        // Task 1
        doTask1(bestKGenes, dataset);

        // Task 2
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

    private static void doTask1(List<Split> bestKGenes, Data[] dataset) {
        // (a)
        printAttRankEntropy(bestKGenes);

        // (b)
        Map<Integer, MapItem> discretizationMap = getDiscretizationMap(bestKGenes);
        printEntropyItemMap(discretizationMap);

        // (c)
        printItemizedDataEntropy(bestKGenes, discretizationMap, dataset);
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
        double s2 = splits.get(1).size();

        double info = (s1 / N * entropyS1) + (s2 / N * entropyS2);
        double gain = entropy - info;

        return new Split(geneId, splitVal, gain);
    }

    private static void printAttRankEntropy(List<Split> bestKGenes) {
        System.out.printf("Best %d genes:\n", bestKGenes.size());
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

    private static void printItemizedDataEntropy(List<Split> bestKGenes, Map<Integer, MapItem> dMap, Data[] dataset) {
        // for each row:
        //   for each gene column:
        //     check bounds for gene column;
        //     assign id accordingly

        // get target genes
        int[] kGenes = bestKGenes.stream()
            .sorted(Comparator.comparingInt(Split::getGeneId))
            .mapToInt(Split::getGeneId)
            .toArray();

        double[][] itemizedData = new double[dataset.length][kGenes.length];

        for (int row = 0; row < dataset.length; row++)
            for (int column = 0; column < kGenes.length; column++)
                itemizedData[row][column] = dataset[row].getGene(kGenes[column]);

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
