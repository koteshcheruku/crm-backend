package com.example.crm.Dto;

import com.example.crm.Enum.TypeEnum;
import com.example.crm.Model.DocsModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocsDto {
    private Long id;
    private String fileName;
    private Long size;
    private String uploadedBy;
    private LocalDate date;
    private String fileUrl;
    private TypeEnum Type;
    private Long employeeId;

    public DocsDto(DocsModel data) {
        this.fileName = data.getFileName();
        this.uploadedBy = data.getUploadedBy();
        this.size = data.getSize();
        this.fileUrl = data.getFileUrl();
        this.Type = data.getType();
        if (data.getEmployeeId() != null) {
            this.employeeId = data.getEmployeeId().getId();
        }
    }
}
