package id.co.pcsindonesia.ia.diagnostic.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataModel {
    /**
     * isPaid : false
     * trxNumber : PCS1284983493593
     * productAmount : 9800
     * merchantCode : 342932
     * currentVolume : 32
     * currentAmount : 316875
     * productNumber : 4
     * submerchantCode : 342932
     * productName : pertamax
     * merchantName : SPBU-34111
     * paymentType : linkaja
     */

    private boolean isPaid; //1
    private String trxNumber;
    private double productAmount;
    private String merchantCode; //4
    private double currentVolume;
    private double currentAmount;
    private double productNumber;
    private String submerchantCode; //8
    private String productName;
    private String merchantName;
    private String paymentType; //11
    /**
     * date : 2019-08-28 16:48:47
     * approvalCode : SDHE283YIS
     * maskedPAN :
     * mid : 08793334323
     * tid : 002332009
     * routing :
     * acquiring : linkaja
     */

    private String date;
    private String approvalCode;
    private String maskedPAN;
    private String mid;
    private String tid;
    private String routing;
    private String acquiring;
    private String pumpNumber;
    private String operatorName;
    private double refund;
    private String flag; //ondemand, push

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    private int dbId;

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public boolean isIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getTrxNumber() {
        return trxNumber;
    }

    public void setTrxNumber(String trxNumber) {
        this.trxNumber = trxNumber;
    }

    public double getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(double productAmount) {
        this.productAmount = productAmount;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public double getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(double currentVolume) {
        this.currentVolume = currentVolume;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(double productNumber) {
        this.productNumber = productNumber;
    }

    public String getSubmerchantCode() {
        return submerchantCode;
    }

    public void setSubmerchantCode(String submerchantCode) {
        this.submerchantCode = submerchantCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getMaskedPAN() {
        return maskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        this.maskedPAN = maskedPAN;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getAcquiring() {
        return acquiring;
    }

    public void setAcquiring(String acquiring) {
        this.acquiring = acquiring;
    }

    public String getPumpNumber() {
        return pumpNumber;
    }

    public void setPumpNumber(String pumpNumber) {
        this.pumpNumber = pumpNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public double getRefund() {
        return refund;
    }

    public void setRefund(double refund) {
        this.refund = refund;
    }

    public static DataModel convertToDataModel(String message){
        DataModel model = new DataModel();
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            JSONObject object = new JSONObject(message);
            String merchantCode = object.getString("merchantCode");
            String submerchantCode = object.getString("submerchantCode");
            String trxNo = object.getString("trxNumber");
            String productName = object.getString("productName");
            String productNumber = object.has("productNumber")?object.getString("productNumber"):"0";
            String productAmount = object.has("productAmount")?object.getString("productAmount"):"0";
            String currentVolume = object.has("currentVolume")?object.getString("currentVolume"):"0";
            String currentAmount = object.has("currentAmount")?object.getString("currentAmount"):"0";
            String paymentType = object.getString("paymentType").toUpperCase();
            String mid = object.has("mid")? object.getString("mid"):"";
            String tid = object.has("tid")? object.getString("tid"):"";
            String approvalCode = object.has("approvalCode")? object.getString("approvalCode"):"";
            String acquiring = object.has("acquiring")? object.getString("acquiring"):"";
            String routing = object.has("routing")? object.getString("routing"):"";
            String maskedPAN = object.has("maskedPAN")? object.getString("maskedPAN"):"";
            String paid = object.has("isPaid")? object.getString("isPaid"):"true";
            boolean isPaid = Boolean.parseBoolean(paid);
            String ldate = object.has("date")? object.getString("date"):sdf.format(new Date());
            object.put("date",ldate);

            String pumpNumber = object.has("pumpNumber")? object.getString("pumpNumber") : "0";
            String operator = object.has("operatorName")? object.getString("operatorName") : "-";
            String refund = object.has("refund")? object.getString("refund") : "0";
            String flag = object.has("flag")? object.getString("flag") : "ondemand";

            model.setAcquiring(acquiring);
            model.setApprovalCode(approvalCode);
            model.setCurrentAmount(Double.parseDouble(currentAmount));
            model.setProductAmount(Double.parseDouble(productAmount));
            model.setCurrentVolume(Double.parseDouble(currentVolume));
            model.setDate(ldate);
            model.setIsPaid(isPaid);
            model.setTrxNumber(trxNo);
            model.setProductName(productName);
            model.setProductNumber(Double.parseDouble(productNumber));
            model.setPaymentType(paymentType);
            model.setMid(mid);
            model.setTid(tid);
            model.setRouting(routing);
            model.setMaskedPAN(maskedPAN);
            model.setMerchantCode(merchantCode);
            model.setSubmerchantCode(submerchantCode);
            model.setOperatorName(operator);
            model.setPumpNumber(pumpNumber);
            model.setRefund(Double.parseDouble(refund));
            model.setFlag(flag);
        }catch (JSONException e){
            Log.e("GlobalHelper","convertToDataModel-Error JSON: "+e.getMessage());
        }
        return model;
    }
}

