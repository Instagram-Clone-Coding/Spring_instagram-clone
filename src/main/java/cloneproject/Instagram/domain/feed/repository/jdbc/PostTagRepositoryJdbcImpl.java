package cloneproject.Instagram.domain.feed.repository.jdbc;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class PostTagRepositoryJdbcImpl implements PostTagRepositoryJdbc {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void savePostTags(List<PostImageTagRequest> postImageTags) {
		final String sql =
			"INSERT INTO post_tags (`post_tag_username`, `post_tag_x`, `post_tag_y`, `post_image_id`) " +
				"VALUES(?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, postImageTags.get(i).getUsername());
					ps.setString(2, postImageTags.get(i).getTagX().toString());
					ps.setString(3, postImageTags.get(i).getTagY().toString());
					ps.setString(4, postImageTags.get(i).getId().toString());
				}

				@Override
				public int getBatchSize() {
					return postImageTags.size();
				}
			}
		);
	}

}
