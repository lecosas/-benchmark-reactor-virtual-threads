package io.backendscience.benchmarkreactorvirtualthreads.domain;

public record Todo (
        int userId,
        int id,
        String title,
        boolean completed
) {}
