package com.doc.team.model;

import java.io.Serializable;

/**
 * teams
 * @author 
 */
public class Team implements Serializable {
    private Long id;

    private String teamname;

    private Long captainID;

    private static final long serialVersionUID = 1L;

    public Team(String teamname, Long captainid) {
        this.teamname = teamname;
        this.captainID = captainid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public Long getCaptainid() {
        return captainID;
    }

    public void setCaptainid(Long captainid) {
        this.captainID = captainid;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Team other = (Team) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTeamname() == null ? other.getTeamname() == null : this.getTeamname().equals(other.getTeamname()))
            && (this.getCaptainid() == null ? other.getCaptainid() == null : this.getCaptainid().equals(other.getCaptainid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTeamname() == null) ? 0 : getTeamname().hashCode());
        result = prime * result + ((getCaptainid() == null) ? 0 : getCaptainid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", teamname=").append(teamname);
        sb.append(", captainid=").append(captainID);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}