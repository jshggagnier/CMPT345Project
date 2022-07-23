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
public class VendorController {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
  
    @GetMapping("/vendorSubmit")
    String vendorSubmit(Map<String, Object> model) {
        Vendor vendor = new Vendor();
      model.put("Vendor", vendor);
      return "vendorSubmit";
    }

    @GetMapping("/vendorView")
  String vendorView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Vendors"));
      ArrayList<Vendor> dataList = new ArrayList<Vendor>();
      while (rs.next()) {
        Vendor obj = new Vendor();
        obj.setVendorName(rs.getString("VendorName"));
        obj.setEmailContact(rs.getString("EmailContact"));
        obj.setBillingShippingAddress(rs.getString("BillingShippingAddress"));
        dataList.add(obj);
      }
      model.put("Vendors", dataList);
      return "vendorView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

    @PostMapping(path = "/vendorSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String handlevendorSubmit(Map<String, Object> model,Vendor Vendor) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO Vendors (VendorName, EmailContact, BillingShippingAddress) VALUES ('"+Vendor.getVendorName()+"', '"+Vendor.getEmailContact()+"', '"+Vendor.getBillingShippingAddress()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/vendorView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
