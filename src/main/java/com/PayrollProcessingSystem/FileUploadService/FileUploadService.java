package com.PayrollProcessingSystem.FileUploadService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.PayrollProcessingSystem.Exception.InvalidFileFormatException;
import com.PayrollProcessingSystem.Model.Records;


@Service
public class FileUploadService {
	private List<Records> records = new ArrayList<>();


	public List<Records> processFile(MultipartFile file) throws Exception {

		records.clear();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if ("ONBOARD".equals(data[5]) && data.length != 9) {
					throw new InvalidFileFormatException("Invalid number of fields in the record for onboard: " + line);
				} else if (!"ONBOARD".equals(data[5]) && data.length != 6) {
					throw new InvalidFileFormatException("Invalid number of fields in the record for other: " + line);
				}

				Records record = new Records();
				try {
					int sequenceNo = Integer.parseInt(data[0]);
					record.setSequenceNo(sequenceNo);
				} catch (NumberFormatException e) {
					throw new InvalidFileFormatException("Invalid Sequence Number: " + data[0]);
				}
				record.setEmpID(data[1]);
				if ("ONBOARD".equals(data[5])) {
					record.setEmpFName(data[2]);
					record.setEmpLName(data[3]);
					record.setDesignation(data[4]);
					record.setEvent(data[5]);
					record.setValue(data[6]);
					try {
						record.setEventDate(LocalDate.parse(data[7], DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					} catch (DateTimeParseException e) {
						throw new InvalidFileFormatException("Invalid Event Date: " + data[7]);
					}
					record.setNotes(data[8]);
				} else {					
					record.setEvent(data[2]);
					record.setValue(data[3]);
					record.setNotes(data[5]);
					try {
						record.setEventDate(LocalDate.parse(data[4], DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					} catch (DateTimeParseException e) {
						throw new InvalidFileFormatException("Invalid Event Date: " + data[4]);
					}

				}

				records.add(record);
			}
		}

		return records;
	}

	public List<Records> getRecords() {
		return records;
	}
	public long getTotalEmployees() {
        return records.stream()
                .filter(record -> record.isOnboard() || record.isExit())
                .map(Records::getEmpID)
                .distinct()
                .count();
    }

	   public Map<String, List<Records>> getMonthlyEmployeeJoinDetails() {
	        return records.stream()
	                .filter(Records::isOnboard).sorted((r1,r2)->r2.getJoiningDate().getMonthValue()-r1.getJoiningDate().getDayOfMonth())
	                .collect(Collectors.groupingBy(record -> record.getJoiningDate().getMonth().toString(), Collectors.toList()));
	    }

	   public Map<String, List<Map<String, String>>> getMonthlyEmployeeExitDetails() {
	        Map<String, List<Map<String, String>>> exitDetails = new HashMap<>();
	        
	        Map<String, Records> onboardingRecords = records.stream()
	            .filter(record -> "ONBOARD".equals(record.getEvent()))
	            .collect(Collectors.toMap(Records::getEmpID, record -> record));

	        records.stream()
	            .filter(record -> "EXIT".equals(record.getEvent()))
	            .forEach(record -> {
	                String month = record.getExitDate().getMonth().toString();
	                Records onboardingRecord = onboardingRecords.get(record.getEmpID());
	                if (onboardingRecord != null) {
	                    Map<String, String> employeeDetail = new HashMap<>();
	                    employeeDetail.put("empID", record.getEmpID());
	                    employeeDetail.put("empFName", onboardingRecord.getEmpFName());
	                    employeeDetail.put("empLName", onboardingRecord.getEmpLName());
	                    employeeDetail.put("designation", onboardingRecord.getDesignation());
	                    exitDetails.computeIfAbsent(month, k -> new ArrayList<>()).add(employeeDetail);
	                } else {
	                	throw new InvalidFileFormatException("Employee must be onboarded");	                }
	            });

	        return exitDetails;
	    }
	   public Map<String, Map<String, Object>> getMonthlySalaryReport() {
	        return records.stream()
	            .filter(Records::isSalary)
	            .collect(Collectors.groupingBy(
	                record -> record.getEventDate().getMonth().toString(),
	                Collectors.collectingAndThen(
	                    Collectors.toList(),
	                    list -> {
	                        Map<String, Object> reportEntry = new HashMap<>();
	                        double totalSalary = list.stream().mapToDouble(record -> Double.parseDouble(record.getValue())).sum();
                            Set<String> uniqueEmployees = new HashSet<>();
                            list.forEach(record -> uniqueEmployees.add(record.getEmpID()));
	                        reportEntry.put("Total Salary", totalSalary);
	                        reportEntry.put("Total Employees", uniqueEmployees.size());
	                        return reportEntry;
	                    }
	                )
	            ));
	    }

    public Map<String, Map<String, Object>> getEmployeeWiseFinancialReport() {
        return records.stream()
                .collect(Collectors.groupingBy(Records::getEmpID, 
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> report = new HashMap<>();
                            report.put("EmpFName", list.get(0).getEmpFName());
                            report.put("EmpLName", list.get(0).getEmpLName());
                            double totalAmountPaid = list.stream()
                                    .filter(record -> record.isSalary() || record.isBonus() || record.isReimbursement())
                                    .mapToDouble(record -> Double.parseDouble(record.getValue()))
                                    .sum();
                            report.put("TotalAmountPaid", totalAmountPaid);
                            return report;
                        })));
    }

    public Map<String, Map<String, Object>> getMonthlyAmountReleased() {
        return records.stream()
                .filter(record -> record.isSalary() || record.isBonus() || record.isReimbursement())
                .collect(Collectors.groupingBy(
                        record -> record.getEventDate().getMonth().toString(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Map<String, Object> result = new HashMap<>();
                                    double totalAmount = list.stream().mapToDouble(record -> Double.parseDouble(record.getValue())).sum();
                                    Set<String> uniqueEmployees = new HashSet<>();
                                    list.forEach(record -> uniqueEmployees.add(record.getEmpID()));
                                    result.put("Total Amount", totalAmount);
                                    result.put("Total Employees", uniqueEmployees.size());
                                    return result;
                                }
                        )
                ));
    }

    public List<Map<String, String>> getYearlyFinancialReport() {
        return records.stream()
                .filter(record -> record.isSalary() || record.isBonus() || record.isReimbursement())
                .map(record -> {
                    Map<String, String> report = new HashMap<>();
                    report.put("Event", record.getEvent());
                    report.put("EmpID", record.getEmpID());
                    report.put("EventDate", record.getEventDate().toString());
                    report.put("EventValue", record.getValue());
                    return report;
                })
                .collect(Collectors.toList());
    }
}

