package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.service.RoomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/listingRooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/create")
    public String showListingRoomPage(@RequestParam Long listingId, Model model) {
        model.addAttribute("allRoomsData", roomService.prepareRoomForms(listingId));
        return "createRooms";
    }

    @PostMapping("/create")
    public String addListingRooms(@ModelAttribute AllRoomsData allRoomsData) {
        roomService.addListingRooms(allRoomsData);
        return "redirect:/home";
    }
}
