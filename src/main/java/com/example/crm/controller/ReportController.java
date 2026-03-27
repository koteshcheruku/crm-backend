package com.example.crm.controller;

import com.example.crm.Model.LeadsModel;
import com.example.crm.Repo.LeadsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private LeadsRepo leadsRepo;

    @GetMapping
    public ResponseEntity<?> getReports() {
        List<LeadsModel> allLeads = leadsRepo.findAll();
        
        // Calculate Lead Sources
        Map<String, Long> sourceCounts = allLeads.stream()
                .filter(l -> l.getSource() != null && !l.getSource().isEmpty())
                .collect(Collectors.groupingBy(LeadsModel::getSource, Collectors.counting()));
                
        long totalLeads = allLeads.size();
        
        List<Map<String, Object>> leadSources = sourceCounts.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("source", e.getKey());
                    map.put("leads", e.getValue());
                    map.put("percentage", totalLeads > 0 ? (int)((e.getValue() * 100) / totalLeads) : 0);
                    return map;
                })
                .sorted((m1, m2) -> Integer.compare((Integer) m2.get("percentage"), (Integer) m1.get("percentage")))
                .collect(Collectors.toList());

        // Dummy Sales Metrics since there's no complex invoice table
        List<Map<String, Object>> salesMetrics = List.of(
            createMetric("Total Revenue", "$124,500", "+12.5%"),
            createMetric("Average Deal Size", "$4,150", "+5.2%"),
            createMetric("Sales Cycle", "18 Days", "-2 Days")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("salesMetrics", salesMetrics);
        response.put("leadSources", leadSources);

        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> createMetric(String label, String value, String trend) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("trend", trend);
        return map;
    }
}
