package com.minhduc.smartrestaurant.service;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Catagory;
import com.minhduc.smartrestaurant.repository.CatagoryRepository;

@Service
public class CatagoryService {
    private final CatagoryRepository catagoryRepository;

    public CatagoryService(CatagoryRepository catagoryRepository) {
        this.catagoryRepository = catagoryRepository;
    }

    public Catagory handleCreateCatagory(Catagory catagory) {
        return catagoryRepository.save(catagory);
    }
}
