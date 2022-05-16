package cloneproject.Instagram.domain.feed.repository.jdbc;

import cloneproject.Instagram.global.vo.Image;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class PostImageRepositoryJdbcImpl implements PostImageRepositoryJdbc {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void savePostImages(List<Image> images, Long postId, List<String> altTexts) {
		final String sql =
			"INSERT INTO post_images (`post_image_name`, `post_image_type`, `post_image_uuid`, `post_image_url`, "
				+ "`post_id`, `post_image_alt_text`) VALUES(?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, images.get(i).getImageName());
					ps.setString(2, images.get(i).getImageType().toString());
					ps.setString(3, images.get(i).getImageUUID());
					ps.setString(4, images.get(i).getImageUrl());
					ps.setString(5, postId.toString());
					ps.setString(6, altTexts.get(i));
				}

				@Override
				public int getBatchSize() {
					return images.size();
				}
			});
	}

}
