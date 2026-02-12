package com.smjestaj.service;

import com.smjestaj.dto.BlockOrUnblockDto;
import com.smjestaj.dto.ChangeRoleDto;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.UserRole;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ListingService listingService;
    private final ListingRepository listingRepository;
    private final ReservationService reservationService;
    private final FavoriteService favoriteService;
    private final OccupancyService occupancyService;

    public void changeUserRole(ChangeRoleDto changeRoleDto) {
        var user = userRepository.findByUsername(changeRoleDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setRole(changeRoleDto.role());
        userRepository.save(user);
    }

    public void blockOrUnblockUser(BlockOrUnblockDto blockOrUnblockDto) {
        var user = userRepository.findByUsername(blockOrUnblockDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setBlocked(!blockOrUnblockDto.blocked());
        userRepository.save(user);

        if(user.getRole().equals(UserRole.LANDLORD) && (user.getBlocked())) {
            var listings = listingRepository.findAllByLandlordAndDeleted(user, false);
            var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
            listings.forEach(listing -> {
                listingService.deleteListing(listing.getId());
                reservationService.cancelAllReservationsForDeletedListing(listing.getId());
                favoriteService.unfavoriteDeletedListing(listing.getId());
                occupancyService.updateListingStatus(listing.getId(), reservationService.getReservationsForListing(listing.getId(), statusList));
            });
        }
    }
}
