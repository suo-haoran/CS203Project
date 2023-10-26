package com.cs203g3.ticketing.ballot;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.ballot.dto.BallotResponseDto;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;


@RestController
@RequestMapping("/v1/sessions/{concertSessionId}/categories/{categoryId}/ballots")
public class BallotController {

    private BallotService ballotService;

    public BallotController(BallotService bs) {
        this.ballotService = bs;
    }

    @GetMapping
    public List<BallotResponseDto> getAllBallotsByConcertSessionIdAndCategoryId(@PathVariable Long concertSessionId, @PathVariable Long categoryId) {
        return ballotService.getAllBallotsByConcertSessionIdAndCategoryId(concertSessionId, categoryId);
    }

    @PostMapping
    @ResponseStatus(code=HttpStatus.CREATED)
    public BallotResponseDto addBallotAsCurrentUser(Authentication auth, @PathVariable Long concertSessionId, @PathVariable Long categoryId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ballotService.addBallot(userDetails, concertSessionId, categoryId);
    }

    @PostMapping("/randomise")
    public void randomiseBallotsByConcertIdAndCategoryId(@PathVariable Long concertSessionId, @PathVariable Long categoryId) {
        ballotService.randomiseBallotForConcertSessionIdAndCategoryId(concertSessionId, categoryId);
    }
}
