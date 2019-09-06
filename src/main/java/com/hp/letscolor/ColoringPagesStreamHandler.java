/*
 * Copyright 2019 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.hp.letscolor.handler.CancelAndStopIntent;
import com.hp.letscolor.handler.ColoringPagesIntentHandler;
import com.hp.letscolor.handler.SessionResumedHandler;
import com.hp.letscolor.handler.FallbackIntentHandler;
import com.hp.letscolor.handler.LaunchHandler;
import com.hp.letscolor.handler.NoIntentHandler;
import com.hp.letscolor.handler.YesIntentHandler;


/**
 * This is the Entry point for Lambda usage. One should register all of Intent Handlers.
 */
public class ColoringPagesStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new LaunchHandler(),
                        new ColoringPagesIntentHandler(),
                        new YesIntentHandler(),
                        new NoIntentHandler(),
                        new SessionResumedHandler(),
                        new CancelAndStopIntent(),
                        new FallbackIntentHandler())
                .withSkillId(System.getenv("SKILL_ID"))
                .build();
    }

    public ColoringPagesStreamHandler() {
        super(getSkill());
    }
}
