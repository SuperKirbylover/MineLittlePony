package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.settings.PonyLevel;

class ClientPonyConfigHDSkins extends ClientPonyConfig {

    @Override
    public void setPonyLevel(PonyLevel ponylevel) {
        // only trigger reloads when the value actually changes
        if (ponylevel != getPonyLevel()) {
            HDSkins.getInstance().getSkinParser().execute();
        }

        super.setPonyLevel(ponylevel);
    }
}