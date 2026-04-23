package com.javaexam.dto;

public record AuditLogPageInfo(
        boolean filtered,
        int currentPage,
        int pageSize,
        long totalCount,
        int totalPages,
        long fromRow,
        long toRow) {

    public static AuditLogPageInfo forFiltered(long totalCount) {
        long fromRow = totalCount > 0 ? 1L : 0L;
        long toRow = totalCount;
        return new AuditLogPageInfo(true, 1, 0, totalCount, 1, fromRow, toRow);
    }

    public static AuditLogPageInfo forPaged(int currentPage, int pageSize, long totalCount, int totalPages) {
        long fromRow = totalCount == 0 ? 0L : ((long) (currentPage - 1) * pageSize) + 1L;
        long toRow = Math.min((long) currentPage * pageSize, totalCount);
        return new AuditLogPageInfo(false, currentPage, pageSize, totalCount, totalPages, fromRow, toRow);
    }
}
