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
public class WorkReportController {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/workReportSubmit/{nid}")
  String workReportSubmit(Map<String, Object> model,@PathVariable String nid) {
    WorkReport workReport = new WorkReport();
    workReport.setOrderNum(Integer.parseInt(nid));
    model.put("WorkReport", workReport);

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs1 = stmt.executeQuery(("SELECT * FROM StaffDetails"));
      ArrayList<Staff> dataList = new ArrayList<Staff>();
      while (rs1.next()) {
        
        Staff obj = new Staff();
        obj.setName(rs1.getString("Name"));
        obj.setPhoneNumber(rs1.getString("PhoneNumber"));
        obj.setStaffId(rs1.getInt("StaffId"));
        
        dataList.add(obj);
      }
      model.put("Staff", dataList);
      return "workReportSubmit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/workReportView")
  String viewWorkReports(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM WorkReports"));
      ArrayList<WorkReport> dataList = new ArrayList<WorkReport>();
      while (rs.next()) {
        WorkReport obj = new WorkReport();
        obj.setOrderNum(rs.getInt("OrderNum"));
        obj.setStaffID(rs.getInt("StaffID"));
        obj.setDate(rs.getString("Date"));
        obj.setMessage(rs.getString("Message"));
        obj.setCloseWorkorder(rs.getBoolean("CloseWorkorder"));
        dataList.add(obj);
      }
      model.put("WorkReports", dataList);
      return "workReportView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/workReportSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleWorkReportSubmit(Map<String, Object> model,WorkReport workReport) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      workReport.setMessage(workReport.getMessage().replace("'",""));
      String sql = "INSERT INTO WorkReports (OrderNum, StaffID, Message, Date, CloseWorkOrder) VALUES ("+workReport.getOrderNum()+", "+workReport.getStaffID()+", '"+workReport.getMessage()+"', '"+workReport.getDate()+"', '"+workReport.getCloseWorkorder()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      if(workReport.isCloseWorkorder())
      {
        ResultSet rs = stmt.executeQuery(("SELECT * FROM Workorders WHERE OrderNum = " + workReport.getOrderNum()));
        rs.next();
        if(rs.getString("EndDate")==null)
        {
          sql = "UPDATE Workorders SET EndDate='"+workReport.getDate()+"' WHERE OrderNum='"+workReport.getOrderNum()+"';";
        }
        else
        {
          sql = "UPDATE Workorders SET EndDate=NULL WHERE OrderNum='"+workReport.getOrderNum()+"';";
        }
        System.out.println(sql);
        stmt.executeUpdate(sql);
      }
      
      return "redirect:/WorkOrderEdit/"+workReport.getOrderNum()+"?";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
