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

    private Long parentTeamID;

    private static final long serialVersionUID = 1L;

    public Team(String teamname, Long captainID, Long parentTeamID) {
        this.teamname = teamname;
        this.captainID = captainID;
        this.parentTeamID = parentTeamID;
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

    public Long getCaptainID() {
        return captainID;
    }

    public void setCaptainID(Long captainID) {
        this.captainID = captainID;
    }

    public Long getParentTeamID() {
        return parentTeamID;
    }

    public void setParentTeamID(Long parentTeamID) {
        this.parentTeamID = parentTeamID;
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
            && (this.getCaptainID() == null ? other.getCaptainID() == null : this.getCaptainID().equals(other.getCaptainID()))
            && (this.getParentTeamID() == null ? other.getParentTeamID() == null : this.getParentTeamID().equals(other.getParentTeamID()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTeamname() == null) ? 0 : getTeamname().hashCode());
        result = prime * result + ((getCaptainID() == null) ? 0 : getCaptainID().hashCode());
        result = prime * result + ((getParentTeamID() == null) ? 0 : getParentTeamID().hashCode());
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
        sb.append(", captainID=").append(captainID);
        sb.append(", parentTeamID=").append(parentTeamID);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}