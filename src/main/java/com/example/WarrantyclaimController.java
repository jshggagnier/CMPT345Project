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
public class WarrantyclaimController {
    
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/warrantyClaimSubmit")
  String warrantyClaimSubmit(Map<String, Object> model) {
    Warrantyclaim warrantyclaim = new Warrantyclaim();
    model.put("WarrantyClaim", warrantyclaim);
    return "warrantyClaimSubmit";
  }

  @GetMapping("/warrantyClaimView")
  String WarrantyDivisionView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM WarrantyClaim"));
      ArrayList<Warrantyclaim> dataList = new ArrayList<Warrantyclaim>();
      while (rs.next()) {
        Warrantyclaim obj = new Warrantyclaim();
        obj.setWarrantyID(rs.getString("WarrantyID"));
        obj.setBrand(rs.getString("Brand"));
        dataList.add(obj);
      }
      model.put("WarrantyClaims", dataList);
      return "warrantyClaimView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/warrantyClaimSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handlewarrantyClaimSubmit(Map<String, Object> model,Warrantyclaim WarrantyClaim) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO WarrantyClaim (WarrantyID, Brand) VALUES ('"+WarrantyClaim.getWarrantyID()+"', '"+WarrantyClaim.getBrand()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/warrantyClaimView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
