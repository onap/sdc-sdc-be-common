package org.onap.sdc.common.versioning.persistence.zusammen;

import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.UserInfo;
import org.onap.sdc.common.session.SessionContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZusammenSessionContextCreator {

    private final SessionContextProvider sessionContextProvider;

    @Autowired
    public ZusammenSessionContextCreator(SessionContextProvider sessionContextProvider) {
        this.sessionContextProvider = sessionContextProvider;
    }


    public SessionContext create() {
        org.onap.sdc.common.session.SessionContext sdcSessionContext = sessionContextProvider.get();

        SessionContext sessionContext = new SessionContext();
        sessionContext.setUser(new UserInfo(sdcSessionContext.getUserId()));
        sessionContext.setTenant(sdcSessionContext.getTenant());
        return sessionContext;
    }
}
