package br.com.gabrielguimaraes.log.parser.model;

public class IpCount {

    private String ip;
    private Integer count;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    @Override
    public int hashCode() {
        return (ip == null) ? 0 : ip.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IpCount other = (IpCount) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IpCount [ip=" + ip + ", count=" + count + "]";
    }
     
}
