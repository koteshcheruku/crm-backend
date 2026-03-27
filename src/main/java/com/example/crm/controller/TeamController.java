package com.example.crm.controller;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.TeamModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.TeamRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamRepo teamRepo;

    @Autowired
    private UsersRepo usersRepo;

    /** GET /api/teams – returns teams the current user belongs to */
    @GetMapping("")
    public ResponseEntity<?> getMyTeams(Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            UsersModel me = principal.getEmployee();
            List<TeamModel> teams = teamRepo.findByMembersContaining(me);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch teams");
        }
    }

    /** POST /api/teams – create a new team. Body: { "name": "...", "memberIds": [1,2,3] } */
    @PostMapping("")
    public ResponseEntity<?> createTeam(@RequestBody Map<String, Object> body, Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            UsersModel creator = principal.getEmployee();

            String name = (String) body.get("name");
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().body("Team name is required");
            }

            TeamModel team = new TeamModel();
            team.setName(name.trim());
            team.setCreatedBy(creator.getUsername());
            team.getMembers().add(creator);

            // Add other requested members
            @SuppressWarnings("unchecked")
            List<Integer> memberIds = (List<Integer>) body.get("memberIds");
            if (memberIds != null) {
                for (Integer mid : memberIds) {
                    usersRepo.findById(mid.longValue()).ifPresent(u -> {
                        if (!team.getMembers().contains(u)) team.getMembers().add(u);
                    });
                }
            }

            teamRepo.save(team);
            return ResponseEntity.status(HttpStatus.CREATED).body(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create team");
        }
    }

    /** POST /api/teams/{id}/members – add a member. Body: { "userId": 5 } */
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            TeamModel team = teamRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Team not found: " + id));
            Long userId = body.get("userId");
            if (userId == null) return ResponseEntity.badRequest().body("userId is required");
            UsersModel user = usersRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            if (!team.getMembers().contains(user)) team.getMembers().add(user);
            teamRepo.save(team);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add member");
        }
    }

    /** DELETE /api/teams/{id}/members/{userId} – remove a member */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        try {
            TeamModel team = teamRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Team not found: " + id));
            team.getMembers().removeIf(u -> u.getId().equals(userId));
            teamRepo.save(team);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove member");
        }
    }

    /** DELETE /api/teams/{id} – delete a team */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        try {
            teamRepo.findById(id).orElseThrow(() -> new RuntimeException("Team not found: " + id));
            teamRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete team");
        }
    }
}
