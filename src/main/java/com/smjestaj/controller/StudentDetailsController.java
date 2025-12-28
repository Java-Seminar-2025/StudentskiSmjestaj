package com.smjestaj.controller;

import com.smjestaj.dto.StudentData;
import com.smjestaj.service.StudentDetailsService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class StudentDetailsController {
    private final StudentDetailsService studentDetailsService;

    @GetMapping("/studentDetails")
    public String showStudentDetailsPage(@RequestParam Long studentId, Model model) {
        var studentData = StudentData.builder().build();
        studentData = studentData.toBuilder().studentId(studentId).build();
        model.addAttribute("studentData", studentData);
        return "studentDetails";
    }

    @PostMapping("/studentDetails")
    public String addStudentDetails(@ModelAttribute StudentData studentData) {
        studentDetailsService.addStudentDetails(studentData);
        return "redirect:/user/login";
    }
}
