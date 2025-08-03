package com.jpmc.midascore.foundation;

public class Balance {
    private Long userId;
    private float amount;

    public Balance(Long userId, float amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "userId=" + userId +
                ", amount=" + amount +
                '}';
    }
}
