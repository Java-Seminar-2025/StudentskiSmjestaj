package com.smjestaj.controller;

import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.dto.ReservationData;
import com.smjestaj.service.FavoriteService;
import com.smjestaj.service.ListingService;
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
    private final ListingService listingService;
    private final FavoriteService favoriteService;

    @PostMapping("/add/room")
    public String addNewRoomReservation(@RequestParam Long roomId,
                                        @ModelAttribute PageDto pageDto,
                                        @ModelAttribute OptionsData optionsData,
                                        Model model) {
        reservationService.addNewRoomReservation(roomId);
        var listingId = roomService.getListingIdByRoomId(roomId);
        var reservations = reservationService.getRoomReservationsOfStudentForListing(listingId);

        model.addAttribute("allRoomsData", roomService.showRoomsOfListing(listingId));
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("reservations", reservations);
        model.addAttribute("hasRoomReservations", !reservations.isEmpty());
        model.addAttribute("hasFullReservation", reservationService.hasFullListingReservation(listingId));
        return "showRooms";
    }

    @PostMapping("add/full")
    public String addNewFullReservation(@RequestParam Long listingId,
                                        @ModelAttribute PageDto pageDto,
                                        @ModelAttribute OptionsData optionsData,
                                        Model model) {
        reservationService.addNewFullReservation(listingId);

        var listings = listingService.filterListings(optionsData, pageDto);
        var favorites = favoriteService.findAllFavoritesOfStudent();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("favorites", favorites);
        return "listings";
    }

    @GetMapping("/showPending")
    public String showReservationsForListing(@RequestParam Long listingId, Model model) {
        var listingData = listingService.getListingById(listingId);

        var listingRooms = roomService.showRoomsOfListing(listingId);
        reservationService.getReservationsForEachRoom(listingRooms);

        var fullReservations = reservationService.getFullReservationsForListing(listingId);

        model.addAttribute("allRoomsData", listingRooms);
        model.addAttribute("fullReservations", fullReservations);
        model.addAttribute("listingData", listingData);
        return "reservations";
    }

    @PostMapping("/accept")
    public String acceptReservation(@RequestParam Long reservationId) {
        reservationService.acceptReservation(reservationId);
        return "redirect:/listings/myListings";
    }
}
