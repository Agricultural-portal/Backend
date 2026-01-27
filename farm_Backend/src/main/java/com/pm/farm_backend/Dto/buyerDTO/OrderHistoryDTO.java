package com.pm.farm_backend.Dto.buyerDTO;

import lombok.Data;
import java.util.List;

@Data
public class OrderHistoryDTO {
    private OrderStats stats;
    private List<OrderDTO> orders;

    @Data
    public static class OrderStats {
        private long totalOrders;
        private long pending;
        private long inTransit;
        private long delivered;
    }
}
