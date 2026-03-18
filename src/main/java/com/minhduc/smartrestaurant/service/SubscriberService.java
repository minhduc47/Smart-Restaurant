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

    public Subscriber handleCreateSubscriber(Subscriber requestSubscriber) {
        // check Category id exist
        if (requestSubscriber.getCategories() != null) {

            List<Long> listIdCategory = new ArrayList<>();
            // get List Category id
            for (Category category : requestSubscriber.getCategories()) {
                listIdCategory.add(category.getId());
            }

            List<Category> listCategories = this.categoryRepository.findByIdIn(listIdCategory);
            requestSubscriber.setCategories(listCategories);
        }
        requestSubscriber.setActive(true);
        return this.subscriberRepository.save(requestSubscriber);
    }

    public Subscriber updateSubscriber(Subscriber currentSubscriber, Subscriber requestSubscriber) {
        currentSubscriber.setName(requestSubscriber.getName());
        currentSubscriber.setEmail(requestSubscriber.getEmail());
        // check category id exist
        if (requestSubscriber.getCategories() != null) {
            // get List Category id
            List<Long> listIdCategory = requestSubscriber.getCategories()
                    .stream().map(category -> category.getId())
                    .collect(Collectors.toList());

            // get List Category by List Category id
            List<Category> listCategories = this.categoryRepository.findByIdIn(listIdCategory);
            // set currentSubscriber
            currentSubscriber.setCategories(listCategories);
        }
        currentSubscriber = this.subscriberRepository.save(currentSubscriber);
        return currentSubscriber;
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
}