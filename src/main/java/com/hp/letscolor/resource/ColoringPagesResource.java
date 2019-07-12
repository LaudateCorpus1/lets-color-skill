/*
 * Copyright 2019 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum ColoringPagesResource {
    ANIMALS(Arrays.asList(
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Animals/colorme_u_Printable_Page_Animals_Cow.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Animals/colorme_u_Printable_Page_Animals_Fox.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Animals/colorme_u_Printable_Page_Animals_Squirrel.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Animals/colorme_u_Printable_Page_Animals_Turtle.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Animals/colorme_u_Printable_Page_Turkey.pdf")
    ),
    FRUITS(Arrays.asList(
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Fruits/colorme_u_Printable_Page_More_Applepie.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Fruits/colorme_u_Printable_Page_More_Fruit.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Fruits/colorme_u_Printable_Page_More_Orangejuice.pdf")
    ),
    ALPHABET(Arrays.asList(
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Letters/colorme_u_Printable_Page_Alphabet_LetterH.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Letters/colorme_u_Printable_Page_Alphabet_LetterL.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Letters/colorme_u_Printable_Page_Alphabet_LetterM.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Letters/colorme_u_Printable_Page_Alphabet_LetterP.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Letters/colorme_u_Printable_Page_Alphabet_LetterT.pdf")
    ),
    NATURE(Arrays.asList(
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Nature/colorme_u_Printable_Page_Science_Nature_Tree1.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Nature/colorme_u_Printable_Page_Science_Nature_Tree4.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Nature/colorme_u_Printable_Page_Science_Nature_Windmills.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Nature/colorme_u_Printable_Page_Science_Nature_Wool.pdf")
    ),
    THINGS(Arrays.asList(
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Things/colorme_u_Printable_Page_More_Car.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Things/colorme_u_Printable_Page_More_Drums.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Things/colorme_u_Printable_Page_More_Plane.pdf",
            "https://lets-color-skill.s3.us-west-2.amazonaws.com/Things/colorme_u_Printable_Page_More_Tugboat.pdf")
    );


    private final List<String> urls;

    ColoringPagesResource(List<String> urls) {
        this.urls = urls;
    }

    public List<String> urls() {
        return this.urls;
    }

    public String capitalizeName() {
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }

    public static ColoringPagesResource pickResource() {
        Random random = new Random();
        ColoringPagesResource[] coloringPagesResources = ColoringPagesResource.values();
        return coloringPagesResources[random.nextInt(coloringPagesResources.length)];
    }

    @Override
    public String toString() {
        return "ColoringPagesResource{" +
                "urls=" + urls +
                '}';
    }
}
