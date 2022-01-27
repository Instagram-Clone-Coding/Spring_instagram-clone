package cloneproject.Instagram.dto.alarm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmDTO implements Comparable<AlarmDTO>{

    @JsonIgnore
    private Long id;
    private String type;
    private String alarmMessage;
    private String agentUsername;
    private String targetUsername;
    private Map<String, Long> itemIds;
    private String createdAt;

    @Override
    public int compareTo(AlarmDTO alarmDTO) {
         return this.id.compareTo(alarmDTO.id); 
    }

    @QueryProjection
    public AlarmDTO(Long id, AlarmType type, String agentUsername, String targetUsername, Long itemId, Date createdAt){
        this.itemIds = new HashMap<>();

        this.id = id;
        this.type = type.toString();
        this.alarmMessage = type.getAlarmMesasge();
        this.agentUsername = agentUsername;
        this.targetUsername = targetUsername;
        this.itemIds.put(type.getFirstItemType(), itemId);
        this.createdAt = DateUtil.convertDateToString(createdAt);
    }

}
