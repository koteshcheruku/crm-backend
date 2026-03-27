package com.example.crm.Dto;

import com.example.crm.Model.RoleModel;
import lombok.Data;
import java.util.List;

@Data
public class RoleDto {
    private Long id;
    private String name;
    private String color;
    private List<String> permissions;
    private long users;

    public RoleDto(RoleModel role, long userCount) {
        this.id = role.getId();
        this.name = role.getName();
        this.color = role.getColor();
        this.permissions = role.getPermissions();
        this.users = userCount;
    }
}
