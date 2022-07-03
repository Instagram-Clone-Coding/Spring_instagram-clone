package cloneproject.Instagram.domain.dm.repository.jdbc;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.RoomUnreadMember;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class RoomUnreadMemberRepositoryJdbcImpl implements RoomUnreadMemberRepositoryJdbc {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void saveAllBatch(List<RoomUnreadMember> roomUnreadMembers, Message message) {
		final String sql = "INSERT INTO room_unread_members (`member_id`, `room_id`, `message_id`) VALUES(?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, roomUnreadMembers.get(i).getMember().getId().toString());
					ps.setString(2, roomUnreadMembers.get(i).getRoom().getId().toString());
					ps.setString(3, message.getId().toString());
				}

				@Override
				public int getBatchSize() {
					return roomUnreadMembers.size();
				}
			});
	}

}
