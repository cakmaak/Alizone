package com.Alizone.Exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
}
