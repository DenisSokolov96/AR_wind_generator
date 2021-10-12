package com.google.ar.sceneform.samples.animation;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class HorizontalPlanesArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
        return config;
    }
}
