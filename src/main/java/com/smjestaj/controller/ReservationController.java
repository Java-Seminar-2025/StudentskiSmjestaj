package com.smjestaj.controller;

import com.smjestaj.dto.ListingFilters;
import com.smjestaj.dto.PageDto;
import com.smjestaj.enums.ReservationStatus;
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
    private final UserService userService;
    private final HomeService homeService;

    @PostMapping("/add/room")
    public String addNewRoomReservation(@RequestParam Long roomId,
                                        @ModelAttribute PageDto pageDto,
                                        @ModelAttribute ListingFilters listingFilters,
                                        Model model) {

        reservationService.addNewRoomReservation(roomId);
        var listingId = roomService.getListingIdByRoomId(roomId);
        var listingRooms = roomService.getAllRoomsDataOfListing(listingId, ReservationStatus.ACTIVE);

        model.addAttribute("allRoomsData", listingRooms);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);

        roomService.setReservableForEachRoom(listingRooms);
        model.addAttribute("isFullListingReservable", listingService.isFullListingReservable(listingId));

        return "bookingPage";
    }

    @PostMapping("add/full")
    public String addNewFullReservation(@RequestParam Long listingId,
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
        return "listings";
    }

    @GetMapping("/showPending")
    public String showPendingReservationsForListing(@RequestParam Long listingId, Model model) {
        var listingData = listingService.getListingById(listingId);
        model.addAttribute("listingData", listingData);

        model.addAttribute("allRoomsData", roomService.getAllRoomsDataOfListing(listingId, ReservationStatus.PENDING));
        model.addAttribute("fullReservations", reservationService.getFullListingReservations(listingId));
        model.addAttribute("userData", userService.getUserData(homeService.getUsernameOfLoggedInUser()));

        return "manageReservations";
    }

    @PostMapping("/accept")
    public String acceptReservation(@RequestParam Long reservationId) {
        reservationService.acceptReservation(reservationId);
        return "redirect:/listings/myListings";
    }

    @GetMapping("/showActive")
    public String showActiveReservationsForListing(@RequestParam Long listingId, Model model) {
        var listingData = listingService.getListingById(listingId);
        model.addAttribute("listingData", listingData);

        model.addAttribute("allRoomsData", roomService.getAllRoomsDataOfListing(listingId, ReservationStatus.ACTIVE));
        model.addAttribute("fullReservations", reservationService.getFullListingReservations(listingId));
        model.addAttribute("userData", userService.getUserData(homeService.getUsernameOfLoggedInUser()));

        return "showActiveReservations";
    }

    @GetMapping("/myReservations")
    public String showMyReservationsPage(Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).build();

        var reservationList = reservationService.getMyReservationsPage(pageDto);
        pageDto = pageDto.toBuilder().totalPages(reservationList.getTotalPages()).build();

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("reservationList", reservationList);
        return "myReservations";
    }

    @PostMapping("/changePage")
    public String changeMyReservationsPage(@ModelAttribute PageDto pageDto,
                                           @RequestParam String action,
                                           Model model) {

        pageDto = listingService.changePage(pageDto, action);
        var reservationList = reservationService.getMyReservationsPage(pageDto);

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("reservationList", reservationList);

        return "myReservations";
    }
}
