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
public class PartController {
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
  
    @GetMapping("/partSubmit")
    String partSubmit(Map<String, Object> model) {
        Part part = new Part();
      model.put("Part", part);
      return "partSubmit";
    }

    @GetMapping("/partView")
    String PartView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs1 = stmt.executeQuery(("SELECT * FROM PartList"));
      ArrayList<Part> dataList = new ArrayList<Part>();
      while (rs1.next()) {
        Part obj = new Part();
        obj.setPartID(rs1.getInt("PartId"));
        obj.setPartName(rs1.getString("PartName"));       
        dataList.add(obj);
      }
      ResultSet rs2 = stmt.executeQuery(("SELECT * FROM PartDescriptions"));
      int x = 0;
      while(rs2.next())
      {
        dataList.get(x).setPartDescription(rs2.getString("PartDescription"));
        x++;
      }
      model.put("Parts", dataList);
      return "partView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/PartSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleStaffSubmit(Map<String, Object> model,Part Part) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql1 = "INSERT INTO PartList (PartName) VALUES ('"+Part.getPartName()+"')";
      String sql2 = "INSERT INTO PartDescriptions (PartName, PartDescription) VALUES ('"+Part.getPartName()+"', '"+Part.getPartDescription()+"')";
      System.out.println(sql2);
      stmt.executeUpdate(sql2);
      System.out.println(sql1);
      stmt.executeUpdate(sql1);
      return "redirect:/partView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

    
}
