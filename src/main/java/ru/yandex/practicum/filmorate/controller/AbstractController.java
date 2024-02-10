package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

public abstract class AbstractController<T> {
    protected final LocalDate BIRTHDATE_FILM = LocalDate.of(1895, 12, 28);
    protected HashMap<Integer, Film> films = new HashMap<>();
    protected HashMap<Integer, User> users = new HashMap<>();
}
