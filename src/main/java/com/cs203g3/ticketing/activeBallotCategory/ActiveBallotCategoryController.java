package com.cs203g3.ticketing.activeBallotCategory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts/{concertId}/categories/{categoryId}/activeBallots")
public class ActiveBallotCategoryController {

    private ActiveBallotCategoryService abcService;

    public ActiveBallotCategoryController(ActiveBallotCategoryService abcs) {
        this.abcService = abcs;
    }

    @PostMapping
    public ActiveBallotCategory addActiveBallotCategory(@PathVariable Long concertId, @PathVariable Long categoryId) {
        return abcService.addActiveBallotCategory(concertId, categoryId);
    }
}
