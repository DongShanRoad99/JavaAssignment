package com.doc.team.service.impl;

import com.doc.team.service.TeamMessageHandler;
import com.doc.team.service.TeamService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TeamReceiver implements TeamMessageHandler {

    private final TeamService teamService;

    @Autowired
    public TeamReceiver(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * 接收创建团队的消息，并调用业务逻辑。
     *
     * @param message 包含teamname和captainID的消息内容。
     */
    @Override
    @RabbitListener(queues = "com.team.create")
    public void handleCreateTeamMessage(Map<String, Object> message) {
        // 从Map中提取参数
        String teamname = (String) message.get("teamname");
        Long captainID = ((Number) message.get("captainID")).longValue();

        if (teamname != null && captainID != null) {
            teamService.createTeamWithCaptain(teamname, captainID);
        } else {
            throw new IllegalArgumentException("Invalid message format for creating a team");
        }
    }

    /**
     * 接收添加成员的消息，并调用业务逻辑。
     *
     * @param message 包含teamID和memberID的消息内容。
     */
    @Override
    @RabbitListener(queues = "com.team.addMember")
    public void handleAddMemberMessage(Map<String, Object> message) {
        // 从Map中提取参数
        Long teamID = ((Number) message.get("teamID")).longValue();
        Long memberID = ((Number) message.get("memberID")).longValue();

        if (teamID != null && memberID != null) {
            teamService.addMemberToTeam(teamID, memberID);
        } else {
            throw new IllegalArgumentException("Invalid message format for adding a member to a team");
        }
    }

    /**
     * 接收删除成员的消息，并调用业务逻辑。
     *
     * @param message 包含teamID和memberID的消息内容。
     */
    @Override
    @RabbitListener(queues = "com.team.deleteMember")
    public void handleDeleteMemberMessage(Map<String, Object> message) {
        // 从Map中提取参数
        Long teamID = ((Number) message.get("teamID")).longValue();
        Long memberID = ((Number) message.get("memberID")).longValue();

        if (teamID != null && memberID != null) {
            teamService.deleteMemberFromTeam(teamID, memberID);
        } else {
            throw new IllegalArgumentException("Invalid message format for deleting a member from a team");
        }
    }
}