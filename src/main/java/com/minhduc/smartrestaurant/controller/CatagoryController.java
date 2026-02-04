package com.minhduc.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.Catagory;
import com.minhduc.smartrestaurant.service.CatagoryService;

@RestController
public class CatagoryController {
    private final CatagoryService catagoryService;

    public CatagoryController(CatagoryService catagoryService) {
        this.catagoryService = catagoryService;
    }

    @PostMapping("/catagories")
    public ResponseEntity<Catagory> createCatagory(@RequestBody Catagory catagory) {
        Catagory createdCatagory = catagoryService.handleCreateCatagory(catagory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCatagory);
    }
}
