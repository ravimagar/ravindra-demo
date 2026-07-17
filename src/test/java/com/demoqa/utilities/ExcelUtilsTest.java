package com.demoqa.utilities;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExcelUtilsTest {

    @Test
    public void readExcelDataFallsBackToCsvWhenWorkbookIsMissing() throws IOException {
        Path tempDir = Files.createTempDirectory("excel-utils-test");
        Path csvPath = tempDir.resolve("manual_test_cases.csv");
        Files.writeString(csvPath, "Name,Value\nAlice,42\n", StandardCharsets.UTF_8);

        Path workbookPath = tempDir.resolve("manual_test_cases.xlsx");
        List<String[]> rows = ExcelUtils.readExcelData(workbookPath.toString());

        Assert.assertEquals(rows.size(), 1, "The fallback parser should return the CSV row");
        Assert.assertEquals(rows.get(0)[0], "Alice", "The first CSV column should be preserved");
        Assert.assertEquals(rows.get(0)[1], "42", "The second CSV column should be preserved");
    }
}
