package ru.yandex.practicum.filmorate.model;


import lombok.Data;


public class Friendship {
    private long userId;
    private long friendId;
    private FriendshipStatus status;
}
