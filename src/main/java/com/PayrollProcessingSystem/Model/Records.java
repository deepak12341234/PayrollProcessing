package com.PayrollProcessingSystem.Model;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Records {

    private int sequenceNo;
    private String empID;
    private String empFName;
    private String empLName;
    private String designation;
    private String event;
    private String value;
    private LocalDate eventDate;
    private String notes;
   

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getEmpFName() {
        return empFName;
    }

    public void setEmpFName(String empFName) {
        this.empFName = empFName;
    }

    public String getEmpLName() {
        return empLName;
    }

    public void setEmpLName(String empLName) {
        this.empLName = empLName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isOnboard() {
        return "ONBOARD".equalsIgnoreCase(event);
    }

    public boolean isSalary() {
        return "SALARY".equalsIgnoreCase(event);
    }

    public boolean isBonus() {
        return "BONUS".equalsIgnoreCase(event);
    }

    public boolean isExit() {
        return "EXIT".equalsIgnoreCase(event);
    }

    public boolean isReimbursement() {
        return "REIMBURSEMENT".equalsIgnoreCase(event);
    }
    public LocalDate getJoiningDate() {
        return isOnboard() ? LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null;
    }

    public LocalDate getExitDate() {
        return isExit() ? LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null;
    }

	@Override
	public String toString() {
		return "Records [sequenceNo=" + sequenceNo + ", empID=" + empID + ", empFName=" + empFName + ", empLName="
				+ empLName + ", designation=" + designation + ", event=" + event + ", value=" + value + ", eventDate="
				+ eventDate + ", notes=" + notes + "]";
	}
    
}
