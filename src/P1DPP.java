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
        for (int i = 0; i < dataset.length; i++) {
            Data data = dataset[i];
            System.out.printf("index: %s\t genes:%d\t class:%s\n", i, data.getGenes().length, data.getClassification());
        }

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
}
