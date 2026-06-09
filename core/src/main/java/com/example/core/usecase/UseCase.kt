package com.example.core.usecase

fun interface UseCase<in P, out R> {
    operator fun invoke(params: P): R
}

fun interface SuspendUseCase<in P, out R> {
    suspend operator fun invoke(params: P): R
}
