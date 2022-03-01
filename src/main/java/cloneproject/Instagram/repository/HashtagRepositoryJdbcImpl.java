package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class HashtagRepositoryJdbcImpl implements HashtagRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(List<Hashtag> hashtags, Post post) {
        final String sql =
                "INSERT INTO hashtags (`hashtag_name`, `post_id`, `hashtag_count`) " +
                        "VALUES(?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, hashtags.get(i).getName());
                        ps.setString(2, post.getId().toString());
                        ps.setString(3, "1");
                    }

                    @Override
                    public int getBatchSize() {
                        return hashtags.size();
                    }
                });
    }
}
