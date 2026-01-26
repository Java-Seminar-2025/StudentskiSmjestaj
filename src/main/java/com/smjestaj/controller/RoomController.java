package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.service.HomeService;
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
    private final HomeService homeService;

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

        model.addAttribute("allRoomsData", roomService.getRoomsOfListing(listingId));
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);

        var roomReservationsOfStudent = reservationService.getReservationsOfStudent(listingId, ReservationType.ROOM);
        model.addAttribute("roomReservationsOfStudent", roomReservationsOfStudent);
        model.addAttribute("hasRoomReservations", !roomReservationsOfStudent.isEmpty());

        model.addAttribute("hasFullReservation",
                !reservationService.getReservationsOfStudent(listingId, ReservationType.FULL_LISTING).isEmpty());

        return "showRooms";
    }
}
