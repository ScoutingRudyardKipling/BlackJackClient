package com.engency.blackjack.network

interface OnNetworkResponseInterface<T> {
    fun success(data: T)
    fun failure(message: String)
}