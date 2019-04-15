package ru.anatoliy.electrolux.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.FileAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.anatoliy.electrolux.constants.Globals;

class ServiceTest {

    private Service service = new Service();

    @AfterEach
    void cleanUp() {
        FileUtils.deleteQuietly(new File(Globals.RESULT_FILE_PATH));
    }

    @Test
    void testGenerateResultFileWithErrors() {
        String pathLogFile = "src/test/resources/logWithErrors.txt";
        AtomicInteger errorsCounter = new AtomicInteger(0);

        service.setLogFilePath(pathLogFile);
        service.findErrors(errorsCounter::set);

        new FileAssert(new File("expectedFilePath")).hasSameContentAs(new File("expectedResult.txt"));
        assertEquals(2, errorsCounter.get());
    }

    @Test
    void testGenerateResultWithoutErrors() {
        String pathLogFile = "src/test/resources/logWithoutErrors.txt";
        AtomicInteger errorsCounter = new AtomicInteger(0);

        service.setLogFilePath(pathLogFile);
        service.findErrors(errorsCounter::set);

        File logResultFile = new File(Globals.RESULT_FILE_PATH);
        assertFalse(logResultFile.exists(), "Where are should not be result file");
        assertEquals(0, errorsCounter.get());
    }
}