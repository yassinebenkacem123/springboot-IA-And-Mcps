package com.mcp.mcpServer.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AccountDTO {

    @NotBlank(message = "Account name is required")
    private String accountName;

    private String description;

}
