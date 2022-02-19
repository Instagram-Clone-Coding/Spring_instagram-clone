package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.mention.MentionType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MentionRepositoryJdbcImpl implements MentionRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void savePostMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, LocalDateTime now) {
        final String sql =
                "INSERT INTO mentions (`mention_create_date`, `mention_type`, `agent_id`, `comment_id`, `post_id`, `target_id`) " +
                        "VALUES(?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, now.toString());
                        ps.setString(2, MentionType.POST.name());
                        ps.setString(3, memberId.toString());
                        ps.setString(4, null);
                        ps.setString(5, postId.toString());
                        ps.setString(6, mentionedMembers.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return mentionedMembers.size();
                    }
                }
        );
    }

    @Override
    public void saveCommentMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, Long commentId, LocalDateTime now) {
        final String sql =
                "INSERT INTO mentions (`mention_create_date`, `mention_type`, `agent_id`, `comment_id`, `post_id`, `target_id`) " +
                        "VALUES(?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, now.toString());
                        ps.setString(2, MentionType.COMMENT.name());
                        ps.setString(3, memberId.toString());
                        ps.setString(4, commentId.toString());
                        ps.setString(5, postId.toString());
                        ps.setString(6, mentionedMembers.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return mentionedMembers.size();
                    }
                }
        );
    }
}
