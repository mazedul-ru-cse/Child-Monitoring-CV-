package sms;

public class SmsHistoryModel {

    String smsId;
    String personName;
    String smsDate;
    String smsType;
    String smsBody;

    public SmsHistoryModel(String smsId, String personName, String smsDate, String smsType, String smsBody) {
        this.smsId = smsId;
        this.personName = personName;
        this.smsDate = smsDate;
        this.smsType = smsType;
        this.smsBody = smsBody;
    }

    public String getSmsId() {
        return smsId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getSmsDate() {
        return smsDate;
    }

    public String getSmsType() {
        return smsType;
    }

    public String getSmsBody() {
        return smsBody;
    }

}
