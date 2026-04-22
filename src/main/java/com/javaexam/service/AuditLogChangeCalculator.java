package com.javaexam.service;

public interface AuditLogChangeCalculator {
    /**
     * 変更前後のエンティティを比較して、変更内容をJSON文字列として返す
     * @param before 変更前のエンティティ
     * @param after 変更後のエンティティ
     * @return 変更内容のJSON文字列（変更がない場合はnull）
     */
    String calculateChanges(Object before, Object after);
}
