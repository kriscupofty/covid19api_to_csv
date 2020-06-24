package assignment;

public class Entry {
    private String date;
    private String province;
    private int confirmed;
    private int deaths;
    private int recovered;
    private int active;
    private int newCases;

    public Entry(String date, String province, int confirmed, int deaths, int recovered, int active) {
        this.date = date;
        this.province = province;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.active = active;
    }

    public String getDate() {
        return date;
    }

    public String getProvince() {
        return province;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%d,%d,%d,%d", date, province, confirmed, deaths, recovered, active, newCases);
    }
}
