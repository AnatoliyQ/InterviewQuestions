package ru.anatoliy.electrolux.service;

import com.google.common.base.Stopwatch;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatoliy.electrolux.constants.Globals;

public class Service {

    private static final Pattern PATTERN = Pattern.compile(".*?(\\[.*?])", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private HashMap<String, List<String>> cacheData = new HashMap<>();
    private List<String> resultCache = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private int errorCounter;

    @Setter
    private String logFilePath;

    public void findErrors(Consumer<Integer> consumer) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            Stream<String> stream = Files.lines(Paths.get(logFilePath));
            stream.forEach(s -> {
                Matcher sessionMatcher = PATTERN.matcher(s);
                if (sessionMatcher.find()) {
                    String sessionID = sessionMatcher.group(1);
                    if (s.contains("logs off")) {
                        cacheData.remove(sessionID);
                        return;
                    }
                    if (s.contains("ERROR")) {
                        errorCounter++;
                        resultCache.addAll(cacheData.get(sessionID));
                        resultCache.add(s);
                        resultCache.add(Globals.SEPARATOR);
                        cacheData.remove(sessionID);
                        return;
                    }
                    addToCache(sessionID, s);
                }
            });

        } catch (IOException e) {
            logger.error("Error on read file", e);
        } finally {
            if(!(errorCounter ==0)) {
                writeResult(resultCache);
            }
            consumer.accept(errorCounter);
            cacheData.clear();
            errorCounter = 0;
            stopwatch.stop();
            long time = stopwatch.elapsed(TimeUnit.SECONDS);
            logger.debug("Work time {} seconds", time);
        }
    }

    private void addToCache(String sessionID, String logString) {
        if (cacheData.containsKey(sessionID)) {
            List<String> current = cacheData.get(sessionID);
            if (current.size() >= 3) {
                current.remove(0);
            }
            current.add(logString);
        } else {
            List<String> temp = new ArrayList<>();
            temp.add(logString);
            cacheData.put(sessionID, temp);
        }
    }

    private void writeResult(List<String> result) {
        try (FileWriter writer = new FileWriter(Globals.RESULT_FILE_PATH, true)) {
            for (String line : result) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("Error on write result file", e);
        }
    }
}
