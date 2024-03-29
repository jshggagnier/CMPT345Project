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
public class CustomerController {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/customerSubmit")
  String customerSubmit(Map<String, Object> model) {
    Customer Customer = new Customer();
    model.put("Customer", Customer);
    return "customerSubmit";
  }

  @GetMapping("/customerView")
  String viewcustomers(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM customers c,AddressBook a where c.address=a.address"));
      ArrayList<Customer> dataList = new ArrayList<Customer>();
      while (rs.next()) {
        Customer obj = new Customer();
        obj.setCustIdentifier(rs.getInt("CustIdentifier"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setPhoneNumber(rs.getString("PhoneNumber"));
        obj.setPostalCode(rs.getString("PostalCode"));
        obj.setAddress(rs.getString("Address"));
        dataList.add(obj);
      }
      model.put("Customers", dataList);
      connection.close();
      return "customerView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/customerSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handlecustomerSubmit(Map<String, Object> model,Customer Customer) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql1 = "INSERT INTO customers (Name, Email, PhoneNumber, Address) VALUES ('"+Customer.getName()+"', '"+Customer.getEmail()+"', '"+Customer.getPhoneNumber()+"', '"+Customer.getAddress()+"')";
      String sql2 = "INSERT INTO AddressBook (Address, PostalCode) VALUES ('"+Customer.getAddress()+"', '"+Customer.getPostalCode()+"') ON CONFLICT (Address) DO UPDATE SET PostalCode = EXCLUDED.PostalCode";
      System.out.println(sql2);
      stmt.executeUpdate(sql2);
      System.out.println(sql1);
      stmt.executeUpdate(sql1);
      connection.close();
      return "redirect:/customerView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/CustomerDelete/{nid}")
  String LoadFormWorkOrderDelete(Map<String, Object> model, @PathVariable String nid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      
      String SQL = ("Delete FROM customers WHERE CustIdentifier = " + nid);
      System.out.println(SQL); // this should only ever print 1, please...
      stmt.executeUpdate(SQL);
      return "redirect:/customerView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
