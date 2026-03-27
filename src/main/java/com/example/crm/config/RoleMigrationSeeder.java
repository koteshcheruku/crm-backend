package com.example.crm.config;

import com.example.crm.Model.RoleModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.RoleRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RoleMigrationSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create default roles if they don't exist
        RoleModel adminRole = createRoleIfNotFound("ADMIN", "from-red-500 to-rose-400", Arrays.asList("ALL_ACCESS", "CREATE_ROLES"));
        RoleModel managerRole = createRoleIfNotFound("MANAGER", "from-purple-500 to-indigo-400", Arrays.asList("VIEW_DASHBOARD", "MANAGE_USERS"));
        RoleModel userRole = createRoleIfNotFound("USER", "from-blue-500 to-cyan-400", Arrays.asList("VIEW_DASHBOARD"));
        RoleModel salesRole = createRoleIfNotFound("SALES", "from-emerald-500 to-teal-400", Arrays.asList("VIEW_LEADS", "EDIT_LEADS"));
        RoleModel supportRole = createRoleIfNotFound("SUPPORT", "from-orange-500 to-amber-400", Arrays.asList("VIEW_CUSTOMERS"));

        // Migrate users whose role_id is null
        List<UsersModel> users = usersRepo.findAll();
        List<UsersModel> usersToUpdate = new ArrayList<>();
        for (UsersModel user : users) {
            if (user.getRole() == null) {
                // Read from old column if available, else default to USER
                String oldRole = user.getOldRole();
                if (oldRole != null) {
                    RoleModel role = roleRepo.findByName(oldRole).orElse(userRole);
                    user.setRole(role);
                    usersToUpdate.add(user);
                } else {
                    user.setRole(userRole);
                }
            }
        }
        usersRepo.saveAll(usersToUpdate);
    }

    private RoleModel createRoleIfNotFound(String name, String color, List<String> permissions) {
        return roleRepo.findByName(name).orElseGet(() -> {
            RoleModel role = new RoleModel();
            role.setName(name);
            role.setColor(color);
            role.setPermissions(permissions);
            return roleRepo.save(role);
        });
    }
}
