/*
 * 사용안함. 초기 샘플.
 */
package com.ziumks.gtway;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;


@Component
public class JwtValidator implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    //@Value("${jwt.secret}")
    private String secret = "s83ztUyN3q4LCC4UycexiNCjULhPeF2QheVcrJaVNbq1zZgZujt_B_wsGcLq0Yj9edE_MJVlXmGv6PKfy9wr9tVmMMNgxU4Psqth7zgwyNsuwYZCNrqUVAQCO3Zy_LHHtJB44fcSCLdHGBJC0wlCinW7Hk23DwJgbNdYgnToY5edO8ENTWb-jNnEhX_xZOPsgyE0B2f4E7e7h3mw94Q5_1v8H19TMGPPLkHTG9qFlJutIFIoSBRU4iiAdDumwnlfps-J9N0SElXx_djXt0PmwUC5oziBGqKz3G2yJGfOCMw8CzYlr2e1goIhlCjGGQd4xk6byE6Iz51OkekKRvTXuztinjYvt-DXTjTowhxxYVDYp_dFEIY-Rbtx-0z0VmytyUf69pdoFV5d-qp_6N_GzYYGOn2kDlEY8N4KcGUNhDqfylfG5GJsSmOkfIY5-V2e1cZHalhOC6y62MC2-JHiLvsIiSE67rQT8g6NRlZLrs3BOpoL-bt7j-4MVshys2L-sJk8_6Fxg0X_NJIjcJsEjrR64E1lUqkSQIumnaHyJ9YVgOxQAIEDkk77ifDC79n5r_Q0Xl81OcRBBMppe_ysfxdFby0UjAL9qDf_ivcdMOdhZykuvs0UeU5H8PWgyjbH1e1HXE-Dnns-qCNtKlp8DvoPpc51OCGb9vAnKfNhXws";
    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);

    public Map<String, Object> getUserParseInfo(String token) {
        Claims parseInfo = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String, Object> result = new HashMap<>();
        //expiration date < now
        boolean isExpired = !parseInfo.getExpiration().before(new Date());
        result.put("username", parseInfo.getSubject());
        result.put("role", parseInfo.get("role", List.class));
        result.put("isExpired", isExpired);
        return result;
    }

    public boolean isValidate(String token) {
        try {
            Map<String, Object> info = getUserParseInfo(token);
        }
        // token is expired
        catch (ExpiredJwtException e) {
            logger.warn("The token is expired.");
            return false;
        }
        // signature is wrong
        catch (SignatureException e) {
            logger.warn("Signature of the token is wrong.");
            return false;
        }
        // format is wrong
        catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            logger.warn("The token string is wrong format.");
            return false;
        }
        return true;
    }
}