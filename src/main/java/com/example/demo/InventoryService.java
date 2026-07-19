package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();

    public boolean deduct(String productId, int quantity) {
        Integer current = stock.get(productId);
        if (current == null) {
            return false;
        }
        if (current >= quantity) {
            stock.put(productId, current - quantity);
            return true;
        }
        return false;
    }

    public void restock(String productId, int quantity) {
        Integer current = stock.get(productId);
        if (current == null) {
            stock.put(productId, quantity);
        } else {
            stock.put(productId, current + quantity);
        }
    }

    public int getStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    public int calculateTotalValue(Map<String, Integer> prices) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : prices.entrySet()) {
            int qty = stock.getOrDefault(entry.getKey(), 0);
            total += qty * entry.getValue();
        }
        return total;
    }

    public boolean reserveStock(String productId, int quantity) {
        if (!stock.containsKey(productId)) {
            return false;
        }
        if (quantity <= 0) {
            return false;
        }
        Integer current = stock.get(productId);
        if (current >= quantity) {
            stock.put(productId, current - quantity);
            return true;
        }
        return false;
    }
}
