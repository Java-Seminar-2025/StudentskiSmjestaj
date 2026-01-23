package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.dto.ReservationData;
import com.smjestaj.service.ReservationService;

import com.smjestaj.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final RoomService roomService;

    @PostMapping("/add")
    public String addNewReservation(@RequestParam Long roomId,
                                    @ModelAttribute PageDto pageDto,
                                    @ModelAttribute OptionsData optionsData,
                                    Model model) {
        reservationService.addNewReservation(roomId);
        var listingId = roomService.getListingIdByRoomId(roomId);

        model.addAttribute("allRoomsData", roomService.showRoomsOfListing(listingId));
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        return "showRooms";
    }

    @GetMapping("/showPending")
    public String showReservationsForListing(@RequestParam Long listingId, Model model) {
        var listingRooms = roomService.showRoomsOfListing(listingId);
        reservationService.getReservationsForEachRoom(listingRooms);

        model.addAttribute("allRoomsData", listingRooms);
        model.addAttribute("reservationData", ReservationData.builder().build());
        return "reservations";
    }

    @PostMapping("/accept")
    public String acceptReservation(@RequestParam Long reservationId) {
        reservationService.acceptReservation(reservationId);
        return "redirect:/listings/myListings";
    }
}
