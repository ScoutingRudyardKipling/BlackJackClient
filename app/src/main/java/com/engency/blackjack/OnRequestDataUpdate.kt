package com.engency.blackjack

interface OnRequestDataUpdate {
    fun onUpdateRequested(cascade : Boolean = true)
}