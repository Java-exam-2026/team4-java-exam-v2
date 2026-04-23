package com.javaexam.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * AuditLogChangeCalculator を管理するクラス
 * target_type に応じた Calculator を提供する
 */
@Component
public class AuditLogChangeCalculatorRegistry {

    private final Map<String, AuditLogChangeCalculator> calculators;

    public AuditLogChangeCalculatorRegistry(
            UserChangeCalculator userChangeCalculator,
            QuestionChangeCalculator questionChangeCalculator) {
        
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
