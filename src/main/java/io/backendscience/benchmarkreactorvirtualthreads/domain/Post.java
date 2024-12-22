package io.backendscience.benchmarkreactorvirtualthreads.domain;

public record Post(
        int userId,
        int id,
        String title,
        String body
) {}
