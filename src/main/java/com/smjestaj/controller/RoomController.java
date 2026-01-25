package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.service.ReservationService;
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
    private final ReservationService reservationService;

    @GetMapping("/create")
    public String showCreateRoomsPage(@RequestParam Long listingId, Model model) {
        model.addAttribute("allRoomsData", roomService.prepareRoomForms(listingId));
        return "createRooms";
    }

    @PostMapping("/create")
    public String addListingRooms(@ModelAttribute AllRoomsData allRoomsData) {
        roomService.addListingRooms(allRoomsData);
        return "redirect:/home";
    }

    @GetMapping("/show")
    public String showRoomsOfListing(@RequestParam Long listingId,
                                     @ModelAttribute OptionsData optionsData,
                                     @ModelAttribute PageDto pageDto,
                                     Model model) {
        var reservations = reservationService.getRoomReservationsOfStudentForListing(listingId);

        model.addAttribute("allRoomsData", roomService.showRoomsOfListing(listingId));
        model.addAttribute("reservations", reservations);
        model.addAttribute("hasRoomReservations", !reservations.isEmpty());
        model.addAttribute("hasFullReservation", reservationService.hasFullListingReservation(listingId));
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("pageDto", pageDto);
        return "showRooms";
    }
}
