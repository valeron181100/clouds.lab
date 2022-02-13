package yandex.cloud.examples.serverless.todo.utils;

import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.regex.Pattern;

public class TrackBrowser {

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/results?search_query=";

    public String browse(String query) throws IOException, ScriptException, NoSuchMethodException {
        var doc = Jsoup.connect(YOUTUBE_BASE_URL + query).get();
        System.out.println("IN-FUNCTION: Youtube page has been loaded");
        var bodyStr = doc.body().toString();
        var pattern = Pattern.compile("var ytInitialData.+?</script>");
        var matcher = pattern.matcher(bodyStr);
        var isFound = matcher.find();
        var matchedResult = matcher.group(0);
        var script = "function foo() {" +
                matchedResult.substring(0, matchedResult.length() - 9) +
                "return { url: 'https://www.youtube.com' + ytInitialData.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0]" +
                ".itemSectionRenderer.contents[0].videoRenderer.navigationEndpoint.commandMetadata.webCommandMetadata.url, time: " +
                "ytInitialData.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0]" +
                ".itemSectionRenderer.contents[0].videoRenderer.lengthText.simpleText};" +
                "}";
        System.out.println("IN-FUNCTION: Script generated");
        System.out.println("IN-FUNCTION: Script eval started");
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
        engine.eval(script);
        System.out.println("IN-FUNCTION: Script eval finished");
        Invocable inv = (Invocable) engine;
        return inv.invokeFunction("foo").toString();
    }

}
