package com.sammwy.mplace;

public class CooldownData {
    public long placeExpiresAt = 0L;
    public long breakExpiresAt = 0L;

    public boolean canPlace() {
        return System.currentTimeMillis() >= placeExpiresAt;
    }

    public boolean canBreak() {
        return System.currentTimeMillis() >= breakExpiresAt;
    }

    public long getRemaining(String type) {
        long now = System.currentTimeMillis();
        if (type.equals("place"))
            return Math.max(0L, placeExpiresAt - now);
        else
            return Math.max(0L, breakExpiresAt - now);
    }

    public String getRemainingFmt(String type) {
        long remaining = getRemaining(type);
        if (remaining == 0)
            return "§aready";
        else
            return "§c" + (remaining / 1000) + "s";
    }

    public void set(String type, long durationMs) {
        long now = System.currentTimeMillis();
        if (type.equals("place"))
            placeExpiresAt = now + durationMs;
        else if (type.equals("break"))
            breakExpiresAt = now + durationMs;
    }
}
