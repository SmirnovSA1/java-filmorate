package ru.yandex.practicum.filmorate.storage.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("user_email"))
                .login(rs.getString("user_login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("user_birthday").toLocalDate())
                .build();

        makeFriendship(user);

        return user;
    }

    private void makeFriendship(User user) {
        String query = "SELECT " +
                "friend_id, " +
                "FROM friendships " +
                "WHERE user_id = ?";

        List<Integer> userFriendships = jdbcTemplate.queryForList(query, Integer.class, user.getId());
        user.setFriends(new HashSet<>(userFriendships));
    }
}
