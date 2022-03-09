public class Bin {

    private final Gene gene;

    private final SplitRange range;

    public Bin(Gene gene, SplitRange range) {
        this.gene = gene;
        this.range = range;
    }

    public Gene getGene() {
        return gene;
    }

    public SplitRange getRange() {
        return range;
    }

}
