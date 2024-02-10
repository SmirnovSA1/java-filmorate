package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

public abstract class AbstractController<T> {
    protected HashMap<Integer, Film> films = new HashMap<>();
    protected HashMap<Integer, User> users = new HashMap<>();
}
