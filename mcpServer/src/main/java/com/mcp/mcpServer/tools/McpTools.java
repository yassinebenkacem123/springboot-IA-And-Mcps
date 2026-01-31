package com.mcp.mcpServer.tools;

import java.util.List;

import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class McpTools {
    @McpTool(
        name = "getEmployee", 
        description = "get information about a given employee"
    )
    public Employee getEmployee(@McpArg(description = "the name of employee.") String name){
        return new Employee(name, 12000d, 3);

    }
    @McpTool(
        name = "getAllEmployees", 
        description = "get information about all nemployees"
    )
    public List<Employee> getAllEmployees(){
        return List.of(
            new Employee("yassine", 12000d, 3),
            new Employee("reda", 12000d, 3),
            new Employee("Ahmed", 12000d, 3),
            new Employee("ayoub", 12000d, 3)
        );
    }
    
}
record Employee(String name, Double salary, int seniority){};

