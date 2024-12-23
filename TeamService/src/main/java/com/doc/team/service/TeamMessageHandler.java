package com.doc.team.service;

import java.util.Map;

/**
 * 团队消息处理器接口。
 */
public interface TeamMessageHandler {

    /**
     * 处理创建团队的消息。
     *
     * @param message 包含teamname和captainID的消息内容。
     */
    void handleCreateTeamMessage(Map<String, Object> message);

    /**
     * 处理添加成员到团队的消息。
     *
     * @param message 包含teamID和memberID的消息内容。
     */
    void handleAddMemberMessage(Map<String, Object> message);

    /**
     * 处理从团队中删除成员的消息。
     *
     * @param message 包含teamID和memberID的消息内容。
     */
    void handleDeleteMemberMessage(Map<String, Object> message);
}