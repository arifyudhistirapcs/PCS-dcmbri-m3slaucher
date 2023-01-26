package id.co.pcsindonesia.ia.diagnostic.model;

import java.util.List;

public class SummaryItemModel {

    private String title;
    private List<SummaryTrxItemModel> listSummaryTrx;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SummaryTrxItemModel> getListSummaryTrx() {
        return listSummaryTrx;
    }

    public void setListSummaryTrx(List<SummaryTrxItemModel> listSummaryTrx) {
        this.listSummaryTrx = listSummaryTrx;
    }
}
