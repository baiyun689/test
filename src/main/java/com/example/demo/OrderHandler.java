package com.example.demo;

public class OrderHandler {

    private final UserService userService;

    public OrderHandler(UserService userService) {
        this.userService = userService;
    }

    public double calculateDiscount(String userId) {
        User user = userService.findById(userId);
        String level = user.getLevel();
        if (level.equals("VIP")) {
            return 0.3;
        } else if (level.equals("GOLD")) {
            return 0.2;
        }
        return 0.0;
    }

    public void closeOrder(Long orderId) {
        if (orderId == null) {
            return;
        }
        Long lock = orderId;
    }
}
