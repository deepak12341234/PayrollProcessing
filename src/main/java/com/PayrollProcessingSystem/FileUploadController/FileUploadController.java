package com.PayrollProcessingSystem.FileUploadController;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.PayrollProcessingSystem.Exception.InvalidFileFormatException;
import com.PayrollProcessingSystem.FileUploadService.FileUploadService;
import com.PayrollProcessingSystem.Model.Records;

@Controller
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/")
    public String index(Model model) {
    	   List<Records> records = fileUploadService.getRecords();
    	   if(records.size()>0) {
    	        model.addAttribute("records", records);

    	        long totalEmployees = fileUploadService.getTotalEmployees();
    	        model.addAttribute("totalEmployees", totalEmployees);

    	        Map<String, List<Records>> monthlyEmployeeJoinDetails = fileUploadService.getMonthlyEmployeeJoinDetails();
                Map<String, List<Map<String, String>>> monthlyEmployeeExitDetails = fileUploadService.getMonthlyEmployeeExitDetails();
                model.addAttribute("monthlyEmployeeJoinDetails", monthlyEmployeeJoinDetails);
                model.addAttribute("monthlyEmployeeExitDetails", monthlyEmployeeExitDetails);
            
    	        Map<String, Map<String, Object>> monthlySalaryReport = fileUploadService.getMonthlySalaryReport();
    	        model.addAttribute("monthlySalaryReport", monthlySalaryReport);

    	        Map<String, Map<String, Object>> employeeWiseFinancialReport = fileUploadService.getEmployeeWiseFinancialReport();
    	        model.addAttribute("employeeWiseFinancialReport", employeeWiseFinancialReport);

    	        Map<String, Map<String, Object>> monthlyAmountReleased = fileUploadService.getMonthlyAmountReleased();
    	        model.addAttribute("monthlyAmountReleased", monthlyAmountReleased);

    	        List<Map<String, String>> yearlyFinancialReport = fileUploadService.getYearlyFinancialReport();
    	        model.addAttribute("yearlyFinancialReport", yearlyFinancialReport);
    	   }
        return "index";
    }
    @GetMapping("/reset")
    public String reset(Model model) {
    	List<Records> records = fileUploadService.getRecords(); 
    	records.removeAll(records);
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("message", "Only .txt and .csv files are allowed.");
            return "redirect:/";
        }

        try {
            List<Records> records = fileUploadService.processFile(file);
            model.addAttribute("records", records);
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + fileName + "'");
        } catch (InvalidFileFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload '" + fileName + "'. Error: " + e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload '" + fileName + "'");
        }

        return "redirect:/";
    }

  

    @ExceptionHandler(InvalidFileFormatException.class)
    public String handleInvalidFileFormatException(InvalidFileFormatException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/";
    }
}
