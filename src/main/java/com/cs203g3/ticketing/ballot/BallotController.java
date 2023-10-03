package com.cs203g3.ticketing.ballot;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.security.auth.UserDetailsImpl;


@RestController
@RequestMapping("/api/concerts/{concertId}/categories/{categoryId}/ballots")
public class BallotController {

    private BallotService ballotService;

    public BallotController(BallotService bs) {
        this.ballotService = bs;
    }

    @PostMapping
    public Ballot addBallotAsCurrentUser(Authentication auth, @PathVariable Long concertId, @PathVariable Long categoryId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ballotService.addBallot(userDetails, concertId, categoryId);
    }
}
