package com.example.spotifymoodlogin;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class MoodMatchers {

    public static final Map<String, List<Pattern>> MOOD_MATCHERS = Map.ofEntries(
        Map.entry("happy", List.of(
            Pattern.compile("pop"), Pattern.compile("dance"), Pattern.compile("disco"),
            Pattern.compile("funk"), Pattern.compile("upbeat"), Pattern.compile("feelgood"),
            Pattern.compile("sunshine"), Pattern.compile("bubblegum"), Pattern.compile("indie pop")
        )),
        Map.entry("chill", List.of(
            Pattern.compile("chill"), Pattern.compile("lo[- ]?fi"), Pattern.compile("acoustic"),
            Pattern.compile("ambient"), Pattern.compile("downtempo"), Pattern.compile("soft"),
            Pattern.compile("smooth"), Pattern.compile("bossa"), Pattern.compile("mellow"), Pattern.compile("laid[- ]?back")
        )),
        Map.entry("pumped", List.of(
            Pattern.compile("edm"), Pattern.compile("trap"), Pattern.compile("house"),
            Pattern.compile("techno"), Pattern.compile("metal"), Pattern.compile("hardstyle"),
            Pattern.compile("dubstep"), Pattern.compile("industrial"), Pattern.compile("big room"), Pattern.compile("festival")
        )),
        Map.entry("melancholic", List.of(
            Pattern.compile("indie"), Pattern.compile("sad"), Pattern.compile("dreampop"),
            Pattern.compile("emo"), Pattern.compile("shoegaze"), Pattern.compile("slowcore"),
            Pattern.compile("darkwave"), Pattern.compile("alternative"), Pattern.compile("post[- ]?punk")
        )),
        Map.entry("romantic", List.of(
            Pattern.compile("r&b"), Pattern.compile("soul"), Pattern.compile("ballad"),
            Pattern.compile("romance"), Pattern.compile("latin pop"), Pattern.compile("soft rock"),
            Pattern.compile("acoustic"), Pattern.compile("love"), Pattern.compile("smooth"), Pattern.compile("slow jam")
        )),
        Map.entry("nostalgic", List.of(
            Pattern.compile("retro"), Pattern.compile("80s"), Pattern.compile("90s"),
            Pattern.compile("vintage"), Pattern.compile("synthwave"), Pattern.compile("classic"),
            Pattern.compile("throwback"), Pattern.compile("vaporwave"), Pattern.compile("oldies"), Pattern.compile("blues")
        )),
        Map.entry("energetic", List.of(
            Pattern.compile("workout"), Pattern.compile("gym"), Pattern.compile("adrenaline"),
            Pattern.compile("power"), Pattern.compile("fast"), Pattern.compile("hype"),
            Pattern.compile("hard rock"), Pattern.compile("uptempo"), Pattern.compile("electro"), Pattern.compile("speed")
        )),
        Map.entry("peaceful", List.of(
            Pattern.compile("meditation"), Pattern.compile("ambient"), Pattern.compile("spa"),
            Pattern.compile("calm"), Pattern.compile("serene"), Pattern.compile("instrumental"),
            Pattern.compile("piano"), Pattern.compile("harp"), Pattern.compile("new age"), Pattern.compile("zen")
        )),
        Map.entry("dark", List.of(
            Pattern.compile("gothic"), Pattern.compile("industrial"), Pattern.compile("black metal"),
            Pattern.compile("darkwave"), Pattern.compile("horror"), Pattern.compile("eerie"),
            Pattern.compile("brooding"), Pattern.compile("doom"), Pattern.compile("minimal")
        )),
        Map.entry("motivated", List.of(
            Pattern.compile("inspiring"), Pattern.compile("workout"), Pattern.compile("anthem"),
            Pattern.compile("goal"), Pattern.compile("triumph"), Pattern.compile("victory"),
            Pattern.compile("epic"), Pattern.compile("empowering"), Pattern.compile("pop rock")
        )),
        Map.entry("sad", List.of(
            Pattern.compile("heartbreak"), Pattern.compile("emotional"), Pattern.compile("blues"),
            Pattern.compile("lament"), Pattern.compile("slow"), Pattern.compile("crying"),
            Pattern.compile("melancholy"), Pattern.compile("breakup"), Pattern.compile("sentimental")
        )),
        Map.entry("angry", List.of(
            Pattern.compile("punk"), Pattern.compile("hardcore"), Pattern.compile("metalcore"),
            Pattern.compile("thrash"), Pattern.compile("rage"), Pattern.compile("aggressive"),
            Pattern.compile("raw"), Pattern.compile("screamo"), Pattern.compile("industrial")
        )),
        Map.entry("relaxed", List.of(
            Pattern.compile("lounge"), Pattern.compile("jazz"), Pattern.compile("chillout"),
            Pattern.compile("downtempo"), Pattern.compile("lo[- ]?fi"), Pattern.compile("acoustic"),
            Pattern.compile("smooth jazz"), Pattern.compile("easy listening")
        )),
        Map.entry("hopeful", List.of(
            Pattern.compile("uplifting"), Pattern.compile("inspirational"), Pattern.compile("gospel"),
            Pattern.compile("anthem"), Pattern.compile("bright"), Pattern.compile("joy"),
            Pattern.compile("cinematic"), Pattern.compile("victory"), Pattern.compile("orchestral")
        )),
        Map.entry("lonely", List.of(
            Pattern.compile("sad"), Pattern.compile("piano"), Pattern.compile("ballad"),
            Pattern.compile("slow"), Pattern.compile("isolation"), Pattern.compile("ambient"),
            Pattern.compile("minimal"), Pattern.compile("heartbreak"), Pattern.compile("acoustic")
        )),
        Map.entry("mysterious", List.of(
            Pattern.compile("noir"), Pattern.compile("dark jazz"), Pattern.compile("suspense"),
            Pattern.compile("cinematic"), Pattern.compile("eerie"), Pattern.compile("synth"),
            Pattern.compile("industrial"), Pattern.compile("experimental")
        )),
        Map.entry("gritty", List.of(
            Pattern.compile("rap"), Pattern.compile("drill"), Pattern.compile("trap"),
            Pattern.compile("underground"), Pattern.compile("street"), Pattern.compile("hard"),
            Pattern.compile("old school"), Pattern.compile("east coast"), Pattern.compile("grime")
        )),
        Map.entry("groovy", List.of(
            Pattern.compile("funk"), Pattern.compile("soul"), Pattern.compile("r&b"),
            Pattern.compile("groove"), Pattern.compile("disco"), Pattern.compile("jam"),
            Pattern.compile("neo soul"), Pattern.compile("smooth"), Pattern.compile("jazzy")
        )),
        Map.entry("wild", List.of(
            Pattern.compile("party"), Pattern.compile("club"), Pattern.compile("edm"),
            Pattern.compile("festival"), Pattern.compile("dancehall"), Pattern.compile("reggaeton"),
            Pattern.compile("rave"), Pattern.compile("hardstyle"), Pattern.compile("electro")
        )),
        Map.entry("epic", List.of(
            Pattern.compile("orchestral"), Pattern.compile("cinematic"), Pattern.compile("trailer"),
            Pattern.compile("score"), Pattern.compile("heroic"), Pattern.compile("fantasy"),
            Pattern.compile("powerful"), Pattern.compile("soundtrack")
        )),
        Map.entry("focused", List.of(
            Pattern.compile("study"), Pattern.compile("concentration"), Pattern.compile("ambient"),
            Pattern.compile("chillhop"), Pattern.compile("lo[- ]?fi"), Pattern.compile("instrumental"),
            Pattern.compile("minimal"), Pattern.compile("beats")
        )),
        Map.entry("funny", List.of(
            Pattern.compile("parody"), Pattern.compile("comedy"), Pattern.compile("novelty"),
            Pattern.compile("meme"), Pattern.compile("silly"), Pattern.compile("weirdcore"),
            Pattern.compile("quirky"), Pattern.compile("upbeat")
        )),
        Map.entry("spiritual", List.of(
            Pattern.compile("worship"), Pattern.compile("gospel"), Pattern.compile("hymn"),
            Pattern.compile("christian"), Pattern.compile("choir"), Pattern.compile("mantra"),
            Pattern.compile("meditation"), Pattern.compile("sacred")
        )),
        Map.entry("rebellious", List.of(
            Pattern.compile("punk"), Pattern.compile("grunge"), Pattern.compile("metal"),
            Pattern.compile("alternative"), Pattern.compile("rock"), Pattern.compile("indie"),
            Pattern.compile("underground"), Pattern.compile("riot"), Pattern.compile("garage")
        )),
        Map.entry("cozy", List.of(
            Pattern.compile("acoustic"), Pattern.compile("folk"), Pattern.compile("lo[- ]?fi"),
            Pattern.compile("soft"), Pattern.compile("singer[- ]?songwriter"),
            Pattern.compile("bedroom pop"), Pattern.compile("campfire"), Pattern.compile("intimate")
        )),
        Map.entry("adventurous", List.of(
            Pattern.compile("world"), Pattern.compile("folk"), Pattern.compile("celtic"),
            Pattern.compile("tribal"), Pattern.compile("latin"), Pattern.compile("travel"),
            Pattern.compile("afrobeat"), Pattern.compile("fusion")
        )),
        Map.entry("playful", List.of(
            Pattern.compile("upbeat"), Pattern.compile("bubblegum"), Pattern.compile("k[- ]?pop"),
            Pattern.compile("electro pop"), Pattern.compile("indie pop"), Pattern.compile("cute"),
            Pattern.compile("bright"), Pattern.compile("cheerful")
        )),
        Map.entry("mellow", List.of(
            Pattern.compile("smooth"), Pattern.compile("chill"), Pattern.compile("acoustic"),
            Pattern.compile("downtempo"), Pattern.compile("r&b"), Pattern.compile("soft"),
            Pattern.compile("bossa"), Pattern.compile("jazz"), Pattern.compile("calm")
        )),
        Map.entry("party", List.of(
            Pattern.compile("edm"), Pattern.compile("club"), Pattern.compile("dance"),
            Pattern.compile("reggaeton"), Pattern.compile("pop"), Pattern.compile("trap"),
            Pattern.compile("techno"), Pattern.compile("festival"), Pattern.compile("anthem")
        )),
        Map.entry("sentimental", List.of(
            Pattern.compile("nostalgic"), Pattern.compile("love"), Pattern.compile("acoustic"),
            Pattern.compile("piano"), Pattern.compile("emotional"), Pattern.compile("heartfelt"),
            Pattern.compile("crooner")
        )),
        Map.entry("country", List.of(
            Pattern.compile("country"), Pattern.compile("americana"), Pattern.compile("folk"),
            Pattern.compile("bluegrass"), Pattern.compile("outlaw"), Pattern.compile("southern rock")
        )),
        Map.entry("spooky", List.of(
            Pattern.compile("halloween"), Pattern.compile("horror"), Pattern.compile("dark"),
            Pattern.compile("eerie"), Pattern.compile("gothic"), Pattern.compile("synth"),
            Pattern.compile("cinematic"), Pattern.compile("minor")
        )),
        Map.entry("heroic", List.of(
            Pattern.compile("epic"), Pattern.compile("orchestral"), Pattern.compile("soundtrack"),
            Pattern.compile("battle"), Pattern.compile("fantasy"), Pattern.compile("score"),
            Pattern.compile("triumphant")
        )),
        Map.entry("sexy", List.of(
            Pattern.compile("r&b"), Pattern.compile("soul"), Pattern.compile("smooth"),
            Pattern.compile("slow jam"), Pattern.compile("funk"), Pattern.compile("sensual"),
            Pattern.compile("groove"), Pattern.compile("lounge")
        )),
        Map.entry("moody", List.of(
            Pattern.compile("dark pop"), Pattern.compile("alt[- ]?r&b"), Pattern.compile("atmospheric"),
            Pattern.compile("melancholic"), Pattern.compile("synth"), Pattern.compile("minimal")
        )),
        Map.entry("motivational", List.of(
            Pattern.compile("inspiring"), Pattern.compile("anthem"), Pattern.compile("pop rock"),
            Pattern.compile("gospel"), Pattern.compile("epic"), Pattern.compile("uplifting"),
            Pattern.compile("energetic")
        )),
        Map.entry("creative", List.of(
            Pattern.compile("indie"), Pattern.compile("experimental"), Pattern.compile("alt pop"),
            Pattern.compile("eclectic"), Pattern.compile("fusion"), Pattern.compile("avant[- ]?garde")
        )),
        Map.entry("high", List.of(
            Pattern.compile("psychedelic"), Pattern.compile("stoner rock"), Pattern.compile("dub"),
            Pattern.compile("reggae"), Pattern.compile("lo[- ]?fi"), Pattern.compile("trance"),
            Pattern.compile("chill")
        )),
        Map.entry("free", List.of(
            Pattern.compile("reggae"), Pattern.compile("ska"), Pattern.compile("indie folk"),
            Pattern.compile("jam"), Pattern.compile("alternative"), Pattern.compile("acoustic"),
            Pattern.compile("wanderlust")
        ))
    );

    private MoodMatchers() {}
}

