package cn.tkk.common.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *  Tkk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    public String getIp() {
        String ip = request.getHeader("x-real-ip");
        if (StringUtils.isBlank(ip)) {
            return request.getRemoteAddr();
        }
        return ip;
    }

    public String getHost() {
        return request.getServerName();
    }

    private String getPort() {
        int port = request.getServerPort();
        return port == 80 ? "" : ":" + String.valueOf(port);
    }

    private HttpServletRequest request;

    private LoginUser loginUser;

    public <T> T getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        } else {
            return (T) loginUser.getId();
        }
    }

    /**
     * @return
     */
    public String getSite() {
        return String.format("%s://%s%s", StringUtils.contains(request.getProtocol(), "https") ? "https" : "http", getHost(), getPort());
    }

    /**
     * @return
     */
    public HttpSession getSession() {
        return request.getSession(true);
    }

    /**
     * @return
     */
    public String getJwt() {
        return request.getHeader("token");
    }
}
