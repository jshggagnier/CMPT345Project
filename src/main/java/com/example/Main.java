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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



@Controller
@SpringBootApplication
public class Main implements CommandLineRunner {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  public void run(String... args) throws Exception {
    //initialize all databases!
    try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Workorders (OrderNum serial,CustomerNum integer, ClaimID varchar(30), StartDate varchar(14), EndDate varchar(14), Description varchar(300))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WorkReports (OrderNum integer, StaffID integer, Message varchar(50), Date varchar(14), CloseWorkOrder varchar(10))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WarrantyClaim (WarrantyID varchar(30), Brand varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Vendors (VendorName varchar(30), EmailContact varchar(30), BillingShippingAddress varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers (CustIdentifier serial,Name varchar(50), Email varchar(50), PhoneNumber varchar(20), Address varchar(50))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MakesUseOf (UsageReport varchar(30), RepairID integer, ToolID integer)");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StaffDetails (StaffId serial, Name varchar(30), PhoneNumber varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StaffRoles (StaffId serial, Role varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Tools (ToolId serial, ToolName varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PartList (PartId serial, PartName varchar(30))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PartDescriptions (PartName varchar(30), PartDescription varchar(255))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Certifications (CertificationNumber integer, CertificationName varchar(30), DateObtained varchar(30), ExpiryDate varchar(30), TechnicianId integer)");
    System.out.println("Boot Successful: Pogrymby");
  }
  catch (Exception e) {
    System.out.println(e);
  }
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }
}
