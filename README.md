# java-filmorate
Template repository for Filmorate project.

### Структура приложения Filmorate
Приложение ***Filmorate*** имеет следующую структуру в реляционных таблицах БД:
```
Table films {
  id int [pk]
  name varchar
  description varchar
  release_date date
  duration int
  genre_id int
  mpa_id int
}

Table genres {
  id int [pk]
  name varchar
}

Table mpa {
  id int [pk]
  name varchar
}

Table likes {
  id int [pk]
  film_id int
  user_id int
}

Table users {
  id int [pk]
  email varchar
  login varchar
  name varchar
  birthday date
}

Table friends {
  id int [pk]
  user_id int
  friend_id int
  confirmed bool
}
```

### Связи таблиц
Описанные выше таблицы имеют следующие связи:
```
Ref: films.genre_id > genres.id     // many-to-one
Ref: films.mpa_id - mpa.id          // one-to-one
Ref: films.id < likes.film_id       // one-to-many
Ref: users.id < likes.user_id       // one-to-many
Ref: users.id < friends.user_id     // one-to-many
Ref: users.id < friends.friend_id   // one-to-many
```

Также ниже представлена графическое представление схемы базы данных
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/main/src/main/resources/ER_Diagram_Filmorate.png">
  <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/main/src/main/resources/ER_Diagram_Filmorate.png">
  <img alt="ER Diagram Filmorate" src="https://raw.githubusercontent.com/SmirnovSA1/java-filmorate/main/src/main/resources/ER_Diagram_Filmorate.png">
</picture>

### Примеры запросов
* Получение количества фильма и его общее количество лайков
```
SELECT f.name,
       COUNT(l.film_id)
FROM films AS f
JOIN likes AS l ON f.id = l.film_id
GROUP BY f.name
```

* Получение фильма с рейтингом "PG"
```
SELECT f.name
FROM films AS f
WHERE f.mpa_id =
    (SELECT m.id
     FROM mpa AS m
     WHERE m.name = 'PG')
```

* Получение всех друзей пользователя
```
SELECT u.name
FROM users AS u
WHERE u.id IN
    (SELECT f.friend_id
     FROM friends AS f
     WHERE f.user_id = 1)
```