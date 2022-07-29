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
public class UsagereportController {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
  
    @GetMapping("/usageReportSubmit")
    String usageReportSubmit(Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(("SELECT * FROM partlist"));
        ArrayList<Part> dataList = new ArrayList<Part>();
        while (rs.next()) {
          Part obj = new Part();
          obj.setPartID(rs.getInt("PartId"));
          obj.setPartName(rs.getString("partName"));
          dataList.add(obj);
        }
        model.put("Parts", dataList);
        ResultSet rs2 = stmt.executeQuery(("SELECT * FROM Tools"));
        ArrayList<Tool> dataList2 = new ArrayList<Tool>();
        while (rs2.next()) {
          Tool obj = new Tool();
          obj.setToolID(rs2.getInt("ToolId"));
          obj.setToolName(rs2.getString("ToolName"));
          dataList2.add(obj);
        }
        model.put("Tools", dataList2);
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
        Usagereport usagereport = new Usagereport();
        model.put("UsageReport", usagereport);
        return "usageReportSubmit";
      } catch (Exception e) {
        model.put("message", e.getMessage());
        return "error";
      }
      
    }

    @GetMapping("/usageReportView")
    String usageReportView(Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(("SELECT * FROM UsageReports"));
        ArrayList<Usagereport> dataList = new ArrayList<Usagereport>();
        while (rs.next()) {
            Usagereport obj = new Usagereport();
          obj.setMessage(rs.getString("UsageReport"));
          obj.setRepairID(rs.getInt("RepairID"));
          obj.setToolID(rs.getInt("ToolID"));
          dataList.add(obj);
        }
        model.put("UsageReports", dataList);
        return "usageReportView";
      } catch (Exception e) {
        model.put("message", e.getMessage());
        return "error";
      }
    }

    @PostMapping(path = "/usageReportSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String handleusageReportSubmit(Map<String, Object> model,Usagereport UsageReport) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      
      String sql;
      if(UsageReport.getPartID() == 0){
        if(UsageReport.getToolID() == 0){sql = "INSERT INTO UsageReports (Message, RepairID, Date) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getDate()+"')";}
        else{sql = "INSERT INTO UsageReports (Message, RepairID, ToolID, Date) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getToolID()+"', '"+UsageReport.getDate()+"')";
      }
      }
      else if(UsageReport.getToolID() == 0){sql = "INSERT INTO UsageReports (Message, RepairID, PartID, Date) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getPartID()+"', '"+UsageReport.getDate()+"')";}
      else {sql = "INSERT INTO UsageReports (Message, RepairID, ToolID,PartID, Date) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getToolID()+"', '"+UsageReport.getPartID()+"', '"+UsageReport.getDate()+"')";}
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
