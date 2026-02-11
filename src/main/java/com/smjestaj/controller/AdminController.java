package com.smjestaj.controller;

import com.smjestaj.dto.*;
import com.smjestaj.enums.UserRole;
import com.smjestaj.service.AdminService;
import com.smjestaj.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/userList")
    public String showUserList(Model model) {
        var users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("changeRoleDto", ChangeRoleDto.builder().build());
        model.addAttribute("blockOrUnblockDto", BlockOrUnblockDto.builder().build());
        return "userList";
    }

    @PostMapping("/changeRole")
    public String changeUserRole(@ModelAttribute ChangeRoleDto changeRoleDto) {
        adminService.changeUserRole(changeRoleDto);
        return "redirect:/admin/userList";
    }

    @PostMapping("/blockOrUnblockUser")
    public String blockOrUnblockUser(@ModelAttribute BlockOrUnblockDto blockOrUnblockDto) {
        adminService.blockOrUnblockUser(blockOrUnblockDto);
        return "redirect:/admin/userList";
    }
}
