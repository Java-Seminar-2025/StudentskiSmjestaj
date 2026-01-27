package com.smjestaj.controller;

import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.service.*;

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
        var listingRooms = roomService.getRoomsOfListing(listingId);

        model.addAttribute("allRoomsData", listingRooms);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);

        roomService.setReservableForEachRoom(listingRooms);
        model.addAttribute("isFullListingReservable", listingService.isFullListingReservable(listingId));
/*
        var roomReservationsOfStudent = reservationService.getReservationsOfStudent(listingId, ReservationType.ROOM);
        model.addAttribute("roomReservationsOfStudent", roomReservationsOfStudent);
        model.addAttribute("hasRoomReservations", !roomReservationsOfStudent.isEmpty());

        model.addAttribute("hasFullReservation",
                !reservationService.getReservationsOfStudent(listingId, ReservationType.FULL_LISTING).isEmpty());
*/
        return "bookingPage";
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
    public String showPendingReservationsForListing(@RequestParam Long listingId, Model model) {
        var listingData = listingService.getListingById(listingId);
        model.addAttribute("listingData", listingData);

        model.addAttribute("allRoomsData", roomService.getRoomsOfListing(listingId));
        model.addAttribute("fullReservations", reservationService.getFullListingReservations(listingId));

        return "reservations";
    }

    @PostMapping("/accept")
    public String acceptReservation(@RequestParam Long reservationId) {
        reservationService.acceptReservation(reservationId);
        return "redirect:/listings/myListings";
    }
}
