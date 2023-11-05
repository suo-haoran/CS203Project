package com.cs203g3.ticketing.activeBallotCategory;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.activeBallotCategory.dto.ActiveBallotCategoryRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1")
public class ActiveBallotCategoryController {

    private ActiveBallotCategoryService abcService;

    public ActiveBallotCategoryController(ActiveBallotCategoryService abcs) {
        this.abcService = abcs;
    }

    @GetMapping("/activeBallots")
    public List<ActiveBallotCategory> getAllActiveBallotCategories() {
        return abcService.getAllActiveBallotCategories();
    }

    @PostMapping("/concerts/{concertId}/categories/{categoryId}/activeBallots")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ActiveBallotCategory addActiveBallotCategory(
        @PathVariable Long concertId, @PathVariable Long categoryId,
        @Valid @RequestBody ActiveBallotCategoryRequestDto reqBody) {
        return abcService.addActiveBallotCategory(concertId, categoryId, reqBody);
    }

    @DeleteMapping("/concerts/{concertId}/categories/{categoryId}/activeBallots")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteActiveBallotCategory(@PathVariable Long concertId, @PathVariable Long categoryId) {
        abcService.handleScheduledTasksAndDeleteActiveBallotCategory(concertId, categoryId);
    }
}
