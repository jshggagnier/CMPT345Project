/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;


@Controller
@SpringBootApplication
public class ToolusagereportController {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
  
    @GetMapping("/toolusageReportSubmit")
    String toolusageReportSubmit(Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(("SELECT * FROM Tools"));
        ArrayList<Tool> dataList = new ArrayList<Tool>();
        while (rs.next()) {
          Tool obj = new Tool();
          obj.setToolID(rs.getInt("ToolId"));
          obj.setToolName(rs.getString("ToolName"));
          dataList.add(obj);
        }
        model.put("Tools", dataList);
        ResultSet rs3 = stmt.executeQuery("SELECT * FROM Workorders WHERE enddate IS NULL");
        ArrayList<Workorder> dataList3 = new ArrayList<Workorder>();
        while (rs3.next()) {
        Workorder obj = new Workorder();
        obj.setOrderNum(rs3.getInt("OrderNum"));
        obj.setStartDate(rs3.getString("startdate"));
        obj.setEndDate(rs3.getString("enddate"));
        obj.setClaimID(rs3.getString("ClaimID"));
        obj.setDescription(rs3.getString("Description"));
        obj.setCustomerNum(rs3.getInt("CustomerNum"));
        dataList3.add(obj);
      }
      model.put("openWorkOrders", dataList3);
      ToolUsageReport usagereport = new ToolUsageReport();
      model.put("ToolUsageReport", usagereport);
      System.out.println("page hit");
      return "toolusageReportSubmit";
      } catch (Exception e) {
        model.put("message", e.getMessage());
        return "error";
      }
      
    }

    @PostMapping(path = "/toolusageReportSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String handletoolusageReportSubmit(Map<String, Object> model,ToolUsageReport UsageReport) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO ToolUsageReports (Message, RepairID, ToolID, Date) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getToolID()+"', '"+UsageReport.getDate()+"')";
      // the above is handling the null values (which are zero in html), which will help greatly when submitting later (since we need it to be a foreign key.)
      System.out.println(sql);
      stmt.executeUpdate(sql);
      return "redirect:/WorkOrderEdit/"+UsageReport.getRepairID();
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
