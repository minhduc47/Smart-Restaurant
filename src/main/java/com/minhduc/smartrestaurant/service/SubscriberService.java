package com.minhduc.smartrestaurant.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.Subscriber;
import com.minhduc.smartrestaurant.domain.request.ReqSubscriberDTO;
import com.minhduc.smartrestaurant.domain.response.ResSubscriberDTO;
import com.minhduc.smartrestaurant.domain.response.email.ResEmailDish;
import com.minhduc.smartrestaurant.repository.CategoryRepository;
import com.minhduc.smartrestaurant.repository.SubscriberRepository;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final CategoryRepository categoryRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, CategoryRepository categoryRepository,
            EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.categoryRepository = categoryRepository;
        this.emailService = emailService;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findById(id);
        if (subscriberOptional.isPresent()) {
            return subscriberOptional.get();
        }
        return null;
    }

    public ResSubscriberDTO handleCreateSubscriber(ReqSubscriberDTO requestSubscriber) throws IdInvalidException {
        Subscriber subscriber = new Subscriber();
        subscriber.setName(requestSubscriber.getName());
        subscriber.setEmail(requestSubscriber.getEmail());
        subscriber.setCategories(this.mapCategoriesFromCategoryIds(requestSubscriber.getCategoryIds()));
        subscriber.setActive(true);

        Subscriber savedSubscriber = this.subscriberRepository.save(subscriber);
        return this.convertToSubscriberResponseDTO(savedSubscriber);
    }

    public ResSubscriberDTO updateSubscriber(Subscriber currentSubscriber, ReqSubscriberDTO requestSubscriber)
            throws IdInvalidException {
        currentSubscriber.setName(requestSubscriber.getName());
        currentSubscriber.setEmail(requestSubscriber.getEmail());
        currentSubscriber.setCategories(this.mapCategoriesFromCategoryIds(requestSubscriber.getCategoryIds()));

        Subscriber updatedSubscriber = this.subscriberRepository.save(currentSubscriber);
        return this.convertToSubscriberResponseDTO(updatedSubscriber);
    }

    public ResSubscriberDTO convertToSubscriberResponseDTO(Subscriber subscriber) {
        ResSubscriberDTO responseDTO = new ResSubscriberDTO();
        responseDTO.setId(subscriber.getId());
        responseDTO.setEmail(subscriber.getEmail());
        responseDTO.setName(subscriber.getName());
        responseDTO.setActive(subscriber.isActive());
        responseDTO.setCreatedAt(subscriber.getCreatedAt());
        responseDTO.setUpdatedAt(subscriber.getUpdatedAt());

        List<String> categoryNames = subscriber.getCategories() == null
                ? Collections.emptyList()
                : subscriber.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        responseDTO.setCategoryNames(categoryNames);
        return responseDTO;
    }

    public void sendNotificationForNewDish(Dish newDish) {
        if (newDish == null || newDish.getCategory() == null) {
            return;
        }

        List<Subscriber> subscribers = this.subscriberRepository
                .findActiveSubscribersByCategoryId(newDish.getCategory().getId());
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }

        for (Subscriber subscriber : subscribers) {
            ResEmailDish resEmailDish = new ResEmailDish();
            resEmailDish.setDishName(newDish.getName());
            resEmailDish.setPrice(newDish.getPrice());
            resEmailDish.setImage(newDish.getImage());
            resEmailDish.setCategoryName(newDish.getCategory().getName());

            this.emailService.sendEmailFromTemplateSync(
                    subscriber.getEmail(),
                    "[Smart Restaurant] Món mới: " + newDish.getName(),
                    "dish",
                    subscriber.getName(),
                    Collections.singletonList(resEmailDish),
                    this.buildUnsubscribeUrl(subscriber.getEmail()));
        }
    }

    public void handleUnsubscribe(String email) throws IdInvalidException {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findByEmail(email);
        if (subscriberOptional.isEmpty()) {
            throw new IdInvalidException("Subscriber với email = " + email + " không tồn tại");
        }

        Subscriber subscriber = subscriberOptional.get();
        subscriber.setActive(false);
        this.subscriberRepository.save(subscriber);
    }

    private String buildUnsubscribeUrl(String email) {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return "http://localhost:8080/api/v1/subscribers/unsubscribe?email=" + encodedEmail;
    }

    private List<Category> mapCategoriesFromCategoryIds(List<Long> categoryIds) throws IdInvalidException {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Category> categories = this.categoryRepository.findByIdIn(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new IdInvalidException("Một hoặc nhiều categoryId không tồn tại");
        }
        return categories;
    }
}