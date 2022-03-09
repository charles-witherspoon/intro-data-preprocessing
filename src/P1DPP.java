import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        List<Split> bestKGenes = getBestKGenes(k, splits);

        // Task 1
        doTask1(bestKGenes);

        // Task 2
        doTask2(bestKGenes, m);
    }

    private static List<Split> getBestKGenes(int k, List<Split> geneSplits) {
        List<Split> bestKGenes = geneSplits.stream()
            .sorted(Comparator.comparingDouble(Split::getGain).reversed())
            .limit(k)
            .collect(Collectors.toList());

        // Update split ranges with ids
        int itemId = 0;
        for (Split split : bestKGenes) {
            split.getlSplit().setItemId(itemId++);
            split.getrSplit().setItemId(itemId++);
        }

        return bestKGenes;
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

    private static void doTask1(List<Split> bestKGenes) {
        // (a)
        printAttRankEntropy(bestKGenes);

        // (b)
        printEntropyItemMap(bestKGenes);

        // (c)
        printItemizedDataEntropy(bestKGenes);
    }


    private static List<Split> getSplits(Data[] dataset) {
        List<Split> splits = new ArrayList<>();

        // get total number of features
        int geneCount = dataset[0].getGenes().length;

        for (int i = 0; i < geneCount; i++) {
            Gene g = new Gene(i, dataset);
            List<Feature> sortedFeatures = g.getFeatures().stream()
                .sorted(Comparator.comparingDouble(Feature::getValue))
                .collect(Collectors.toList());

            Split bestSplit = null;
            double entropy = getEntropy(sortedFeatures);

            for (int j = 0; j < sortedFeatures.size() - 1; j++) {
                double splitVal = (sortedFeatures.get(j).getValue() + sortedFeatures.get(j + 1).getValue()) / 2.0;
                Split split = getInfoFromSplit(splitVal, sortedFeatures, entropy, g);
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

    private static Split getInfoFromSplit(double splitVal, List<Feature> features, double entropy, Gene gene) {
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

        return new Split(gene, splitVal, gain);
    }

    private static void printAttRankEntropy(List<Split> bestKGenes) {
        System.out.printf("Best %d genes:\n", bestKGenes.size());
        System.out.println("gene,split,gain");
        for (Split split : bestKGenes)
            System.out.printf("%d,%8.7e,%8.7e\n",
                split.getGene().getId(), split.getValue(), split.getGain());

        System.out.println("\n");
    }

    private static void printEntropyItemMap(List<Split> bestKGenes) {
        System.out.println("gene,range,identifier");

        List<SimpleEntry<Integer, SplitRange>> splitRanges = new ArrayList<>();
        for (Split split: bestKGenes) {
            splitRanges.add(new SimpleEntry<>(split.getGene().getId(), split.getlSplit()));
            splitRanges.add(new SimpleEntry<>(split.getGene().getId(), split.getrSplit()));
        }

        splitRanges.stream()
            .sorted(Comparator.comparingInt(sr -> sr.getValue().getItemId()))
            .forEach(sr -> System.out.printf("g%d,%s\n", sr.getKey(), sr.getValue()));

        System.out.println("\n");
    }

    private static void printItemizedDataEntropy(List<Split> bestKGenes) {
        int[][] itemizedData = bestKGenes.stream()
            .map(split -> split.getGene().getFeatures()
                .stream()
                .mapToInt(feature -> split.getItemIdForValue(feature.getValue()))
                .toArray())
            .toArray(int[][]::new);

        // print values
        String header = bestKGenes.stream()
            .map(split -> String.format("g%d", split.getGene().getId()))
            .collect(Collectors.joining(","));
        System.out.println(header);

        for (int row = 0; row < itemizedData[0].length; row++) {
            System.out.println(getItemizedDataRow(itemizedData, row));
        }
    }

    private static String getItemizedDataRow(int[][] itemizedData, int row) {
        return Arrays.stream(itemizedData)
            .map(datum -> String.valueOf(datum[row]))
            .collect(Collectors.joining(","));
    }

    private static void doTask2(List<Split> bestKGenes, int m) {

        List<Gene> kGenes = bestKGenes.stream()
            .map(Split::getGene)
            .collect(Collectors.toList());

        // (a)
        printEquidensityItemMap(bestKGenes, m);

        // (b)
        printItemizedDataEquidensity();
    }

    private static void printEquidensityItemMap(List<Split> bestKGenes, int m) {

    }

    private static void printItemizedDataEquidensity() {

    }
}
