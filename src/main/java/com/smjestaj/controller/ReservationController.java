package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
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
    public String addNewReservation(@RequestParam Long roomId) {
        reservationService.addNewReservation(roomId);
        var listingId = roomService.getListingIdByRoomId(roomId);
        return "redirect:/listingRooms/show?listingId=" + listingId;
    }
}
