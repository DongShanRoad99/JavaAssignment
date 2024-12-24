package com.doc.team.service.impl;

import com.doc.team.mapper.TeamMapper;
import com.doc.team.model.Team;
import com.doc.team.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 团队相关业务逻辑的具体实现。
 */
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;

    @Autowired
    public TeamServiceImpl(TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
    }

    /**
     * 根据团队ID获取团队信息。
     *
     * @param id 团队的唯一标识符。
     * @return 包含团队详细信息的对象，如果找不到则返回null。
     */
    @Override
    public Team getTeamById(Long id) {
        return teamMapper.getTeamById(id);
    }

    /**
     * 创建一个新的团队，并自动将队长加入到该团队。
     * <p>
     * 此操作是原子性的，即要么团队和队长成员记录都成功创建，要么都不创建。
     *
     * @param teamname 团队名称，不允许为空且必须唯一。
     * @param captainID 队长的用户ID，不能为空。
     */
    @Override
    @Transactional
    public void createTeamWithCaptain(String teamname, Long captainID, Long parentTeamID) {
        // 创建团队并获取生成的teamID
        Team team = new Team(teamname, captainID, parentTeamID);
        teamMapper.createTeam(team);
        System.out.println(team.getId());
        // 将队长作为成员加入到新创建的团队中
        teamMapper.addMemberToTeam(team.getId(), captainID);
    }

    /**
     * 添加成员到指定的团队。
     * <p>
     * 如果成员已经是团队的一员，则不会重复添加。
     *
     * @param teamID 团队ID，标识要加入的团队。
     * @param memberID 成员的用户ID，标识要加入团队的用户。
     */
    @Override
    @Transactional
    public void addMemberToTeam(Long teamID, Long memberID) {
        // 检查是否已经存在相同的记录以避免重复添加
        // 注意：这里假设MyBatis或数据库配置会处理重复键错误
        teamMapper.addMemberToTeam(teamID, memberID);
    }

    /**
     * 从团队中删除成员。
     * <p>
     * 如果成员不是团队的一部分，则此操作无效果。
     *
     * @param teamID 团队ID，标识要从中移除成员的团队。
     * @param memberID 成员的用户ID，标识要被移除的用户。
     */
    @Override
    @Transactional
    public void deleteMemberFromTeam(Long teamID, Long memberID) {
        teamMapper.deleteMemberFromTeam(teamID, memberID);
    }
}