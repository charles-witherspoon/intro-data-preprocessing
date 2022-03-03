import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P1DPP {

    public static void main(String[] args) {
        // get args
        String datafilename = args[0];
        int k = Integer.parseInt(args[1]);
        int m = Integer.parseInt(args[2]);

        Data[] dataset = generateDataset(datafilename);
        if (dataset == null)
            return;

        // Task 1
        doTask1(dataset);

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

    private static void doTask1(Data[] dataset) {

        // for each gene:
        //   - get total entropy (to be used for determining information gain)
        //   - collect gx -> class tuples
        //   - sort tuples
        //   - check each split
        //     - entropy(s) = -(p log p - n log n)
        //     - IS(S1,S2) = (|S1|/|S|)Entropy(S1) + (|S2|/|S|)Entropy(S2)
        //     - Gain(v,S) = Entropy(S) - IS(S1,S2)
        //   - once best split is found:
        //     - add to discretization map

        Gene g0 = new Gene(0, dataset);
        g0.getFeatures().forEach(f -> System.out.printf("%f, %d\n", f.getValue(), f.getClassification()));
    }
}
