package cloneproject.Instagram.domain.hashtag.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;

@RequiredArgsConstructor
public class HashtagPostRepositoryJdbcImpl implements HashtagPostRepositoryJdbc {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void saveAllBatch(List<HashtagPost> newHashtagPost) {
		final String sql = "INSERT INTO hashtag_posts (`hashtag_id`, `post_id`) " +
			"VALUES(?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, newHashtagPost.get(i).getHashtag().getId().toString());
					ps.setString(2, newHashtagPost.get(i).getPost().getId().toString());
				}

				@Override
				public int getBatchSize() {
					return newHashtagPost.size();
				}
			});
	}

}
