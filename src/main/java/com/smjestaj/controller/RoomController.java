package com.smjestaj.controller;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.ListingFilters;
import com.smjestaj.dto.PageDto;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.service.ListingService;
import com.smjestaj.service.RoomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.EnumSet;

@Controller
@RequestMapping("/listingRooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final ListingService listingService;

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

    @GetMapping("/bookingPage")
    public String showBookingPage(@RequestParam Long listingId,
                                  @ModelAttribute ListingFilters listingFilters,
                                  @ModelAttribute PageDto pageDto,
                                  Model model) {

        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var listingRooms = roomService.getAllRoomsDataOfListing(listingId, statusList);
        model.addAttribute("allRoomsData", listingRooms);

        roomService.setReservableForEachRoom(listingRooms);
        model.addAttribute("isFullListingReservable", listingService.isFullListingReservable(listingId));

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);

        return "bookingPage";
    }
}
