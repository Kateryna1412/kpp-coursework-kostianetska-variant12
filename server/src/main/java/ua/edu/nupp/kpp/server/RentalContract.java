package ua.edu.nupp.kpp.server;

public class RentalContract {
    private Long id;
    private long requestId;
    private String contractDate;
    private double totalCost;

    public RentalContract() {
    }

    public RentalContract(Long id, long requestId, String contractDate, double totalCost) {
        this.id = id;
        this.requestId = requestId;
        this.contractDate = contractDate;
        this.totalCost = totalCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}