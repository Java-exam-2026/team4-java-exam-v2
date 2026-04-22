package com.javaexam.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AuditLogChangeCalculatorRegistry {

    private final Map<String, AuditLogChangeCalculator> calculators;

    public AuditLogChangeCalculatorRegistry(
            UserChangeService userChangeCalculator,
            QuestionChangeService questionChangeCalculator) {
        
        this.calculators = new HashMap<>();
        this.calculators.put("USER", userChangeCalculator);
        this.calculators.put("QUESTION", questionChangeCalculator);
    }

    /**
     * target_type に応じた Calculator を取得
     * @param targetType ターゲットタイプ（USER, QUESTION など）
     * @return 対応する Calculator
     */
    public AuditLogChangeCalculator getCalculator(String targetType) {
        return calculators.get(targetType);
    }
}
