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
public class WarrantydivisionController {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/warrantyDivisionSubmit")
  String warrantyDivisionSubmit(Map<String, Object> model) {
    Warrantydivision warrantydivision = new Warrantydivision();
    model.put("WarrantyDivision", warrantydivision);
    return "warrantyDivisionSubmit";
  }

  @GetMapping("/warrantyDivisionView")
  String WarrantyDivisionView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM WarrantyDivisions"));
      ArrayList<Warrantydivision> dataList = new ArrayList<Warrantydivision>();
      while (rs.next()) {
        Warrantydivision obj = new Warrantydivision();
        obj.setBrand(rs.getString("Brand"));
        obj.setEmailContact(rs.getString("EmailContact"));
        dataList.add(obj);
      }
      model.put("WarrantyDivisions", dataList);
      return "warrantyDivisionView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/warrantyDivisionSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handlewarrantyDivisionSubmit(Map<String, Object> model,Warrantydivision WarrantyDivision) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO WarrantyDivisions (Brand, EmailContact) VALUES ('"+WarrantyDivision.getBrand()+"', '"+WarrantyDivision.getEmailContact()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/warrantyDivisionView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

}
