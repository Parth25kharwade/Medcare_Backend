package com.medcare.controller;

import com.medcare.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;

    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/explain-lab")
    public String explainLabReport(@RequestBody Map<String, Object> data) {

        String prompt = """
You are a clinical AI assistant.

Analyze this lab report and return:

1. Parameter
2. Normal Range
3. Patient Value
4. Interpretation
5. Risk Level

Lab Report:
""" + data;

        return geminiService.analyzePatient(prompt);
    }
}