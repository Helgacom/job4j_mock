package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.service.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final ProfilesService profilesService;
    private final TopicsService topicsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            model.addAttribute("categories", categoriesService.getMostPopular());
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        var newInterviews = interviewsService.getByType(1);
        List<ProfileDTO> userList = newInterviews.stream()
                .map(i -> profilesService.getProfileById(i.getSubmitterId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Map<Integer, Integer> sizeCategoryInterview = new HashMap<>();
        for (InterviewDTO interview : newInterviews) {
            int categoryId = topicsService.getById(interview.getTopicId()).getCategory().getId();
            Integer count = sizeCategoryInterview.get(categoryId);
            sizeCategoryInterview.put(categoryId, count == null ? 1 : ++count);
        }
        model.addAttribute("sizeCategory", sizeCategoryInterview);
        model.addAttribute("new_interviews", newInterviews);
        model.addAttribute("users", userList);
        return "index";
    }
}