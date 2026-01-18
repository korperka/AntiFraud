package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UserListResponse {
    private List<UserResponseDTO> items;
    private Integer total;
    private Integer page;
    private Integer size;
}
