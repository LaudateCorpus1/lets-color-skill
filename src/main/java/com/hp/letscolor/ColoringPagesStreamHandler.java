/*
 * Copyright 2018 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.hp.letscolor.handler.CancelAndStopIntent;
import com.hp.letscolor.handler.ColoringPagesIntentHandler;
import com.hp.letscolor.handler.ConnectionsResponseIntentHandler;
import com.hp.letscolor.handler.FallbackIntentHandler;
import com.hp.letscolor.handler.LaunchIntentHandler;
import com.hp.letscolor.handler.NoIntentHandler;
import com.hp.letscolor.handler.YesIntentHandler;


/**
 * This is the Entry point for Lambda usage. One should register all of Intent Handlers.
 */
public class ColoringPagesStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new LaunchIntentHandler(),
                        new ColoringPagesIntentHandler(),
                        new YesIntentHandler(),
                        new NoIntentHandler(),
                        new ConnectionsResponseIntentHandler(),
                        new CancelAndStopIntent(),
                        new FallbackIntentHandler())
                .withSkillId("amzn1.ask.skill.13178136-5511-46f9-8106-892ee4563f50")
                .build();
    }

    public ColoringPagesStreamHandler() {
        super(getSkill());
    }
}
