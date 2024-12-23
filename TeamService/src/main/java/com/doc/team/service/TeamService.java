package com.doc.team.service;

/**
 * 团队相关的业务逻辑接口。
 */
public interface TeamService {

    /**
     * 创建一个新的团队，并自动将队长加入到该团队。
     * <p>
     * 此操作是原子性的，即要么团队和队长成员记录都成功创建，要么都不创建。
     *
     * @param teamname 团队名称，不允许为空且必须唯一。
     * @param captainID 队长的用户ID，不能为空。
     */
    void createTeamWithCaptain(String teamname, Long captainID);

    /**
     * 添加成员到指定的团队。
     * <p>
     * 如果成员已经是团队的一员，则不会重复添加。
     *
     * @param teamID 团队ID，标识要加入的团队。
     * @param memberID 成员的用户ID，标识要加入团队的用户。
     */
    void addMemberToTeam(Long teamID, Long memberID);

    /**
     * 从团队中删除成员。
     * <p>
     * 如果成员不是团队的一部分，则此操作无效果。
     *
     * @param teamID 团队ID，标识要从中移除成员的团队。
     * @param memberID 成员的用户ID，标识要被移除的用户。
     */
    void deleteMemberFromTeam(Long teamID, Long memberID);
}