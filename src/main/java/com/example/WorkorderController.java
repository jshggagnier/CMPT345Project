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
public class WorkorderController {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/workOrderSubmit")
  String workOrderSubmit(Map<String, Object> model) {
    Workorder workorder = new Workorder();
    model.put("WorkOrder", workorder);

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM customers"));
      ArrayList<Customer> dataList = new ArrayList<Customer>();
      while (rs.next()) {
        Customer obj = new Customer();
        obj.setCustIdentifier(rs.getInt("CustIdentifier"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setPhoneNumber(rs.getString("PhoneNumber"));
        obj.setAddress(rs.getString("Address"));
        dataList.add(obj);
      }
      model.put("Customers", dataList);
      ResultSet rs20 = stmt.executeQuery(("SELECT * FROM WarrantyClaim"));
      ArrayList<Warrantyclaim> dataList20 = new ArrayList<Warrantyclaim>();
      while (rs20.next()) {
        Warrantyclaim obj = new Warrantyclaim();
        obj.setBrand(rs20.getString("Brand"));
        obj.setWarrantyID(rs20.getString("WarrantyID"));
        dataList20.add(obj);
      }
      model.put("WarrantyClaims", dataList20);
      return "workOrderSubmit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/workOrderView")
  String viewWorkOrders(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT OrderNum,startdate,enddate,ClaimID,Description,name FROM Workorders,Customers WHERE Workorders.CustomerNum=Customers.custidentifier AND Workorders.enddate IS NOT NULL"));
      ArrayList<TableArray> dataList = new ArrayList<TableArray>();
      while (rs.next()) {
        TableArray obj = new TableArray();
        obj.setFirstString(rs.getString("OrderNum"));
        obj.setThirdString(rs.getString("startdate"));
        obj.setFourthString(rs.getString("enddate"));
        obj.setFifthString(rs.getString("ClaimID"));
        obj.setSixthString(rs.getString("Description"));
        obj.setSecondString(rs.getString("name"));
        dataList.add(obj);
      }
      model.put("closedWorkOrders", dataList);
      ResultSet rs2 = stmt.executeQuery(("SELECT OrderNum,startdate,enddate,ClaimID,Description,name FROM Workorders,Customers WHERE Workorders.CustomerNum=Customers.custidentifier AND Workorders.enddate IS NULL"));
      ArrayList<TableArray> dataList2 = new ArrayList<TableArray>();
      while (rs2.next()) {
        TableArray obj = new TableArray();
        obj.setFirstString(rs2.getString("OrderNum"));
        obj.setThirdString(rs2.getString("startdate"));
        obj.setFourthString(rs2.getString("enddate"));
        obj.setFifthString(rs2.getString("ClaimID"));
        obj.setSixthString(rs2.getString("Description"));
        obj.setSecondString(rs2.getString("name"));
        dataList2.add(obj);
      }
      model.put("openWorkOrders", dataList2);
      ResultSet rs3 = stmt.executeQuery(("SELECT OrderNum,startdate,enddate,ClaimID,Description,name FROM Workorders,Customers WHERE Workorders.OrderNum NOT IN(SELECT Workorders.OrderNum FROM Workorders,WorkReports WHERE Workorders.OrderNum = Workreports.OrderNum) AND Workorders.CustomerNum=Customers.custidentifier"));
      ArrayList<TableArray> dataList3 = new ArrayList<TableArray>();
      while (rs3.next()) {
        TableArray obj = new TableArray();
        obj.setFirstString(rs3.getString("OrderNum"));
        obj.setThirdString(rs3.getString("startdate"));
        obj.setFourthString(rs3.getString("enddate"));
        obj.setFifthString(rs3.getString("ClaimID"));
        obj.setSixthString(rs3.getString("Description"));
        obj.setSecondString(rs3.getString("name"));
        dataList3.add(obj);
      }
      model.put("newWorkOrders", dataList3);
      return "workOrderView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/workOrderSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleWorkorderSubmit(Map<String, Object> model,Workorder workorder) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql;
      if(workorder.getClaimID().equals("")) {sql = "INSERT INTO Workorders (CustomerNum,StartDate,Description) VALUES ("+workorder.getCustomerNum()+", '"+workorder.getStartDate()+"', '"+workorder.getDescription()+"')";}
      else{sql = "INSERT INTO Workorders (CustomerNum,ClaimID,StartDate,Description) VALUES ("+workorder.getCustomerNum()+", '"+workorder.getClaimID()+"', '"+workorder.getStartDate()+"', '"+workorder.getDescription()+"')";}
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/workOrderView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/WorkOrderEdit/{nid}")
  String LoadFormWorkOrderEdit(Map<String, Object> model, @PathVariable String nid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Workorders WHERE OrderNum = " + nid)); // this should only ever run once, since OrderNum is serial
      Workorder workorder = new Workorder();
      while (rs.next()) {
        workorder.setOrderNum(rs.getInt("OrderNum"));
        workorder.setStartDate(rs.getString("startdate"));
        workorder.setEndDate(rs.getString("enddate"));
        workorder.setClaimID(rs.getString("ClaimID"));
        workorder.setDescription(rs.getString("Description"));
        workorder.setCustomerNum(rs.getInt("CustomerNum"));
      }
      ResultSet rs2 = stmt.executeQuery(("SELECT * FROM customers WHERE CustIdentifier = " + workorder.getCustomerNum()));
      Customer customer = new Customer();
      while (rs2.next()) {
        
        customer.setCustIdentifier(rs2.getInt("CustIdentifier"));
        customer.setName(rs2.getString("Name"));
        customer.setEmail(rs2.getString("Email"));
        customer.setPhoneNumber(rs2.getString("PhoneNumber"));
        customer.setAddress(rs2.getString("Address"));
      }
      ResultSet rs3 = stmt.executeQuery(("SELECT * FROM WorkReports WHERE OrderNum = " + workorder.getOrderNum()));
      ArrayList<WorkReport> WorkReports = new ArrayList<WorkReport>();
      while (rs3.next()) {
        WorkReport obj = new WorkReport();
        obj.setOrderNum(rs3.getInt("OrderNum"));
        obj.setStaffID(rs3.getInt("StaffID"));
        obj.setDate(rs3.getString("Date"));
        obj.setMessage(rs3.getString("Message"));
        obj.setCloseWorkorder(rs3.getBoolean("CloseWorkorder"));
        WorkReports.add(obj);
      }
      ResultSet rs4 = stmt.executeQuery(("SELECT * FROM ToolUsageReports WHERE RepairID = " + workorder.getOrderNum()));
      ArrayList<ToolUsageReport> dataList = new ArrayList<ToolUsageReport>();
      while (rs4.next()) {
        ToolUsageReport obj = new ToolUsageReport();
        obj.setToolID(rs4.getInt("ToolID"));
        obj.setDate(rs4.getString("Date"));
        obj.setMessage(rs4.getString("Message"));
        dataList.add(obj);
      }
      ResultSet rs5 = stmt.executeQuery(("SELECT * FROM PartUsageReports WHERE RepairID = " + workorder.getOrderNum()));
      ArrayList<PartUsageReport> dataList2 = new ArrayList<PartUsageReport>();
      while (rs5.next()) {
        PartUsageReport obj = new PartUsageReport();
        obj.setPartID(rs5.getInt("PartId"));
        obj.setDate(rs5.getString("Date"));
        obj.setMessage(rs5.getString("Message"));
        dataList2.add(obj);
      }
      ResultSet rs20 = stmt.executeQuery(("SELECT * FROM WarrantyClaim"));
      ArrayList<Warrantyclaim> dataList20 = new ArrayList<Warrantyclaim>();
      while (rs20.next()) {
        Warrantyclaim obj = new Warrantyclaim();
        obj.setBrand(rs20.getString("Brand"));
        obj.setWarrantyID(rs20.getString("WarrantyID"));
        dataList20.add(obj);
      }
      model.put("WarrantyClaims", dataList20);
      model.put("ToolUsageReports",dataList);
      model.put("PartUsageReports", dataList2);
      model.put("WorkReports",WorkReports);
      model.put("Customer", customer);
      model.put("WorkOrder", workorder);
      return "workOrderEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/workOrderEditForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleWorkorderEdit(Map<String, Object> model,Workorder workorder) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "UPDATE Workorders SET ClaimID='"+workorder.getClaimID()+"', StartDate='"+workorder.getStartDate()+"', Description='"+workorder.getDescription()+"' WHERE OrderNum='"+workorder.getOrderNum()+"';";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/workOrderView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
