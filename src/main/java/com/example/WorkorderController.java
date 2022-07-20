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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers (CustIdentifier serial,Name varchar(50), Email varchar(50), PhoneNumber varchar(20), Address varchar(50))");
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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Workorders (OrderNum serial,CustomerNum integer, ClaimID integer, StartDate varchar(10), EndDate varchar(10), Description varchar(300))");
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Workorders"));
      ArrayList<Workorder> dataList = new ArrayList<Workorder>();
      while (rs.next()) {
        Workorder obj = new Workorder();
        obj.setOrderNum(rs.getInt("OrderNum"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setClaimID(rs.getString("ClaimID"));
        obj.setDescription(rs.getString("Description"));
        obj.setCustomerNum(rs.getInt("CustomerNum"));
        dataList.add(obj);
      }
      model.put("WorkOrders", dataList);
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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers (CustIdentifier serial,Name varchar(50), Email varchar(50), PhoneNumber varchar(20), Address varchar(50))");
      //by now we have verified the customer exists
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Workorders (OrderNum serial,CustomerNum integer, ClaimID integer, StartDate varchar(10), EndDate varchar(10), Description varchar(300))");
      String sql = "INSERT INTO Workorders (CustomerNum,ClaimID,StartDate,Description) VALUES ("+workorder.getCustomerNum()+", '"+workorder.getClaimID()+"', '"+workorder.getStartDate()+"', '"+workorder.getDescription()+"')";
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
      Workorder obj = new Workorder();
      while (rs.next()) {
        obj.setOrderNum(rs.getInt("OrderNum"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setClaimID(rs.getString("ClaimID"));
        obj.setDescription(rs.getString("Description"));
        obj.setCustomerNum(rs.getInt("CustomerNum"));
      }
      model.put("WorkOrder", obj);
      return "WorkOrderEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
