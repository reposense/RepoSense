package reposense.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Scanner;

import static reposense.util.TestUtil.loadResource;

public class TestUtilTest {

    private static final String FILE_CONTENTS = "1";

    private static final String DIRECTORY_NAME = "TestUtilTest";
    private static final String PATH_NO_SPECIAL_CHAR_TEST = DIRECTORY_NAME + "/test.txt";
    private static final String PATH_PLUS_TEST = DIRECTORY_NAME + "/plus+test.txt";
    private static final String PATH_SPACE_TEST = DIRECTORY_NAME + "/symbols +-,.;'[]{}~`!@#$%^&()_-+=test.txt";
    private static final String PATH_SYMBOLS_TEST = DIRECTORY_NAME + "/test";


    @Test
    public void loadResource_validFileWithNoSpecialCharacters_success() throws Exception {
        Path path = loadResource(TestUtilTest.class, PATH_NO_SPECIAL_CHAR_TEST);
        FileReader fileReader = new FileReader(path.toString());
        String actualLine = new Scanner(fileReader).nextLine();
        String expectedLine = FILE_CONTENTS;
        Assert.assertEquals(expectedLine, actualLine);
    }

}
