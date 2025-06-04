package com.assignment.modules.controller;

import com.assignment.modules.service.AuditService;
import com.assignment.modules.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;


    @GetMapping
    public List<AuditLog> getAllAuditLogs() {
        return auditService.getNAuditLogs(-1);
    }

    @GetMapping("/{N}")
    public List<AuditLog> getNAuditLogs(@PathVariable int N) {
        return auditService.getNAuditLogs(N);
    }
}
