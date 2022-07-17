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
    return "workOrderSubmit";
  }

  @GetMapping("/workOrderView")
  String viewWorkOrders(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(50), startdate DATE, enddate DATE, teams varchar(500), fundinginformation varchar(100))");
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<Workorder> dataList = new ArrayList<Workorder>();
      while (rs.next()) {
        Workorder obj = new Workorder();
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));

        dataList.add(obj);
      }
      model.put("WorkItems", dataList);
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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Workorders (OrderNum serial,CustomerNum integer, ClaimID integer, StartDate varchar(10), EndDate varchar(10), Description varchar(300),CustomerName varchar(100))");
      String sql = "INSERT INTO Workorders (CustomerNum,ClaimID,StartDate,Description,CustomerName) VALUES (1, '"+workorder.getClaimID()+"', '"+workorder.getStartDate()+"', '"+workorder.getDescription()+"', '"+workorder.getCustomerName()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/workOrderView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }
}
