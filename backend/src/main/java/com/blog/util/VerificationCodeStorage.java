package com.blog.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Verification Code Storage Utility
 * Stores verification codes in memory with automatic expiration
 */
public class VerificationCodeStorage {
    
    // Store verification codes with email as key
    private static final Map<String, String> codeStorage = new ConcurrentHashMap<>();
    
    // Store expiration times
    private static final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();
    
    // Default expiration time: 5 minutes
    private static final long DEFAULT_EXPIRATION = 5 * 60 * 1000;
    
    /**
     * Store a verification code for an email
     * @param email User's email
     * @param code Verification code
     */
    public static void storeCode(String email, String code) {
        codeStorage.put(email, code);
        expirationTimes.put(email, System.currentTimeMillis() + DEFAULT_EXPIRATION);
        
        // Schedule cleanup task
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeExpiredCode(email);
            }
        }, DEFAULT_EXPIRATION);
    }
    
    /**
     * Validate a verification code
     * @param email User's email
     * @param code Verification code to validate
     * @return true if code is valid and not expired, false otherwise
     */
    public static boolean validateCode(String email, String code) {
        // Check if code exists
        String storedCode = codeStorage.get(email);
        if (storedCode == null) {
            return false;
        }
        
        // Check if code is expired
        Long expirationTime = expirationTimes.get(email);
        if (expirationTime == null || System.currentTimeMillis() > expirationTime) {
            // Remove expired code
            removeExpiredCode(email);
            return false;
        }
        
        // Check if codes match
        return storedCode.equals(code);
    }
    
    /**
     * Remove a verification code (after successful use)
     * @param email User's email
     */
    public static void removeCode(String email) {
        codeStorage.remove(email);
        expirationTimes.remove(email);
    }
    
    /**
     * Remove an expired verification code
     * @param email User's email
     */
    private static void removeExpiredCode(String email) {
        codeStorage.remove(email);
        expirationTimes.remove(email);
    }
}