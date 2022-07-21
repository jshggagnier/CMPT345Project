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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
      Usagereport usagereport = new Usagereport();
      model.put("UsageReport", usagereport);
      return "usageReportSubmit";
    }

    @GetMapping("/usageReportView")
    String usageReportView(Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MakesUseOf (UsageReport varchar(30), RepairID integer, ToolID integer)");
        ResultSet rs = stmt.executeQuery(("SELECT * FROM MakesUseOf"));
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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MakesUseOf (UsageReport varchar(30), RepairID integer, ToolID integer)");
      String sql = "INSERT INTO MakesUseOf (UsageReport, RepairID, ToolID) VALUES ('"+UsageReport.getMessage()+"', '"+UsageReport.getRepairID()+"', '"+UsageReport.getToolID()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/usageReportView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
