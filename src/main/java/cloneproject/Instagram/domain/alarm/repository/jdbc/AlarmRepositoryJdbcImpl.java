package cloneproject.Instagram.domain.alarm.repository.jdbc;

import cloneproject.Instagram.domain.alarm.dto.AlarmType;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class AlarmRepositoryJdbcImpl implements AlarmRepositoryJdbc {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void saveMentionPostAlarms(Member agent, List<Member> targets, Post post, LocalDateTime now) {
		final String sql =
			"INSERT INTO alarms (`alarm_created_date`, `alarm_type`, `alarm_agent_id`, `post_id`, `alarm_target_id`) " +
				"VALUES(?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, now.toString());
					ps.setString(2, AlarmType.MENTION_POST.name());
					ps.setString(3, agent.getId().toString());
					ps.setString(4, post.getId().toString());
					ps.setString(5, targets.get(i).getId().toString());
				}

				@Override
				public int getBatchSize() {
					return targets.size();
				}
			});
	}

	@Override
	public void saveMentionCommentAlarms(Member agent, List<Member> targets, Post post, Comment comment,
		LocalDateTime now) {
		final String sql =
			"INSERT INTO alarms (`alarm_created_date`, `alarm_type`, `alarm_agent_id`, `comment_id`, `post_id`, `alarm_target_id`) "
				+
				"VALUES(?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, now.toString());
					ps.setString(2, AlarmType.MENTION_COMMENT.name());
					ps.setString(3, agent.getId().toString());
					ps.setString(4, comment.getId().toString());
					ps.setString(5, post.getId().toString());
					ps.setString(6, targets.get(i).getId().toString());
				}

				@Override
				public int getBatchSize() {
					return targets.size();
				}
			});
	}

}
