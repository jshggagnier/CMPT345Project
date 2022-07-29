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
public class ToolController {
    
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/toolSubmit")
  String toolSubmit(Map<String, Object> model) {
    Tool tool = new Tool();
    model.put("Tool", tool);

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Tools"));
      ArrayList<Tool> dataList = new ArrayList<Tool>();
      while (rs.next()) {
        Tool obj = new Tool();
        obj.setToolID(rs.getInt("ToolId"));
        obj.setToolName(rs.getString("ToolName"));
        dataList.add(obj);
      }
      model.put("Tools", dataList);
      return "toolSubmit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/toolView")
  String viewTools(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Tools"));
      ArrayList<Tool> dataList = new ArrayList<Tool>();
      while (rs.next()) {
        Tool obj = new Tool();
        obj.setToolID(rs.getInt("ToolId"));
        obj.setToolName(rs.getString("ToolName"));
        dataList.add(obj);
      }
      model.put("Tools", dataList);
      return "toolView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/toolSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleWorkorderSubmit(Map<String, Object> model,Tool Tool) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO Tools (ToolName) VALUES ('"+Tool.getToolName()+"')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/toolView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/ToolEdit/{nid}")
  String LoadFormWorkOrderEdit(Map<String, Object> model, @PathVariable String nid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Tools WHERE ToolId = " + nid)); // this should only ever run once, since OrderNum is serial
      Tool tool = new Tool();
      while (rs.next()) {
        tool.setToolID(rs.getInt("ToolId"));
        tool.setToolName(rs.getString("ToolName"));
      }
      model.put("Tool", tool);
      return "toolEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/toolEditForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleWorkorderEdit(Map<String, Object> model,Tool Tool) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Tools (ToolId serial, ToolName varchar(30))");
      String sql = "UPDATE Tools SET ToolName='"+Tool.getToolName()+"';";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:toolView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }
}
