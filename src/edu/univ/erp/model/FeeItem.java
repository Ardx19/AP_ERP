package edu.univ.erp.model;

import java.math.BigDecimal;
import java.sql.Date;

public class FeeItem {
    private int feeId;
    private String description;
    private BigDecimal amount;
    private String status;
    private Date dueDate;

    public FeeItem(int feeId, String description, BigDecimal amount, String status, Date dueDate) {
        this.feeId = feeId;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Getters
    public int getFeeId() { return feeId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public Date getDueDate() { return dueDate; }
}