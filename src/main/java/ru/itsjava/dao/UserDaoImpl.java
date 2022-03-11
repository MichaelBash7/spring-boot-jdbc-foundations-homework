package ru.itsjava.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.itsjava.domain.Pet;
import ru.itsjava.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@SuppressWarnings("ALL")
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao{
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public int count() {
        return jdbc.getJdbcOperations().queryForObject("select count (*) from user", Integer.class);
    }

    @Override
    public long insert(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = Map.of("name", user.getName(), "age", user.getAge(), "pet_id", user.getPet().getId());
        jdbc.update("insert into user(name, age, pet_id) values (:name, :age, :pet_id)", new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void update(User user) {
        Map<String, Object> params = Map.of("id", user.getId(), "name", user.getName(), "age", user.getAge());
        jdbc.update("update user set name = :name where id = :id", params);
        jdbc.update("update user set age = :age where id = :id", params);
    }

    @Override
    public void delete(User user) {
        Map<String, Object> params = Map.of("id", user.getId());
        jdbc.update("delete from user where id = :id", params);
    }

    @Override
    public User findById(long id) {
        Map<String, Object> params = Map.of("id", id);
        return jdbc.queryForObject("select u.id, name, age, p.id, species from user u, pets p where u.id = :id " +
                "and u.pet_id = p.id", params, new UserMapper());
    }


    private static class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getLong("id"), rs.getString("name"), rs.getInt("age"),
                   new Pet(rs.getLong("id"), rs.getString("species")));
        }
    }
}
