package br.com.gabrielguimaraes.log.parser.model;

import br.com.gabrielguimaraes.log.parser.database.Id;

public class IpBlocked {

    @Id
    private Integer id;
    private String ip;
    private String reason;

    public IpBlocked() {}
    
    public IpBlocked(String ip, String reason) {
        this.ip = ip;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IpBlocked other = (IpBlocked) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IpBlocked [id=" + id + ", ip=" + ip + ", reason=" + reason + "]";
    }

}
