package com.example.sweetrock

import java.util.Date

data class Review(val username: String, val uid: String, val date: Date, val rating: Float, val text: String)