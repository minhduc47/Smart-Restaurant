package com.minhduc.smartrestaurant.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqTableDTO;
import com.minhduc.smartrestaurant.domain.response.ResTableDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.TableService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    @ApiMessage("Create a new table")
    public ResponseEntity<ResTableDTO> createTable(@Valid @RequestBody ReqTableDTO reqTableDTO) {
        ResTableDTO newTable = this.tableService.handleCreateTable(reqTableDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTable);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch table by id")
    public ResponseEntity<ResTableDTO> getTableById(@PathVariable("id") long id) throws IdInvalidException {
        RestaurantTable table = this.tableService.fetchTableById(id);
        ResTableDTO resTableDTO = this.tableService.convertToResTableDTO(table);
        return ResponseEntity.ok(resTableDTO);
    }

    @GetMapping
    @ApiMessage("Fetch all tables with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllTables(
            @Parameter(name = "filter", description = "Query filter (VD: name ~ 'duck')") @Filter Specification<RestaurantTable> spec,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(this.tableService.fetchAllTables(spec, pageable));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a table")
    public ResponseEntity<ResTableDTO> updateTable(@PathVariable("id") long id,
            @Valid @RequestBody ReqTableDTO reqTableDTO)
            throws IdInvalidException {
        ResTableDTO updatedTable = this.tableService.handleUpdateTable(id, reqTableDTO);
        return ResponseEntity.ok(updatedTable);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a table")
    public ResponseEntity<Void> deleteTable(@PathVariable("id") long id) throws IdInvalidException {
        this.tableService.handleDeleteTable(id);
        return ResponseEntity.ok().build();
    }
}
