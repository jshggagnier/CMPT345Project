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
public class CertificationController {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/certificationSubmit")
  String certificationSubmit(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT Name,StaffId FROM StaffDetails"));
      ArrayList<Staff> dataList = new ArrayList<Staff>();
      while (rs.next()) {
        Staff obj = new Staff();
        obj.setName(rs.getString("Name"));
        obj.setStaffId(rs.getInt("StaffId"));
        dataList.add(obj);
      }
      model.put("Technicians", dataList);
      Certification certification = new Certification();
      model.put("Certification", certification);
      return "certificationSubmit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/certificationView")
  String CertificationView(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Certifications"));
      ArrayList<Certification> dataList = new ArrayList<Certification>();
      while (rs.next()) {
        Certification obj = new Certification();
        obj.setExpiryDate(rs.getString("ExpiryDate"));
        obj.setCertificationNumber(rs.getInt("CertificationNumber"));
        obj.setDateObtained(rs.getString("DateObtained"));
        obj.setCertificationName(rs.getString("CertificationName"));
        obj.setTechnicianId(rs.getInt("TechnicianId"));
        dataList.add(obj);
      }
      model.put("Certifications", dataList);
      return "certificationView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/certificationSubmitForm", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handlecertificationSubmit(Map<String, Object> model,Certification Certification) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO Certifications (CertificationNumber, CertificationName, DateObtained, ExpiryDate, TechnicianId) VALUES ("+Certification.getCertificationNumber()+", '"+Certification.getCertificationName()+"', '"+Certification.getDateObtained()+"', '"+Certification.getExpiryDate()+"', "+Certification.getTechnicianId()+")";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      return "redirect:/certificationView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }
    
}
