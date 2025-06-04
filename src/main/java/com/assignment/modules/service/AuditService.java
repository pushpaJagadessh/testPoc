package com.assignment.modules.service;

import com.assignment.modules.model.AuditLog;
import com.assignment.modules.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAudit(String username, String action, String entityId, String details,
                         HttpServletRequest request, int statusCode, String errorMsg) {

        String ipAddress = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        AuditLog log = new AuditLog(
                username,
                action,
                entityId,
                details,
                ipAddress,
                requestUri,
                statusCode,
                errorMsg
        );

        auditLogRepository.save(log);
    }

    public List<AuditLog> getNAuditLogs(int N) {
        if(N < -1 || N == 0) {
            return null;
        }
        else if(N == -1) {
            return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        }
        else{
            return auditLogRepository.findAll(PageRequest.of(0, N, Sort.by(Sort.Direction.DESC, "timestamp"))).getContent();
        }
    }
}
