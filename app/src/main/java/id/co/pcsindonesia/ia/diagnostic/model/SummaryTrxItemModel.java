package id.co.pcsindonesia.ia.diagnostic.model;

public class SummaryTrxItemModel {

    private String left;
    private String right;

    public SummaryTrxItemModel(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
