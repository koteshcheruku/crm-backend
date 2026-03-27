package com.example.crm.service;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.LeadsRepo;
import com.example.crm.Repo.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class dashboardService {
    @Autowired
    private TasksRepo tasksRepo;
    @Autowired
    private LeadsRepo leadsRepo;

    public ResponseEntity<?> getData(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        UsersModel user = principal.getEmployee();

        boolean isAdmin = ("ADMIN".equals(user.getRole().getName()) || "MANAGER".equals(user.getRole().getName()));

        long tasksCount = 0;
        long activeTasksCount = 0;
        long leadsCount = 0;
        long newTodayCount = 0;
        long followUpsCount = 0;

        Map<com.example.crm.Enum.StatusEnum, Long> statusCounts;

        if (isAdmin) {
            tasksCount = tasksRepo.count();
            activeTasksCount = tasksRepo.findAll().stream().filter(
                    t -> !"Completed".equalsIgnoreCase(t.getStatus()) && !"Done".equalsIgnoreCase(t.getStatus()))
                    .count();

            var allLeads = leadsRepo.findAll();
            leadsCount = allLeads.size();
            newTodayCount = allLeads.stream().filter(l -> l.getStatus() == com.example.crm.Enum.StatusEnum.NEW).count();
            followUpsCount = allLeads.stream().filter(l -> l.getFollowUp() != null && !l.getFollowUp().trim().isEmpty())
                    .count();
            statusCounts = allLeads.stream().collect(java.util.stream.Collectors
                    .groupingBy(com.example.crm.Model.LeadsModel::getStatus, java.util.stream.Collectors.counting()));
        } else {
            var myTasks = tasksRepo.findByEmployeeId_Id(user.getId());
            tasksCount = myTasks.size();
            activeTasksCount = myTasks.stream().filter(
                    t -> !"Completed".equalsIgnoreCase(t.getStatus()) && !"Done".equalsIgnoreCase(t.getStatus()))
                    .count();

            var myLeads = leadsRepo.findByEmployeeId_Id(user.getId());
            leadsCount = myLeads.size();
            newTodayCount = myLeads.stream().filter(l -> l.getStatus() == com.example.crm.Enum.StatusEnum.NEW).count();
            followUpsCount = myLeads.stream().filter(l -> l.getFollowUp() != null && !l.getFollowUp().trim().isEmpty())
                    .count();
            statusCounts = myLeads.stream().collect(java.util.stream.Collectors
                    .groupingBy(com.example.crm.Model.LeadsModel::getStatus, java.util.stream.Collectors.counting()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("tasks", tasksCount);
        data.put("activeTasks", activeTasksCount);
        data.put("leads", leadsCount);
        data.put("newToday", newTodayCount);
        data.put("followUps", followUpsCount);

        List<Map<String, Object>> leadsByStatus = java.util.Arrays.asList(
                createStatusData("New", statusCounts.getOrDefault(com.example.crm.Enum.StatusEnum.NEW, 0L), leadsCount,
                        "from-blue-500 to-cyan-400"),
                createStatusData("Contacted", statusCounts.getOrDefault(com.example.crm.Enum.StatusEnum.CONTACTED, 0L),
                        leadsCount, "from-purple-500 to-pink-400"),
                createStatusData("Qualified", statusCounts.getOrDefault(com.example.crm.Enum.StatusEnum.QUALIFIED, 0L),
                        leadsCount, "from-emerald-500 to-teal-400"),
                createStatusData("Converted", statusCounts.getOrDefault(com.example.crm.Enum.StatusEnum.OPEN, 0L),
                        leadsCount, "from-orange-500 to-amber-400"));
        data.put("leadsByStatus", leadsByStatus);

        List<Map<String, Object>> performance = java.util.Arrays.asList(
                createPerfData("Conversion Rate", "14%", "+2.4%"),
                createPerfData("Avg. Response Time", "2h 15m", "-30m"),
                createPerfData("Customer Satisfaction", "4.8/5", "+0.2"));
        data.put("performance", performance);

        return ResponseEntity.ok(data);
    }

    private Map<String, Object> createStatusData(String status, long count, long total, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("count", count);
        map.put("percentage", total > 0 ? (int) ((count * 100) / total) : 0);
        map.put("color", color);
        return map;
    }

    private Map<String, Object> createPerfData(String label, String value, String trend) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("trend", trend);
        return map;
    }
}
