package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.EmployeeDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;
import ru.sibdigital.addcovid.parser.ExcelWriter;
import ru.sibdigital.addcovid.repository.ClsExcelRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Log4j2
@Controller
public class FileDownloadController {

    @Autowired
    ExcelWriter excelWriter;

    @Autowired
    RequestService requestService;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    private static final int BUFFER_SIZE = 4096;

    @RequestMapping("/download_employees")
    public String forwardUpload(HttpSession session, HttpServletResponse response) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        try {
            ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
            List<DocEmployee> employees = requestService.getEmployeesByOrganizationId(organization.getId());
            File downloadFile = excelWriter.createEmployeesFile(employees);

            FileInputStream inputStream = new FileInputStream(downloadFile);
            response.setContentLength((int) downloadFile.length());

            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", "Employees.xlsx");
            response.setHeader(headerKey, headerValue);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // get output stream of the response
            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            // write bytes read from the input stream into the output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outStream.close();
        }
        catch (Exception e) {

        }
        return "";
    }




}
