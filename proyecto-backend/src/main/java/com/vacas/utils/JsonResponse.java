package com.vacas.utils;

import com.google.gson.JsonObject;

public class JsonResponse {
    
    public static String success(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        return json.toString();
    }
    
    public static String success(String message, JsonObject data) {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        json.add("data", data);
        return json.toString();
    }
    
    public static String error(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        return json.toString();
    }
    
    public static String error(String message, int code) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("errorCode", code);
        json.addProperty("message", message);
        return json.toString();
    }
}