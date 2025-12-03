package com.smjestaj.controller;

import com.smjestaj.dto.*;
import com.smjestaj.enums.UserRole;
import com.smjestaj.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/userList")
    public String showUserList(Model model) {
        List<SafeUserData> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("changeRoleDto", new ChangeRoleDto());
        model.addAttribute("blockOrUnblockDto", new BlockOrUnblockDto());
        return "userList";
    }

    @GetMapping("/studentList")
    public String showStudentList(Model model) {
        List<SafeUserData> students = userService.getAllUsersWithRole(UserRole.STUDENT);
        model.addAttribute("students", students);
        model.addAttribute("changeRoleDto", new ChangeRoleDto());
        model.addAttribute("blockOrUnblockDto", new BlockOrUnblockDto());
        return "studentList";
    }
/*
    @GetMapping("/landlordList")
    public String showLandlordList(Model model) {
        List<SafeUserData> landlords = userService.getAllUsersWithRole(UserRole.LANDLORD);
        model.addAttribute("landlords", landlords);
        return "landlordList";
    }

    @GetMapping("/adminList")
    public String showAdminList(Model model) {
        List<SafeUserData> admins = userService.getAllUsersWithRole(UserRole.ADMIN);
        model.addAttribute("admins", admins);
        return "adminList";
    }
*/
    @PostMapping("/changeRole")
    public String changeUserRole(@ModelAttribute ChangeRoleDto changeRoleDto) {
        userService.changeUserRole(changeRoleDto);
        return "redirect:/admin/userList";
    }

    @PostMapping("/blockOrUnblockUser")
    public String blockOrUnblockUser(@ModelAttribute BlockOrUnblockDto blockOrUnblockDto) {
        userService.blockOrUnblockUser(blockOrUnblockDto);
        return "redirect:/admin/userList";
    }
}
