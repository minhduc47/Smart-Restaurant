package com.minhduc.smartrestaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.Subscriber;
import com.minhduc.smartrestaurant.repository.CategoryRepository;
import com.minhduc.smartrestaurant.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final CategoryRepository categoryRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, CategoryRepository categoryRepository) {
        this.subscriberRepository = subscriberRepository;
        this.categoryRepository = categoryRepository;
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
}