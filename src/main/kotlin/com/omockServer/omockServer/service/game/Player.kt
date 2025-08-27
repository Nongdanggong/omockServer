package com.omockServer.omockServer.converter.dto

import com.omockServer.omockServer.service.User
import com.omockServer.omockServer.service.game.StoneColor

class Player(
    val user: User,
    val stoneColor: StoneColor,
)
