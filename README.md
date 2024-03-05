# java-filmorate
Template repository for Filmorate project.

### Структура приложения Filmorate
Приложение ***Filmorate*** имеет следующую структуру в реляционных таблицах БД:
```
Table film {
  film_id int [pk]
  name varchar
  description varchar
  release_date date
  duration int
  rating_id int
}

Table film_genre {
  film_genre int [pk]
  film_id int
  genre_id int
}

Table genre {
  genre_id int [pk]
  name varchar
}

Table rating {
  rating_id int [pk]
  name varchar
}

Table film_like {
  film_like_id int [pk]
  film_id int
  user_id int
}

Table users {
  user_id int [pk]
  email varchar
  login varchar
  name varchar
  birthday date
}

Table friendship {
  friendship_id int [pk]
  user_id int
  friend_id int
  confirmed bool
}
```

### Связи таблиц
Описанные выше таблицы имеют следующие связи:
```
Ref: film.film_id < film_genre.film_id     // one-to-many
Ref: genre.genre_id < film_genre.genre_id  // one-to-many
Ref: film.rating_id > rating.rating_id     // many-to-one
Ref: film.film_id < film_like.film_id      // one-to-many
Ref: users.user_id < film_like.user_id     // one-to-many
Ref: users.user_id < friendship.user_id    // one-to-many
Ref: users.user_id < friendship.friend_id  // one-to-many
```

Также ниже представлена графическое представление схемы базы данных
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/59eb13a1ce65c2a6d61d57398f0adc82e9883404/src/main/resources/ER_Diagram_Filmorate.png">
  <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/59eb13a1ce65c2a6d61d57398f0adc82e9883404/src/main/resources/ER_Diagram_Filmorate.png">
  <img alt="ER Diagram Filmorate" src="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/59eb13a1ce65c2a6d61d57398f0adc82e9883404/src/main/resources/ER_Diagram_Filmorate.png">
</picture>

### Примеры запросов
* Получение наименование фильма и его общее количество лайков
```
SELECT f.name,
       COUNT(fl.film_id)
FROM film AS f
JOIN film_like AS fl ON f.film_id = fl.film_id
GROUP BY f.name
```

* Получение количества лайков фильма по идентификатору (film_id)
```
SELECT COUNT(fl.film_id)
FROM film_like AS fl
WHERE fl.film_id = 29
```

* Получение фильма с рейтингом "PG"
```
SELECT f.name
FROM film AS f
WHERE f.rating_id =
    (SELECT r.id
     FROM rating AS r
     WHERE r.name = 'PG')
```

* Получение всех друзей пользователя
```
SELECT u.name
FROM user AS u
WHERE u.user_id IN
    (SELECT fs.friend_id
     FROM friendship AS fs
     WHERE fs.user_id = 1)
```

* Получение фильмов, которые лайкнул пользователь
```
SELECT f.name
FROM film AS f
WHERE f.film_id IN
    (SELECT fl.film_id
     FROM film_like AS fl
     WHERE fl.user_id = 36)
```