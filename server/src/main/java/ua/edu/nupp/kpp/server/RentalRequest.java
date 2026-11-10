package ua.edu.nupp.kpp.server;

public class RentalRequest {
    private Long id;
    private long clientId;
    private long equipmentId;
    private RentalRequestState state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public RentalRequest() {
    }

    public RentalRequest(Long id, long clientId, long equipmentId, RentalRequestState state, double f1, double f2, double f3, double integralIndex) {
        this.id = id;
        this.clientId = clientId;
        this.equipmentId = equipmentId;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralIndex = integralIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public RentalRequestState getState() {
        return state;
    }

    public void setState(RentalRequestState state) {
        this.state = state;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getIntegralIndex() {
        return integralIndex;
    }

    public void setIntegralIndex(double integralIndex) {
        this.integralIndex = integralIndex;
    }
}