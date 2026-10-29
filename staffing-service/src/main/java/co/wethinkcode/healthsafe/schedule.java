package co.wethinkcode.healthsafe;

public class schedule {
    private String[] doctors;
    private int alertLevel;
    private String department;
    private String wardId;

    public schedule(String[] doctors, int alertLevel, String department, String wardId) {
        this.alertLevel = alertLevel;
        this.department = department;
        this.wardId = wardId;
        this.doctors = doctors;
    }

    public schedule(){}

    public String[] getDoctors() {
        return doctors;
    }

    public void setDoctors(String[] doctors) {
        this.doctors = doctors;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }
}
