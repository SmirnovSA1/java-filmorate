MERGE INTO genres (genre_id, genre_name) 
VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

MERGE INTO mpa (mpa_id, mpa_name, mpa_min_age) 
VALUES (1, 'G', 0), (2, 'PG', 0), (3, 'PG-13', 13), (4, 'R', 17), (5, 'NC-17', 18);