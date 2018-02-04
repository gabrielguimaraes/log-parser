package br.com.gabrielguimaraes.log.parser.model;

import java.time.LocalDateTime;

import br.com.gabrielguimaraes.log.parser.database.Column;
import br.com.gabrielguimaraes.log.parser.database.Id;

public class LogData {
    
    @Id
	private Integer id;
	
	@Column("execution_date")
	private LocalDateTime executionDate;
	private String ip;
	private String request;
	private Integer status;
	@Column("user_agent")
	private String userAgent;
	
	public Integer getId() {
		return id;
	}

	public LocalDateTime getExecutionDate() {
		return executionDate;
	}

	public String getIp() {
		return ip;
	}

	public String getRequest() {
		return request;
	}

	public Integer getStatus() {
		return status;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setExecutionDate(LocalDateTime executionDate) {
		this.executionDate = executionDate;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogData other = (LogData) obj;
        if (executionDate == null) {
            if (other.executionDate != null)
                return false;
        } else if (!executionDate.equals(other.executionDate))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (request == null) {
            if (other.request != null)
                return false;
        } else if (!request.equals(other.request))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (userAgent == null) {
            if (other.userAgent != null)
                return false;
        } else if (!userAgent.equals(other.userAgent))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LogData [id=" + id + ", executionDate=" + executionDate + ", ip=" + ip + ", request=" + request
                + ", status=" + status + ", userAgent=" + userAgent + "]";
    }

}
