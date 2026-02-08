package com.smjestaj.controller;

import com.smjestaj.dto.ListingFilters;
import com.smjestaj.dto.PageDto;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.service.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.EnumSet;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final RoomService roomService;
    private final ListingService listingService;
    private final FavoriteService favoriteService;
    private final UserService userService;
    private final HomeService homeService;

    @PostMapping("/add/room")
    public String addNewRoomReservation(@RequestParam Long roomId,
                                        @ModelAttribute PageDto pageDto,
                                        @ModelAttribute ListingFilters listingFilters,
                                        Model model) {

        reservationService.addNewRoomReservation(roomId);
        var listingId = roomService.getListingIdByRoomId(roomId);

        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var listingRooms = roomService.getAllRoomsDataOfListing(listingId, statusList);

        model.addAttribute("allRoomsData", listingRooms);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);

        roomService.setReservableForEachRoom(listingRooms);
        model.addAttribute("isFullListingReservable", listingService.isFullListingReservable(listingId));

        return "bookingPage";
    }

    @PostMapping("add/full")
    public String addNewFullReservation(@RequestParam Long listingId,
                                        @RequestParam String returnPage,
                                        @ModelAttribute PageDto pageDto,
                                        @ModelAttribute ListingFilters listingFilters,
                                        Model model) {
        reservationService.addNewFullReservation(listingId);

        var listings = listingService.filterListings(listingFilters, pageDto);
        var favorites = favoriteService.findAllFavoritesOfStudent();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);
        model.addAttribute("favorites", favorites);
        return returnPage;
    }

    @GetMapping("/manage")
    public String manageReservationsForListing(@RequestParam Long listingId, Model model) {
        var userData = userService.getUserData(homeService.getUsernameOfLoggedInUser());
        model.addAttribute("userData", userData);

        var listingData = listingService.getListingById(listingId);
        model.addAttribute("listingData", listingData);

        var statusList = EnumSet.of(ReservationStatus.PENDING, ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        model.addAttribute("allRoomsData", roomService.getAllRoomsDataOfListing(listingId, statusList));
        model.addAttribute("fullReservations", reservationService.getFullListingReservations(listingId));

        return "manageReservations";
    }

    @PostMapping("/accept")
    public String acceptReservation(@RequestParam Long reservationId) {
        reservationService.acceptReservation(reservationId);
        return "redirect:/listings/myListings";
    }

    @GetMapping("/showActive")
    public String showActiveReservationsForListing(@RequestParam Long listingId, Model model) {
        model.addAttribute("userData", userService.getUserData(homeService.getUsernameOfLoggedInUser()));

        var listingData = listingService.getListingById(listingId);
        model.addAttribute("listingData", listingData);

        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        model.addAttribute("allRoomsData", roomService.getAllRoomsDataOfListing(listingId, statusList));
        model.addAttribute("fullReservations", reservationService.getFullListingReservations(listingId));

        return "showActiveReservations";
    }

    @GetMapping("/myReservations")
    public String showMyReservationsPage(Model model) {
        model.addAttribute("listingsWithMyReservations", reservationService.getListingsWithMyReservations());
        return "myReservations";
    }

    @GetMapping("/showBookedRooms")
    public String showBookedRoomsPage(@RequestParam Long listingId, Model model) {
        var reservations = reservationService.getReservationsOfStudentForListing(listingId);
        model.addAttribute("bookedRoomsData", roomService.getBookedRoomsData(listingId, reservations));
        return "bookedRooms";
    }

    @PostMapping("/cancel")
    public String cancelRoomReservation(@RequestParam Long listingId, @RequestParam Long roomId) {
        reservationService.cancelRoomReservation(roomId);
        return "redirect:/reservations/showBookedRooms?listingId=" + listingId;
    }

    @PostMapping("/cancelAllForListing")
    public String cancelMyReservationsForListing(@RequestParam Long listingId) {
        reservationService.cancelMyReservationsForListing(listingId);
        return "redirect:/reservations/myReservations";
    }
}
