package com.smjestaj.service;

import com.smjestaj.dto.BlockOrUnblockDto;
import com.smjestaj.dto.ChangeRoleDto;
import com.smjestaj.enums.UserRole;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ListingService listingService;
    private final ListingRepository listingRepository;

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
            listings.forEach(listing -> {
                listingService.deleteListing(listing.getId());
            });
        }
    }
}
