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
public class StaffController {
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
  
    @GetMapping("/staffSubmit")
    String staffSubmit(Map<String, Object> model) {
        Staff staff = new Staff();
      model.put("Staff", staff);
      return "staffSubmit";
    }

    @GetMapping("/staffView")
    String StaffView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT sd.staffid,sd.Name,sd.phonenumber,sr.Role,count(c.CertificationName) FROM staffdetails sd,staffroles sr,certifications c WHERE sd.staffid=sr.staffid AND sd.staffid = c.technicianid AND EXISTS(select c.certificationname from certifications c except (select c.certificationname from certifications c where c.technicianid=sd.staffid)) GROUP BY (sd.staffid,sd.phonenumber,sd.Name,sr.Role)"));
      ArrayList<TableArray> dataList = new ArrayList<TableArray>();
      while (rs.next()) {
        TableArray obj = new TableArray();
        obj.setFirstString(rs.getString("staffid"));
        obj.setSecondString(rs.getString("name"));
        obj.setThirdString(rs.getString("phonenumber"));
        obj.setFourthString(rs.getString("role"));
        obj.setFifthString(rs.getString("count"));
        dataList.add(obj);
      }
      model.put("UncertifiedStaff", dataList);
      ResultSet rs3 = stmt.executeQuery(("SELECT sd.staffid,sd.Name,sd.phonenumber,sr.Role,count(c.CertificationName) FROM staffdetails sd,staffroles sr,certifications c WHERE sd.staffid=sr.staffid AND sd.staffid = c.technicianid AND NOT EXISTS(select c.certificationname from certifications c except (select c.certificationname from certifications c where c.technicianid=sd.staffid)) GROUP BY (sd.staffid,sd.phonenumber,sd.Name,sr.Role)"));
      ArrayList<TableArray> dataList3 = new ArrayList<TableArray>();
      while (rs3.next()) {
        TableArray obj = new TableArray();
        obj.setFirstString(rs3.getString("staffid"));
        obj.setSecondString(rs3.getString("name"));
        obj.setThirdString(rs3.getString("phonenumber"));
        obj.setFourthString(rs3.getString("role"));
        obj.setFifthString(rs3.getString("count"));
        dataList3.add(obj);
      }
      model.put("FullyCertifiedStaff", dataList3);
      return "staffView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/StaffSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleStaffSubmit(Map<String, Object> model,Staff Staff) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql1 = "INSERT INTO StaffDetails (Name, PhoneNumber) VALUES ('"+Staff.getName()+"', '"+Staff.getPhoneNumber()+"')";
      String sql2 = "INSERT INTO StaffRoles (Role) VALUES ('"+Staff.getRole()+"')";
      System.out.println(sql2);
      stmt.executeUpdate(sql2);
      System.out.println(sql1);
      stmt.executeUpdate(sql1);
      return "redirect:/staffView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

    
}
