package com.example.crm.controller;

import com.example.crm.Enum.StatusEnum;
import com.example.crm.Model.LeadsModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.LeadsRepo;
import com.example.crm.Repo.UsersRepo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadsImportController {

    @Autowired
    private LeadsRepo leadsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @PostMapping("/import")
    public ResponseEntity<?> importLeads(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file provided or file is empty"));
        }

        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename();
        if (contentType != null && !contentType.contains("csv") &&
                !contentType.contains("text/plain") &&
                (originalName == null || !originalName.endsWith(".csv"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "File must be a CSV file"));
        }

        int importedCount = 0;
        List<Map<String, String>> failedRows = new ArrayList<>();

        try {
            Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setIgnoreEmptyLines(true)
                    .build();

            CSVParser parser = new CSVParser(reader, format);
            List<CSVRecord> records = parser.getRecords();

            if (records.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "CSV file has no data rows"));
            }

            int rowNum = 2; // start at 2 (row 1 = header)
            for (CSVRecord record : records) {
                String reason = validateAndSaveRow(record, rowNum);
                if (reason == null) {
                    importedCount++;
                } else {
                    Map<String, String> failed = new HashMap<>();
                    failed.put("row", String.valueOf(rowNum));
                    failed.put("reason", reason);
                    failedRows.add(failed);
                }
                rowNum++;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("imported", importedCount);
            result.put("failed", failedRows);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse CSV: " + e.getMessage()));
        }
    }

    private String validateAndSaveRow(CSVRecord record, int rowNum) {
        try {
            // Required fields validation
            String names = safeGet(record, "names");
            if (names.isBlank()) return "Missing required field: names";

            String phoneStr = safeGet(record, "phone");
            if (phoneStr.isBlank()) return "Missing required field: phone";

            long phone;
            try {
                phone = Long.parseLong(phoneStr.trim());
            } catch (NumberFormatException e) {
                return "Invalid phone number (must be numeric): " + phoneStr;
            }

            String source = safeGet(record, "source");
            if (source.isBlank()) source = "Website";

            String status = safeGet(record, "status");
            if (status.isBlank()) status = "New";

            String assignedTo = safeGet(record, "assignedTo");
            if (assignedTo.isBlank()) assignedTo = "Unassigned";

            String followUp = safeGet(record, "followUp");
            if (followUp.isBlank()) followUp = "";

            String actions = safeGet(record, "actions");
            if (actions.isBlank()) actions = "Initial contact";

            LeadsModel lead = new LeadsModel();
            lead.setNames(names);
            lead.setPhone(phone);
            lead.setSource(source);
            try {
                // .trim() removes hidden spaces, .toUpperCase() handles case mismatches
                lead.setStatus(StatusEnum.valueOf(status.trim().toUpperCase()));
            } catch (IllegalArgumentException | NullPointerException e) {
                // If the CSV value is "Active " or "invalid_status", this runs
                lead.setStatus(StatusEnum.UNKNOWN); // Or handle as an error
            }
            lead.setAssignedTo(assignedTo);
            lead.setFollowUp(followUp);
            lead.setActions(actions);

            // Optional employee link
            String empIdStr = safeGet(record, "employeeId");
            if (!empIdStr.isBlank()) {
                try {
                    Long empId = Long.parseLong(empIdStr.trim());
                    UsersModel emp = usersRepo.findById(empId).orElse(null);
                    if (emp != null) lead.setEmployeeId(emp);
                } catch (NumberFormatException ignored) {
                    // Non-fatal: just skip the employee link
                }
            }

            leadsRepo.save(lead);
            return null; // success

        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    /** Safely get a CSV column value, returning empty string if column doesn't exist */
    private String safeGet(CSVRecord record, String column) {
        try {
            return record.isSet(column) ? record.get(column).trim() : "";
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
