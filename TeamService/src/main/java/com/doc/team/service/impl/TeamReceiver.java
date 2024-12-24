package com.doc.team.service.impl;

import com.doc.team.model.Team;
import com.doc.team.service.TeamMessageHandler;
import com.doc.team.service.TeamService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeamReceiver implements TeamMessageHandler {

    private final TeamService teamService;
    private final RabbitTemplate rabbitTemplate;
    public static final String RESPONSE_KEY = "com.team.response";
    public static final String EXCHANGE_NAME = "com.team";

    @Autowired
    public TeamReceiver(TeamService teamService, RabbitTemplate rabbitTemplate) {
        this.teamService = teamService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 将用户ID和消息信息封装到Map中，并发送到指定的交换机和路由键。
     *
     * @param userID 用户ID，表示操作者。
     * @param message 提供给用户的反馈信息。
     */
    protected void sendResponseToUser(Long userID, String message) {
        // 创建包含userID和message的Map
        Map<String, Object> response = new HashMap<>();
        response.put("userID", userID);
        response.put("message", message);

        // 发送响应消息到指定的交换机和路由键
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, RESPONSE_KEY, response);
    }

    /**
     * 接收创建团队的消息，并调用业务逻辑。
     *
     * @param message 包含teamname和captainID的消息内容。
     */
    @Override
    @RabbitListener(queues = "com.team.create")
    public void handleCreateTeamMessage(Map<String, Object> message) {
        try{
            // 从Map中提取参数
            Long userID = ((Number) message.get("userID")).longValue();
            String teamname = (String) message.get("teamname");
            Long captainID = ((Number) message.get("captainID")).longValue();
            Long parentTeamID = ((Number) message.get("parentTeamID")).longValue();

            if(!userID.equals(captainID)){
                sendResponseToUser(userID, "Please create a team in person");
                return;
            }
            if(teamname == null || teamname.isEmpty()){
                sendResponseToUser(userID, "Please use a correct team name");
                return;
            }
            teamService.createTeamWithCaptain(teamname, captainID, parentTeamID);
            sendResponseToUser(userID, "team '" + teamname + "' successfully created");
        } catch (Exception e){
            System.out.println(e.getMessage());
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
        try{
            // 从Map中提取参数
            Long userID = ((Number) message.get("userID")).longValue();
            Long teamID = ((Number) message.get("teamID")).longValue();
            Long memberID = ((Number) message.get("memberID")).longValue();

            Team team = teamService.getTeamById(teamID);
            if(!userID.equals(team.getCaptainID())){
                sendResponseToUser(userID, "you are unauthorized for this action");
                return;
            }
            teamService.addMemberToTeam(teamID, memberID);
            sendResponseToUser(userID, "add member successfully");
        } catch (Exception e){
            System.out.println(e.getMessage());
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
        try{
            // 从Map中提取参数
            Long userID = ((Number) message.get("userID")).longValue();
            Long teamID = ((Number) message.get("teamID")).longValue();
            Long memberID = ((Number) message.get("memberID")).longValue();

            Team team = teamService.getTeamById(teamID);
            if(!userID.equals(team.getCaptainID())){
                sendResponseToUser(userID, "you are unauthorized for this action");
                return;
            }
            teamService.deleteMemberFromTeam(teamID, memberID);
            sendResponseToUser(userID, "delete member successfully");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}