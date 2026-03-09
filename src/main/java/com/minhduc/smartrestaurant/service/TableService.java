package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqTableDTO;
import com.minhduc.smartrestaurant.domain.response.ResTableDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.RestaurantTableRepository;
import com.minhduc.smartrestaurant.util.constant.TableEnum;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class TableService {
    private final RestaurantTableRepository restaurantTableRepository;

    public TableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public ResTableDTO handleCreateTable(ReqTableDTO reqTableDTO) {
        RestaurantTable table = new RestaurantTable();
        table.setName(reqTableDTO.getName());
        table.setQrToken(reqTableDTO.getQrToken());
        table.setOccupied(TableEnum.AVAILABLE);

        RestaurantTable savedTable = this.restaurantTableRepository.save(table);
        return this.convertToResTableDTO(savedTable);
    }

    public ResTableDTO handleUpdateTable(long id, ReqTableDTO reqTableDTO) throws IdInvalidException {
        RestaurantTable currentTable = this.fetchTableById(id);
        currentTable.setName(reqTableDTO.getName());
        currentTable.setQrToken(reqTableDTO.getQrToken());

        RestaurantTable updatedTable = this.restaurantTableRepository.save(currentTable);
        return this.convertToResTableDTO(updatedTable);
    }

    public RestaurantTable fetchTableById(long id) throws IdInvalidException {
        Optional<RestaurantTable> tableOptional = this.restaurantTableRepository.findById(id);
        if (tableOptional.isPresent()) {
            return tableOptional.get();
        }

        throw new IdInvalidException("Bàn với id = " + id + " không tồn tại");
    }

    public ResultPaginationDTO fetchAllTables(Specification<RestaurantTable> spec, Pageable pageable) {
        Page<RestaurantTable> pageTable = this.restaurantTableRepository.findAll(spec, pageable);
        List<ResTableDTO> listTables = pageTable.getContent()
                .stream()
                .map(this::convertToResTableDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageTable.getTotalPages());
        meta.setTotal(pageTable.getTotalElements());

        result.setMeta(meta);
        result.setResult(listTables);
        return result;
    }

    public void handleDeleteTable(long id) throws IdInvalidException {
        RestaurantTable currentTable = this.fetchTableById(id);
        this.restaurantTableRepository.delete(currentTable);
    }

    public ResTableDTO convertToResTableDTO(RestaurantTable table) {
        ResTableDTO resTableDTO = new ResTableDTO();
        resTableDTO.setId(table.getId());
        resTableDTO.setName(table.getName());
        resTableDTO.setQrToken(table.getQrToken());
        resTableDTO.setOccupied(table.getOccupied());
        resTableDTO.setCreatedAt(table.getCreatedAt());
        resTableDTO.setCreatedBy(table.getCreatedBy());

        return resTableDTO;
    }
}
