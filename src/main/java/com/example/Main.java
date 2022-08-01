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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;


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
    stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS public");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PartDescriptions (PartName varchar(30), PartDescription varchar(255), PRIMARY KEY (PartName))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PartList (PartId serial, PartName varchar(30), PRIMARY KEY (partID), FOREIGN KEY (PartName) REFERENCES PartDescriptions ON DELETE NO ACTION ON UPDATE NO ACTION)"); // this as well as part descriptions were done wrong, with the foreign key not actually being a primary key
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StaffRoles (StaffId serial, Role varchar(30), PRIMARY KEY (StaffId))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Tools (ToolId serial, ToolName varchar(30),PRIMARY KEY (ToolId))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS AddressBook (Address varchar(50), PostalCode varchar(9),PRIMARY KEY (Address) )");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WarrantyClaim (WarrantyID varchar(30), Brand varchar(30), PRIMARY KEY (WarrantyID))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WarrantyDivisions (Brand varchar(30), EmailContact varchar(30), PRIMARY KEY (Brand))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Vendors (VendorName varchar(30), EmailContact varchar(30), BillingShippingAddress varchar(30), PRIMARY KEY (VendorName))");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers (CustIdentifier serial, Name varchar(50), Email varchar(50), PhoneNumber varchar(20), Address varchar(50),PRIMARY KEY (CustIdentifier), FOREIGN KEY (Address) REFERENCES AddressBook ON DELETE SET NULL ON UPDATE CASCADE)");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Workorders (OrderNum serial,CustomerNum integer, ClaimID varchar(30), StartDate varchar(14), EndDate varchar(14), Description varchar(300), PRIMARY KEY (OrderNum), FOREIGN KEY (CustomerNum) REFERENCES Customers ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (ClaimID) REFERENCES WarrantyClaim ON DELETE SET NULL ON UPDATE CASCADE)");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WorkReports (OrderNum integer, StaffID integer, Message varchar(50), Date varchar(14), CloseWorkOrder varchar(10), PRIMARY KEY (OrderNum, Date),FOREIGN KEY (StaffID) REFERENCES StaffRoles ON DELETE NO ACTION ON UPDATE NO ACTION, FOREIGN KEY (OrderNum) REFERENCES WorkOrders ON DELETE CASCADE ON UPDATE CASCADE)"); //there was a mistake in this, adding a foreign key custID that shouldnt have been there, we also hadnt done our cascade right
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ToolUsageReports (Message varchar(30), RepairID integer, ToolID integer, Date varchar(14),Primary Key (ToolID,Message,Date), FOREIGN KEY (RepairID) REFERENCES WorkOrders ON DELETE SET NULL ON UPDATE NO ACTION, FOREIGN KEY (ToolID) REFERENCES Tools ON DELETE NO ACTION ON UPDATE NO ACTION)"); // these needed to be set null when a repair is deleted, since we still need their data (to know if a part has been used)
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PartUsageReports (Message varchar(30), RepairID integer, PartID integer, Date varchar(14),Primary Key (PartID), FOREIGN KEY (RepairID) REFERENCES WorkOrders ON DELETE SET NULL ON UPDATE NO ACTION, FOREIGN KEY (PartID) REFERENCES PartList ON DELETE NO ACTION ON UPDATE NO ACTION)"); // see above, we also needed to set the primary keys for both of them, as parts can only be used once, but tools can be used many times
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StaffDetails (StaffId serial, Name varchar(30), PhoneNumber varchar(30), FOREIGN KEY (StaffId) REFERENCES StaffRoles ON DELETE CASCADE ON UPDATE CASCADE)");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Certifications (CertificationNumber integer, CertificationName varchar(30), DateObtained varchar(30), ExpiryDate varchar(30), TechnicianId integer,PRIMARY KEY (CertificationName, CertificationNumber), FOREIGN KEY (TechnicianID) REFERENCES StaffRoles ON DELETE CASCADE ON UPDATE CASCADE)");
    System.out.println("Boot Successful: Pogrimby"); // we also somehow spelt references wrong a couple times
  }
  catch (Exception e) {
    System.out.println(e);
  }
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping("/ResetDatabase")
  String ResetDatabase() {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("DROP SCHEMA public CASCADE");
      stmt.executeUpdate("CREATE SCHEMA public");
      System.out.println("Wipe Complete: Reinitializing");
      run("");
      return "index";
    }
    catch (Exception e) {
      System.out.println(e);
      return "error";
    }

  }

  @RequestMapping("/statsView")
  String statsView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("select AVG(g.count) from (select count(*) from customers c, workorders w where c.custidentifier=w.customernum group by c.custidentifier) g");
      rs.next();
      model.put("averagerepairnum",rs.getString("avg"));
      rs = stmt.executeQuery("select AVG(g.count) from (select count(*) from staffdetails s, certifications c where s.staffid=c.technicianid group by s.staffid) g");
      rs.next();
      model.put("averagecertificationnum",rs.getString("avg"));
      return "statsview";
    }
    catch (Exception e) {
      System.out.println(e);
      return "error";
    }
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
